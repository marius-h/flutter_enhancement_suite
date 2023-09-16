package de.mariushoefler.flutterenhancementsuite.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.psi.DartArrayAccessExpression
import com.jetbrains.lang.dart.psi.DartCallExpression
import com.jetbrains.lang.dart.psi.DartNewExpression
import com.jetbrains.lang.dart.psi.DartReferenceExpression
import de.mariushoefler.flutterenhancementsuite.editor.icons.FontAwesomeIcons
import de.mariushoefler.flutterenhancementsuite.editor.icons.IonIcons
import de.mariushoefler.flutterenhancementsuite.editor.icons.MaterialCommunityIcons
import de.mariushoefler.flutterenhancementsuite.editor.icons.MdiIcons
import javax.swing.Icon

const val HEX_RADIX = 16

class FlutterEditorAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return

        val text = element.text

        if (element is DartReferenceExpression || element is DartArrayAccessExpression) {
            val icon: Icon? = getIconForPrefix(text)
            if (icon != null) attachIcon(element, holder, icon)
        } else {
            val constIconDataText = when (element) {
                is DartNewExpression -> "const IconData("
                is DartCallExpression -> "IconData("
                else -> return
            }

            if (text.startsWith(constIconDataText)) {
                val value = parseNumberFromCallParam(text, constIconDataText).toString()
                val icon: Icon? = getIconForFilename(element, value)
                if (icon != null) {
                    attachIcon(element, holder, icon)
                }
            }
        }
    }

    private fun getIconForFilename(element: PsiElement, value: String) = when {
        element.containingFile.name.contains("ionicons") -> {
            IonIcons.getIconByCode(value)
        }

        element.containingFile.name.contains("font_awesome") -> {
            FontAwesomeIcons.getIconByCode(value)
        }

        element.containingFile.name.contains("material_community_icons") -> {
            MaterialCommunityIcons.getIconByCode(value)
        }

        else -> null
    }

    private fun getIconForPrefix(text: String) = when {
        text.startsWith("Ionicons.") -> {
            findIcon(text, "Ionicons.", IonIcons)
        }

        text.startsWith("MaterialCommunityIcons.") -> {
            findIcon(text, "MaterialCommunityIcons.", MaterialCommunityIcons)
        }

        text.startsWith("FontAwesome.") -> {
            findIcon(text, "FontAwesome.", FontAwesomeIcons)
        }

        text.startsWith("MdiIcons.") -> {
            findIcon(text, "MdiIcons.", MdiIcons)
        }

        else -> null
    }

    private fun parseNumberFromCallParam(callText: String, prefix: String): Int? {
        if (callText.startsWith(prefix) && callText.endsWith(")")) {
            var value = callText.substring(prefix.length, callText.length - 1).trim()
            val index = value.indexOf(',')
            if (index != -1) {
                value = value.substring(0, index)
            }
            try {
                return if (value.startsWith("0x")) {
                    Integer.parseUnsignedInt(value.substring(2), HEX_RADIX)
                } else {
                    Integer.parseUnsignedInt(value)
                }
            } catch (ignored: NumberFormatException) {
            }
        }

        return null
    }

    private fun <T> findIcon(text: String, prefix: String, iconPack: FlutterIcons<T>): Icon? {
        val key = text.substring(prefix.length)

        return iconPack.getIcon(key)
    }

    private fun attachIcon(element: PsiElement, holder: AnnotationHolder, icon: Icon) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
            .gutterIconRenderer(FlutterIconRenderer(icon, element))
            .create()
    }
}

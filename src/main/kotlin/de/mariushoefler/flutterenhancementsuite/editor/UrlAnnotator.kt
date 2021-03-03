package de.mariushoefler.flutterenhancementsuite.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.util.containers.ContainerUtil
import de.mariushoefler.flutterenhancementsuite.utils.isPubPackageName
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile

/**
 * Shows a tooltip which key to press to open up the full pub package documentation
 *
 * @since v1.2
 */
class UrlAnnotator : Annotator {

    companion object {
        private val messageKey = Key.create<String>("hyperlink.message")
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode || !element.containingFile.isPubspecFile()) return

        if (element.text.isPubPackageName()) {
            val message = holder.currentAnnotationSession.getUserData(messageKey) ?: run {
                val message = getMessage()
                holder.currentAnnotationSession.putUserData(messageKey, message)
                message
            }
            element.firstChild?.let {
                holder.newAnnotation(HighlightSeverity.INFORMATION, message).range(it).create()
            }
        }
    }

    private fun getMessage(): String {
        val shortcuts = KeymapManager.getInstance().activeKeymap.getShortcuts(IdeActions.ACTION_QUICK_JAVADOC)
        return ContainerUtil.find(shortcuts) { shortcut -> shortcut.isKeyboard }?.let { keyboardShortcut ->
            val shortcutText = KeymapUtil.getShortcutText(keyboardShortcut)
            return "Press $shortcutText to open full documentation"
        } ?: "Open full documentation"
    }
}

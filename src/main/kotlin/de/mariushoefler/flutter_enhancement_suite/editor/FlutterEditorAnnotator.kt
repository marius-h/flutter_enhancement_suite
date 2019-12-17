package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.psi.DartArrayAccessExpression
import com.jetbrains.lang.dart.psi.DartCallExpression
import com.jetbrains.lang.dart.psi.DartNewExpression
import com.jetbrains.lang.dart.psi.DartReferenceExpression
import de.mariushoefler.flutter_enhancement_suite.editor.icons.FontAwesomeIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.IonIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.MaterialCommunityIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.MdiIcons
import javax.swing.Icon


class FlutterEditorAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (holder.isBatchMode) return

		val text = element.text

		if (element is DartReferenceExpression || element is DartArrayAccessExpression) {
			val icon: Icon? = when {
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

			if (icon != null) attachIcon(element, holder, icon)
		} else {
			val constIconDataText = when (element) {
				is DartNewExpression -> "const IconData("
				is DartCallExpression -> "IconData("
				else -> return
			}

			if (text.startsWith(constIconDataText)) {
				val value = parseNumberFromCallParam(text, constIconDataText).toString()

				var icon: Icon? = null

//				element.containingFile.name

				when {
					element.containingFile.name.contains("ionicons") -> {
						icon = IonIcons.getIconByCode(value)
					}
					element.containingFile.name.contains("font_awesome") -> {
						icon = FontAwesomeIcons.getIconByCode(value)
					}
					element.containingFile.name.contains("material_community_icons") -> {
						icon = MaterialCommunityIcons.getIconByCode(value)
					}
				}

				if (icon != null) {
					attachIcon(element, holder, icon)
				}
				//				if (value != null) {


////					val hex = java.lang.Long.toHexString(value.toLong())
//					val hex: String = value.toString()
//
//					var icon = MaterialCommunityIcons.getIconByCode(hex)
//					if (icon != null) {
//						attachIcon(element, holder, icon)
//					} else {
//						icon = FontAwesomeIcons.getIconByCode(hex)
//						if (icon != null) {
//							attachIcon(element, holder, icon)
//						} else {
//							icon = IonIcons.getIconByCode(hex)
//							if (icon != null) {
//								attachIcon(element, holder, icon)
//							}
//						}
//					}
//				}
			}
		}
	}

	private fun parseNumberFromCallParam(callText: String, prefix: String): Int? {
		if (callText.startsWith(prefix) && callText.endsWith(")")) {
			var value = callText.substring(prefix.length, callText.length - 1).trim()
			val index = value.indexOf(',')
			if (index != -1) {
				value = value.substring(0, index)
			}
			try {
				return if (value.startsWith("0x"))
					Integer.parseUnsignedInt(value.substring(2), 16)
				else
					Integer.parseUnsignedInt(value)
			} catch (ignored: NumberFormatException) {
			}

		}

		return null
	}

	private fun <T> findIcon(text: String, prefix: String, iconPack: AbstractFlutterIcons<T>): Icon? {
		val key = text.substring(prefix.length)

		return iconPack.getIcon(key)
	}

	private fun attachIcon(element: PsiElement, holder: AnnotationHolder, icon: Icon) {
		try {
			val annotation = holder.createInfoAnnotation(element, null)
			annotation.gutterIconRenderer = FlutterIconRenderer(icon, element)
		} catch (e: Exception) {
		}
	}
}
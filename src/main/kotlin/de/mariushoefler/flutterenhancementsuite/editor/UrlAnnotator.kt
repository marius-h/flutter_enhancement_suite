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
 * Shows a tooltip for pub packages to press `Ctrl+Q` to open up the documentation
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
			var message = holder.currentAnnotationSession.getUserData(messageKey)
			if (message == null) {
				message = getMessage()
				holder.currentAnnotationSession.putUserData(messageKey, message)
			}
			element.firstChild?.let {
				holder.newAnnotation(HighlightSeverity.INFORMATION, message).range(it).create()
			}
		}
	}

	private fun getMessage(): String {
		var message = "Open documentation"
		val shortcuts = KeymapManager.getInstance().activeKeymap.getShortcuts(IdeActions.ACTION_QUICK_JAVADOC)
		var shortcutText = ""
		val keyboardShortcut = ContainerUtil.find(shortcuts) { shortcut -> shortcut.isKeyboard }
		if (keyboardShortcut != null) {
			if (shortcutText.isNotEmpty()) shortcutText += ", "
			shortcutText += KeymapUtil.getShortcutText(keyboardShortcut)
		}
		if (shortcutText.isNotEmpty()) {
			message += " ($shortcutText)"
		}
		return message
	}
}

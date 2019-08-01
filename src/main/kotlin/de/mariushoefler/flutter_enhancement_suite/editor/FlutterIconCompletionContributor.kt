package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.util.ui.EmptyIcon
import com.jetbrains.lang.dart.ide.completion.DartCompletionExtension
import com.intellij.util.ui.JBUI
import com.jetbrains.lang.dart.ide.completion.DartServerCompletionContributor
import de.mariushoefler.flutter_enhancement_suite.editor.icons.FontAwesomeIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.IonIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.MaterialCommunityIcons
import javafx.scene.paint.Material
import org.apache.commons.lang.StringUtils
import org.dartlang.analysis.server.protocol.CompletionSuggestion
import org.dartlang.analysis.server.protocol.Element
import java.util.*
import javax.swing.Icon


class FlutterIconCompletionContributor : DartCompletionExtension() {

	companion object {
		private const val ICON_SIZE = 16
		private val EMPTY_ICON = JBUI.scale(EmptyIcon.create(ICON_SIZE))
	}

	override fun createLookupElement(project: Project, suggestion: CompletionSuggestion): LookupElementBuilder? {
		val icon = findIcon(suggestion)

		if (icon != null) {
			return DartServerCompletionContributor
					.createLookupElement(project, suggestion).withTypeText("", icon, false)
					.withTypeIconRightAligned(true)
		}

		return null
	}

	private fun findIcon(suggestion: CompletionSuggestion): Icon? {
		val element = suggestion.element
		if (element != null) {
			val returnType = element.returnType
			if (!StringUtils.isEmpty(returnType)) {
				val name = element.name
				if (name != null) {
					val declaringType = suggestion.declaringType
					when {
						Objects.equals(declaringType, "FontAwesome") -> {
							return FontAwesomeIcons.getIcon(name) ?: EMPTY_ICON
						}
						Objects.equals(declaringType, "Ionicons") -> {
							return IonIcons.getIcon(name) ?: EMPTY_ICON
						}
						Objects.equals(declaringType, "MaterialCommunityIcons") -> {
							return MaterialCommunityIcons.getIcon(name) ?: EMPTY_ICON
						}
					}
				}
			}
		}

		return null
	}


}
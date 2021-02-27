package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.jetbrains.lang.dart.ide.completion.DartCompletionExtension
import com.jetbrains.lang.dart.ide.completion.DartServerCompletionContributor
import de.mariushoefler.flutter_enhancement_suite.editor.icons.FontAwesomeIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.IonIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.MaterialCommunityIcons
import de.mariushoefler.flutter_enhancement_suite.editor.icons.MdiIcons
import org.apache.commons.lang.StringUtils
import org.dartlang.analysis.server.protocol.CompletionSuggestion
import javax.swing.Icon


class FlutterIconCompletionContributor : DartCompletionExtension() {

	override fun createLookupElement(project: Project, suggestion: CompletionSuggestion): LookupElementBuilder? {
		val icon = findIcon(suggestion) ?: return null

		return DartServerCompletionContributor
			.createLookupElement(project, suggestion)
			.withTypeText("", icon, false)
			.withTypeIconRightAligned(true)
	}

	private fun findIcon(suggestion: CompletionSuggestion): Icon? {
		val element = suggestion.element
		if (element != null) {
			val returnType = element.returnType
			if (!StringUtils.isEmpty(returnType)) {
				element.name?.let { name ->
					return when (suggestion.declaringType) {
						"FontAwesome" -> {
							FontAwesomeIcons.getIcon(name)
						}
						"Ionicons" -> {
							IonIcons.getIcon(name)
						}
						"MaterialCommunityIcons" -> {
							MaterialCommunityIcons.getIcon(name)
						}
						"MdiIcons" -> {
							MdiIcons.getIcon(name)
						}
						else -> null
					}
				}
			}
		}

		return null
	}


}

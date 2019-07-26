package de.mariushoefler.flutter_enhancement_suite.livetemplates

import com.intellij.codeInsight.template.impl.LiveTemplateLookupElement
import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.ExternalDocumentationProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

class LiveTemplatePreviewProvider : AbstractDocumentationProvider(), ExternalDocumentationProvider {

	override fun getDocumentationElementForLookupItem(psiManager: PsiManager, obj: Any?, element: PsiElement?): PsiElement? {
		if (obj !is LiveTemplateLookupElementImpl) {
			return null
		}

		return LiveTemplateElement(psiManager, obj)
	}

	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		if (element !is LiveTemplateElement) {
			return null
		}
		val liveTemplateLookupElement = element.element as LiveTemplateLookupElementImpl
		val template = liveTemplateLookupElement.template

		var templateHtml = StringUtil.escapeXmlEntities(template.string)

		templateHtml = templateHtml.replace(Regex("\\\$\\w+\\\$")) { matchResult ->
			if (matchResult.value == "\$END\$") {
				return@replace ""
			}
			"<em style=\"background-color:black;color:white;\">${matchResult.value.replace("$", "")}</em>"
		}

		val result = StringBuilder()
		result.append("<html>")
		result.append("<h2>Template Preview</h2>")
		result.append("<pre>")
		result.append(templateHtml)
		result.append("</pre>")
		result.append("</html>")

		return result.toString()
	}

	override fun fetchExternalDocumentation(project: Project?, element: PsiElement?, docUrls: MutableList<String>?): String? = null

	override fun hasDocumentationFor(element: PsiElement?, originalElement: PsiElement?): Boolean {
		return element is LiveTemplateLookupElement
	}

	override fun canPromptToConfigureDocumentation(element: PsiElement?): Boolean {
		return false
	}

	override fun promptToConfigureDocumentation(element: PsiElement?) {}
}
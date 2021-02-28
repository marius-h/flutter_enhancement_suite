package de.mariushoefler.flutterenhancementsuite.templates.livetemplates

import com.intellij.codeInsight.template.impl.LiveTemplateLookupElementImpl
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager

class LiveTemplatePreviewProvider : AbstractDocumentationProvider() {

    companion object {
        val REPLACES_REFS = listOf("&lt;", "&gt;", "&amp;", "&#39;", "&quot;")
        val REPLACES_DISP = listOf("<", ">", "&", "'", "\"")
    }

    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager,
        obj: Any?,
        element: PsiElement?
    ): PsiElement? {
        return if (obj !is LiveTemplateLookupElementImpl) null
        else LiveTemplateElement(psiManager, obj)
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element !is LiveTemplateElement) {
            return null
        }
        val liveTemplateLookupElement = element.element as LiveTemplateLookupElementImpl
        val template = liveTemplateLookupElement.template

        var templateHtml = StringUtil.replace(template.string, REPLACES_DISP, REPLACES_REFS)

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
}

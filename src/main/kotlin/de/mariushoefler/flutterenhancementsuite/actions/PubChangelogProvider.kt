package de.mariushoefler.flutterenhancementsuite.actions

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.utils.PubApi
import de.mariushoefler.flutterenhancementsuite.utils.REGEX_DEPENDENCY
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile

/**
 * Shows the changelog of a package when hovering over its version in pubspec.yaml
 *
 * @since v1.4
 */
class PubChangelogProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement?): String {
        return findPackageNameAndGenerateDoc(element) ?: "Changelog not available"
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return findPackageNameAndGenerateDoc(element)
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        contextElement?.let {
            if (file.isPubspecFile() && it.parent.parent.text.matches(REGEX_DEPENDENCY.toRegex())
            ) {
                return contextElement
            }
        }
        return null
    }

    private fun findPackageNameAndGenerateDoc(element: PsiElement): String? {
        return element.parent.parent.firstChild.text?.let {
            PubApi.getPackageChangelog(it)
        }
    }
}

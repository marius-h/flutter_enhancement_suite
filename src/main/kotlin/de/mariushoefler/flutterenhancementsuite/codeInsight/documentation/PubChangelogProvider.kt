package de.mariushoefler.flutterenhancementsuite.codeInsight.documentation

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.utils.PubApi
import de.mariushoefler.flutterenhancementsuite.utils.REGEX_DEPENDENCY
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile
import io.flutter.utils.FlutterModuleUtils

val changelogKey = Key<String>("changelog")

/**
 * Shows the changelog of a package when hovering over its version in pubspec.yaml
 *
 * @since v1.4
 */
class PubChangelogProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement?): String? {
        return findPackageNameAndGenerateChangelog(element)
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return findPackageNameAndGenerateChangelog(element)
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        return contextElement?.let {
            return if (file.isPubspecFile() && it.parent?.parent?.text?.matches(REGEX_DEPENDENCY.toRegex()) == true) {
                contextElement
            } else null
        }
    }

    private fun findPackageNameAndGenerateChangelog(element: PsiElement): String? {
        val cachedValue = element.getUserData(changelogKey)
        if (cachedValue != null) return cachedValue

        if (!FlutterModuleUtils.isInFlutterModule(element)) return null

        val changelogData = element.parent?.parent?.firstChild?.text?.let {
            PubApi.getPackageChangelog(it)
        } ?: "Changelog not available"
        element.putUserData(changelogKey, changelogData)
        return changelogData
    }
}

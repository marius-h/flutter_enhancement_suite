package de.mariushoefler.flutterenhancementsuite.codeInsight

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement
import de.mariushoefler.flutterenhancementsuite.utils.PubApi
import de.mariushoefler.flutterenhancementsuite.utils.isPubPackageName
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile

class PubDocumentationProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return element?.parent?.parent?.firstChild?.text?.let {
            PubApi.getPackageDoc(it, true)
        }
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return if (element is SuggestionElement) {
            PubApi.getPackageDoc(element.name, true)
        } else {
            element.parent?.text?.let {
                if (it.isPubPackageName()) {
                    // TODO: unshorten again when performance was improved
                    PubApi.getPackageDoc(element.text, true)
                } else null
            }
        }
    }

    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager,
        `object`: Any?,
        element: PsiElement
    ): PsiElement? {
        if (`object` is String) {
            return SuggestionElement(psiManager, `object`)
        }
        return null
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        if (file.isPubspecFile()) {
            return contextElement
        }
        return null
    }

    inner class SuggestionElement(private val psiManager: PsiManager, private val element: String) : FakePsiElement() {
        override fun getParent(): PsiElement? = null

        override fun isValid() = true

        override fun getContainingFile(): PsiFile {
            return PsiFileFactory
                .getInstance(project)
                .createFileFromText("hoge.txt", FileTypes.PLAIN_TEXT, "")
        }

        override fun getManager() = psiManager

        override fun getName() = element.split(":")[0]
    }
}

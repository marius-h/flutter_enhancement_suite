package de.mariushoefler.flutterenhancementsuite.codeInsight.documentation

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement
import de.mariushoefler.flutterenhancementsuite.utils.PubApi
import de.mariushoefler.flutterenhancementsuite.utils.isPubPackageName
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile

val shortDocKey = Key<String>("short_doc")
val longDocKey = Key<String>("long_doc")

class PubDocumentationProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return element?.parent?.parent?.firstChild?.text?.let {
            getShortDoc(element, it)
        }
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return if (element is SuggestionElement) {
            getShortDoc(element, element.name)
        } else {
            getLongDoc(element)
        }
    }

    private fun getShortDoc(element: PsiElement, packageName: String): String? {
        val cachedValue = element.getUserData(shortDocKey)
        if (cachedValue != null) return cachedValue
        val shortDoc = PubApi.getPackageDoc(packageName, true)
        element.putUserData(shortDocKey, shortDoc)
        return shortDoc
    }

    private fun getLongDoc(element: PsiElement): String? {
        val cachedValue = element.getUserData(longDocKey)
        if (cachedValue != null) return cachedValue
        val longDoc = element.parent?.text?.let {
            if (it.isPubPackageName()) {
                PubApi.getPackageDoc(element.text, false)
            } else null
        }
        element.putUserData(longDocKey, longDoc)
        return longDoc
    }

    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager,
        `object`: Any?,
        element: PsiElement
    ): PsiElement? {
        return if (`object` is String) {
            SuggestionElement(psiManager, `object`)
        } else null
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        return if (file.isPubspecFile()) {
            contextElement
        } else null
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

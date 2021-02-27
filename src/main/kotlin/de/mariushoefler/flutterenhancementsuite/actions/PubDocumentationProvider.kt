package de.mariushoefler.flutterenhancementsuite.actions

import com.intellij.lang.documentation.AbstractDocumentationProvider
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

class PubDocumentationProvider : AbstractDocumentationProvider() {
	override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String {
		return "Quick Info"
	}

	override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): MutableList<String>? {
		return null
	}

	override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
		println("element = $element")
		if (originalElement != null && !originalElement.containingFile.isPubspecFile()) {
			return null
		}
		return if (element is SuggestionElement) {
			PubApi.getPackageDoc(element.name, true)
		} else {
			element.parent?.text?.let {
				if (it.isPubPackageName() && element.containingFile.isPubspecFile()) {
					PubApi.getPackageDoc(element.text)
				} else null
			}
		}
	}

	override fun getDocumentationElementForLookupItem(
		psiManager: PsiManager,
		`object`: Any?,
		element: PsiElement
	): PsiElement? {
		if (`object` is String && element.containingFile.isPubspecFile()) {
			return SuggestionElement(psiManager, `object`)
		}
		return null
	}

	override fun getDocumentationElementForLink(
		psiManager: PsiManager?,
		link: String?,
		context: PsiElement?
	): PsiElement? {
		return null
	}

	override fun getCustomDocumentationElement(
		editor: Editor,
		file: PsiFile,
		contextElement: PsiElement?
	): PsiElement? {
		return contextElement
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

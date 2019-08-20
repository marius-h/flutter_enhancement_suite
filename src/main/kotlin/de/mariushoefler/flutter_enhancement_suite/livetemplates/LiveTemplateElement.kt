package de.mariushoefler.flutter_enhancement_suite.livetemplates

import com.intellij.codeInsight.template.impl.LiveTemplateLookupElement
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement


class LiveTemplateElement(private val psiManager: PsiManager, val element: LiveTemplateLookupElement) : FakePsiElement() {
	override fun getParent(): PsiElement? = null

	override fun isValid() = true

	override fun getContainingFile(): PsiFile {
		return PsiFileFactory
				.getInstance(project)
				.createFileFromText("hoge.txt", FileTypes.PLAIN_TEXT, "")
	}

	override fun getManager(): PsiManager {
		return psiManager
	}

	override fun getName(): String? = element.lookupString
}

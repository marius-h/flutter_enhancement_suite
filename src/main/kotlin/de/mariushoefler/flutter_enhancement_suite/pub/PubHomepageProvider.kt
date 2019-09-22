package de.mariushoefler.flutter_enhancement_suite.pub

import com.intellij.openapi.paths.WebReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutter_enhancement_suite.utils.isPubPackageName
import de.mariushoefler.flutter_enhancement_suite.utils.isPubspecFile

class PubHomepageProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
		println("PubHomepageProvider.getReferencesByElement: ${element.text}")
		if (!(element.containingFile.isPubspecFile() && element.text.isPubPackageName())) return emptyArray()

		println("Add reference to ${element.text}")

		return arrayOf(WebReference(element, element.textRangeInParent, "https://github.com/"))
	}
}
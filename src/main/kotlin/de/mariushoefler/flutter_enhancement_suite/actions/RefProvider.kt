package de.mariushoefler.flutter_enhancement_suite.actions

import com.intellij.openapi.paths.WebReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutter_enhancement_suite.utils.isPubPackageName
import de.mariushoefler.flutter_enhancement_suite.utils.isPubspecFile

// TODO: implement ctrl+click to open package's homepage directly
class RefProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
		println("getReferencesByElement")
		if (element.containingFile.isPubspecFile() && element.text.isPubPackageName()) {
//			val text = element.firstChild.text
//			println(text)
			arrayOf(WebReference(element, element.textRangeInParent, "https://github.com/"))
		}
		return arrayOf()
	}
}
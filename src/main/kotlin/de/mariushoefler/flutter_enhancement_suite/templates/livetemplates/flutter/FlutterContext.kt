package de.mariushoefler.flutter_enhancement_suite.templates.livetemplates.flutter

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

public class FlutterContext : TemplateContextType("FLUTTER", "Flutter") {

	override fun isInContext(file: PsiFile, offset: Int): Boolean {
		return file.name.endsWith(".dart")
	}
}
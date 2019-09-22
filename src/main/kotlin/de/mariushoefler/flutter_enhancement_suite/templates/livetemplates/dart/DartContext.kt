package de.mariushoefler.flutter_enhancement_suite.templates.livetemplates.dart

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

public class DartContext : TemplateContextType("DART", "Dart") {

	override fun isInContext(file: PsiFile, offset: Int): Boolean {
		return file.name.endsWith(".dart")
	}
}
package de.mariushoefler.flutter_enhancement_suite.templates.postfixtemplates

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class FlutterPostfixTemplateProvider : PostfixTemplateProvider {
	override fun getTemplates() = setOf<PostfixTemplate>()

	override fun isTerminalSymbol(currentChar: Char): Boolean {
		return currentChar == '.' || currentChar == '!'
	}

	override fun afterExpand(file: PsiFile, editor: Editor) {
	}

	override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int) = copyFile

	override fun preExpand(file: PsiFile, editor: Editor) {
	}
}

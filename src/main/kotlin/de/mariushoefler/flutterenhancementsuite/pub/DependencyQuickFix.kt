package de.mariushoefler.flutterenhancementsuite.pub

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import de.mariushoefler.flutterenhancementsuite.utils.FlutterProjectUtils

class DependencyQuickFix(
    psiElement: PsiElement,
    private val latestVersion: String,
    private val forcePubGet: Boolean
) : LocalQuickFixOnPsiElement(psiElement) {
    override fun getFamilyName(): String = "Update package"

    override fun getText(): String = "Update package" + if (!forcePubGet) " without running pub get" else ""

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val factory = JavaPsiFacade.getInstance(project).elementFactory
        val psiExpression = factory.createDummyHolder(
            "^$latestVersion",
            IElementType("text", Language.findLanguageByID("yaml")),
            null
        )
        startElement.replace(psiExpression)

        if (forcePubGet) FlutterProjectUtils.runPackagesGet(file.virtualFile)
    }
}

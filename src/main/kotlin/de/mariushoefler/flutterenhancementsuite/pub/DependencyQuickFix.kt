package de.mariushoefler.flutterenhancementsuite.pub

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.utils.FlutterProjectUtils
import org.jetbrains.yaml.YAMLElementGenerator

class DependencyQuickFix(
    psiElement: PsiElement,
    private val latestVersion: String,
    private val forcePubGet: Boolean
) : LocalQuickFixOnPsiElement(psiElement) {
    override fun getFamilyName(): String = "Update package"

    override fun getText(): String = "Update package" + if (!forcePubGet) " without running pub get" else ""

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val expression = YAMLElementGenerator(project).createDummyYamlWithText("^$latestVersion").firstChild

        runUndoTransparentWriteAction {
            startElement.replace(expression)
        }

        if (forcePubGet) FlutterProjectUtils.runPackagesGet(file.virtualFile, project)
    }
}

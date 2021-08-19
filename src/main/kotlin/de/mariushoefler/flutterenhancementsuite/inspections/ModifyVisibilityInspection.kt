package de.mariushoefler.flutterenhancementsuite.inspections

import com.intellij.codeInsight.FileModificationService
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.RefactoringQuickFix
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiNamedElement
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.RefactoringActionHandlerFactory
import com.intellij.refactoring.RefactoringFactory

const val PRIVATE_MODIFIER = "_"

class ModifyVisibilityInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor = VisibilityModifierVisitor() { element ->
        holder.registerProblem(
            element,
            "blablabla",
            ProblemHighlightType.INFORMATION,
            if (element.isPrivate()) MakePublicFix() else MakePrivateFix()
        )
    }

    class MakePrivateFix : VisibilityModifierQuickFix() {
        override fun getName(): String = "Make private"

        override fun getModifiedName(element: PsiElement): String = PRIVATE_MODIFIER + element.text
    }

    class MakePublicFix : VisibilityModifierQuickFix() {
        override fun getName(): String = "Make public"

        override fun getModifiedName(element: PsiElement): String = element.text.removePrefix(PRIVATE_MODIFIER)
    }

    abstract class VisibilityModifierQuickFix : RefactoringQuickFix {
        override fun getFamilyName(): String = name

        override fun getHandler(): RefactoringActionHandler {
            return RefactoringActionHandlerFactory.getInstance().createRenameHandler()
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            if (!FileModificationService.getInstance().preparePsiElementForWrite(descriptor.psiElement)) return

            val element = descriptor.psiElement

            RefactoringFactory.getInstance(project).createRename(element, getModifiedName(element)).apply {
                isPreviewUsages = false
            }.run()
        }

        protected abstract fun getModifiedName(element: PsiElement): String
    }
}

private class VisibilityModifierVisitor(val onModifieableElementVisit: (e: PsiNamedElement) -> Unit) :
    PsiElementVisitor() {
    override fun visitElement(element: PsiElement) {
        if (element is PsiNamedElement) {
            onModifieableElementVisit(element)
        }
        super.visitElement(element)
    }
}

private fun PsiNamedElement.isPrivate(): Boolean {
    return this.text.startsWith(PRIVATE_MODIFIER)
}

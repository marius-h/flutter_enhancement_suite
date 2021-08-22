package de.mariushoefler.flutterenhancementsuite.intentions

import com.intellij.CommonBundle
import com.intellij.codeInsight.FileModificationService
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.jetbrains.lang.dart.assists.AssistUtils
import com.jetbrains.lang.dart.assists.DartSourceEditException
import com.jetbrains.lang.dart.ide.refactoring.ServerRenameRefactoring
import com.jetbrains.lang.dart.ide.refactoring.status.RefactoringStatus
import com.jetbrains.lang.dart.psi.DartComponentName
import com.jetbrains.lang.dart.psi.DartReferenceExpression
import com.jetbrains.lang.dart.psi.DartSimpleType
import com.jetbrains.lang.dart.psi.DartType

const val PRIVATE_MODIFIER = "_"

/**
 * Provides quick fixes for modifying the visibility of classes, functions and variables
 *
 * @author Marius Höfler
 * @since v1.6.0
 */
abstract class AbstractModifyVisibilityIntentionAction : PsiElementBaseIntentionAction(), IntentionAction {
    override fun getFamilyName(): String = text

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val parent = element.parent?.parent
        val isReference =
            parent is DartReferenceExpression && parent.parent !is DartType && parent.parent !is DartSimpleType
        return isAvailable(element) && (parent is DartComponentName || isReference)
    }

    override fun startInWriteAction() = true

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val virtualFile = element.containingFile?.virtualFile
        if (virtualFile == null || !FileModificationService.getInstance()
                .preparePsiElementForWrite(element)
        ) return

        // Create the refactoring.
        val refactoring = ServerRenameRefactoring(project, virtualFile, element.textOffset, 0)
        // Validate initial status.
        val initialStatus: RefactoringStatus = refactoring.checkInitialConditions() ?: return
        if (initialStatus.hasError()) {
            initialStatus.message?.let {
                CommonRefactoringUtil.showErrorHint(project, editor, it, CommonBundle.getErrorTitle(), null)
            }
            return
        }
        refactoring.setNewName(getModifiedName(element))
        doRenameRefactoring(project, refactoring, refactoring.potentialEdits)?.let { error ->
            Messages.showErrorDialog(project, error, CommonBundle.getErrorTitle())
        }
    }

    protected abstract fun getModifiedName(element: PsiElement): String

    protected abstract fun isAvailable(element: PsiElement): Boolean

    private fun doRenameRefactoring(
        project: Project,
        refactoring: ServerRenameRefactoring,
        excludedIds: Set<String>
    ): String? {
        refactoring.checkFinalConditions()?.let { finalStatus ->
            if (finalStatus.hasError()) {
                return finalStatus.message
            }

            refactoring.change?.let { change ->
                return WriteAction.compute<String?, RuntimeException> {
                    try {
                        AssistUtils.applySourceChange(project, change, false, excludedIds)
                    } catch (e: DartSourceEditException) {
                        return@compute e.message
                    }
                    null
                }
            }
        }
        return null
    }
}


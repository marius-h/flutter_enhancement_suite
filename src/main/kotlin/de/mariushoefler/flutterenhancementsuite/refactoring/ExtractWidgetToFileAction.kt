package de.mariushoefler.flutterenhancementsuite.refactoring

import com.intellij.CommonBundle
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.refactoring.util.CommonRefactoringUtil
import de.mariushoefler.flutterenhancementsuite.utils.ifLet
import io.flutter.FlutterUtils
import io.flutter.refactoring.ExtractWidgetRefactoring

const val NAME_FIELD_WIDTH = 200
const val SMALL_PADDING = 4

/**
 * Extract a widget to a seperate file
 *
 * @since v1.3
 */
class ExtractWidgetToFileAction : DumbAwareAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(event: AnActionEvent) {
        event.dataContext.run {
            ifLet(
                getData(PlatformDataKeys.PROJECT),
                getData(PlatformDataKeys.VIRTUAL_FILE),
                getData(PlatformDataKeys.EDITOR),
                getData(PlatformDataKeys.CARET)
            ) { (project, file, editor, caret) ->
                createExtractDialog(caret as Caret, project as Project, file as VirtualFile, editor as Editor)
            }
        }
    }

    private fun createExtractDialog(caret: Caret, project: Project, file: VirtualFile, editor: Editor) {
        val offset = caret.selectionStart
        val length = caret.selectionEnd - offset
        val refactoring = ExtractWidgetRefactoring(project, file, offset, length)

        // Validate the initial status.
        val initialStatus = refactoring.checkInitialConditions() ?: return
        if (initialStatus.hasError()) {
            initialStatus.message?.let { message ->
                CommonRefactoringUtil.showErrorHint(project, editor, message, CommonBundle.getErrorTitle(), null)
            }
            return
        }

        ExtractWidgetDialog(project, file, editor, refactoring).show()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = isVisibleFor(e)
        super.update(e)
    }

    private fun isVisibleFor(e: AnActionEvent): Boolean {
        val dataContext = e.dataContext
        val file = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
        return !(file == null || !FlutterUtils.isDartFile(file))
    }
}

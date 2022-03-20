package de.mariushoefler.flutterenhancementsuite.editor.linter

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.jetbrains.yaml.psi.YAMLFile

/**
 * Provides the Linter Editor to files named "analysis_options.yaml"
 *
 * @since v1.3
 */
class LinterEditorProvider : FileEditorProvider, DumbAware {
    companion object {
        const val ID = "linter-rules-editor"
    }

    override fun accept(project: Project, file: VirtualFile): Boolean {
        val psiFile = PsiManager.getInstance(project).findFile(file)
        return psiFile is YAMLFile && psiFile.name.contains("analysis_options")
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return LinterEditor(project, file)
    }

    override fun getEditorTypeId() = ID

    override fun getPolicy() = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}

package de.mariushoefler.flutter_enhancement_suite.editor.linter

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.refactoring.toPsiFile
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
		val psiFile = file.toPsiFile(project)
		return psiFile is YAMLFile && psiFile.name.contains("analysis_options")
	}

	override fun createEditor(project: Project, file: VirtualFile): FileEditor {
		return LinterEditor(project, file)
	}

	override fun disposeEditor(editor: FileEditor) {
		println("DISPOSE EDITOR")
		super.disposeEditor(editor)
	}

	override fun getEditorTypeId() = ID

	override fun getPolicy() = FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}
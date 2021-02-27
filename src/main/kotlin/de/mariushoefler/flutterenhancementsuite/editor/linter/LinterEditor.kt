package de.mariushoefler.flutterenhancementsuite.editor.linter

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ui.UIUtil
import java.beans.PropertyChangeListener
import javax.swing.JComponent

/**
 * Editor for modifying project's analysis_options.yaml
 *
 * @since v1.3
 */
class LinterEditor(val project: Project, file: VirtualFile) : UserDataHolderBase(), FileEditor {

	companion object {
		const val NAME = "Linter Rules Editor"
	}

	init {
		UIUtil.invokeAndWaitIfNeeded<Unit> {
			myPanel = LinterViewPanel(project, this)
		}
	}

	private lateinit var myPanel: LinterViewPanel

	override fun getComponent(): JComponent {
		return myPanel.myContainer
	}

	override fun getPreferredFocusedComponent(): JComponent {
		return myPanel.getPreferredFocusedComponent()
	}

	override fun getName(): String {
		return NAME
	}

	override fun getState(level: FileEditorStateLevel): FileEditorState {
		return FileEditorState.INSTANCE
	}

	override fun setState(state: FileEditorState) {}

	override fun isModified() = false

	override fun isValid() = true

	override fun selectNotify() {}

	override fun deselectNotify() {}

	override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

	override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

	override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? = null

	override fun getCurrentLocation(): FileEditorLocation? = null

	override fun getStructureViewBuilder(): StructureViewBuilder? = null

	override fun dispose() {}

	override fun toString(): String {
		return "LinterEditor ${System.identityHashCode(this)}"
	}
}

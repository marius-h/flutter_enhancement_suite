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
class LinterEditor(val project: Project, private val virtualFile: VirtualFile?) : FileEditorImpl() {
    private lateinit var myPanel: LinterViewPanel

    init {
        UIUtil.invokeAndWaitIfNeeded<Unit> {
            myPanel = LinterViewPanel(project, this)
        }
    }

    override fun getName() = "Linter Rules Editor"

    override fun getComponent() = myPanel.myContainer

    override fun getPreferredFocusedComponent(): JComponent {
        return myPanel.getPreferredFocusedComponent()
    }

    override fun toString(): String {
        return "LinterEditor ${System.identityHashCode(this)}"
    }

    override fun getFile() = virtualFile
}

abstract class FileEditorImpl : UserDataHolderBase(), FileEditor {
    override fun getState(level: FileEditorStateLevel): FileEditorState {
        return FileEditorState.INSTANCE
    }

    override fun setState(state: FileEditorState) {}

    override fun isModified() = false

    override fun isValid() = true

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? = null

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun getStructureViewBuilder(): StructureViewBuilder? = null

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun dispose() {}
}

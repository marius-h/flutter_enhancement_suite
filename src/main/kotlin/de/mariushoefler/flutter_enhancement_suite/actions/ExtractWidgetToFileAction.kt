package de.mariushoefler.flutter_enhancement_suite.actions

import com.intellij.CommonBundle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.util.ui.JBUI
import com.jetbrains.lang.dart.ide.actions.DartStyleAction
import com.jetbrains.lang.dart.ide.refactoring.ServerRefactoringDialog
import com.jetbrains.lang.dart.util.DartElementGenerator
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import de.mariushoefler.flutter_enhancement_suite.utils.toSnakeCase
import io.flutter.FlutterUtils
import io.flutter.refactoring.ExtractWidgetRefactoring
import org.jetbrains.kotlin.idea.refactoring.toPsiFile
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import de.mariushoefler.flutter_enhancement_suite.utils.createImportStatement

/**
 * Extract a widget to a seperate file
 *
 * @since v1.3
 */
class ExtractWidgetToFileAction : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = event.dataContext
        val project = dataContext.getData<Project>(PlatformDataKeys.PROJECT)
        val file = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
        val editor = dataContext.getData(PlatformDataKeys.EDITOR)
        val caret = dataContext.getData<Caret>(PlatformDataKeys.CARET)

        if (project != null && file != null && editor != null && caret != null) {
            val offset = caret.selectionStart
            val length = caret.selectionEnd - offset
            val refactoring = ExtractWidgetRefactoring(project, file, offset, length)


            // Validate the initial status.
            val initialStatus = refactoring.checkInitialConditions() ?: return
            if (initialStatus.hasError()) {
                val message = initialStatus.message
                if (message != null) {
                    CommonRefactoringUtil.showErrorHint(project, editor, message, CommonBundle.getErrorTitle(), null)
                }
                return
            }

            ExtractWidgetDialog(project, file, editor, refactoring).show()
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = isVisibleFor(e)
        super.update(e)
    }

    private fun isVisibleFor(e: AnActionEvent): Boolean {
        val dataContext = e.dataContext
        //val project = dataContext.getData<Project>(PlatformDataKeys.PROJECT)
        val file = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
        return !(file == null || !FlutterUtils.isDartFile(file))
    }

    override fun startInTransaction() = true
}

internal class ExtractWidgetDialog(project: Project,
                                   val file: VirtualFile,
                                   var editor: Editor?,
                                   myRefactoring: ExtractWidgetRefactoring) : ServerRefactoringDialog<ExtractWidgetRefactoring>(project, editor, myRefactoring), VirtualFileListener {

    private val myNameField = JTextField()

    init {
        title = "Extract Widget"
        init()

        myNameField.text = "NewWidget"
        myNameField.selectAll()
        myNameField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                updateRefactoringOptions()
            }
        })

        updateRefactoringOptions()
    }

    private fun updateRefactoringOptions() {
        myRefactoring.setName(myNameField.text)
        myRefactoring.sendOptions()
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        super.contentsChanged(event)
        if (event.file.path == file.path) {
            file.refresh(true, true) {
                val originalFile = event.file.toPsiFile(project)
                val fileName = myNameField.text.toSnakeCase() + ".dart"
                if (originalFile != null) {
                    for (element in originalFile.children) {
                        if (element.text.startsWith("class ${myNameField.text}")) {
                            runUndoTransparentWriteAction {
                                originalFile.containingDirectory?.createFile(fileName)?.let {
                                    val projectName = PubspecYamlUtil.getDartProjectName(PubspecYamlUtil.findPubspecYamlFile(project, file)!!)
                                    val pathToNewFile = projectName + it.virtualFile.path.split("lib")[1]
                                    val importStatementOrig = project.createImportStatement("package:$pathToNewFile")
                                    val importStatement = project.createImportStatement("package:flutter/material.dart")

                                    originalFile.addAfter(importStatementOrig, originalFile.firstChild)
                                    it.add(importStatement)
                                    it.add(element)
                                    element.delete()
                                    DartStyleAction.runDartfmt(project, mutableListOf(it.virtualFile, file))
                                }
                            }
                            VirtualFileManager.getInstance().removeVirtualFileListener(this)
                            return@refresh
                        }
                    }
                }
            }
        }
    }

    override fun doAction() {
        VirtualFileManager.getInstance().addVirtualFileListener(this)
        super.doAction()
        FileDocumentManager.getInstance().saveAllDocuments()
    }

    override fun createCenterPanel(): JComponent? = null

    override fun createNorthPanel(): JComponent? {
        val panel = JPanel(GridBagLayout())
        val gbConstraints = GridBagConstraints()

        gbConstraints.insets = JBUI.insetsBottom(4)
        gbConstraints.gridx = 0
        gbConstraints.gridy = 0
        gbConstraints.gridwidth = 1
        gbConstraints.weightx = 0.0
        gbConstraints.weighty = 0.0
        gbConstraints.fill = GridBagConstraints.NONE
        gbConstraints.anchor = GridBagConstraints.WEST
        val nameLabel = JLabel("Widget name:")
        panel.add(nameLabel, gbConstraints)

        gbConstraints.insets = JBUI.insets(0, 4, 4, 0)
        gbConstraints.gridx = 1
        gbConstraints.gridy = 0
        gbConstraints.gridwidth = GridBagConstraints.REMAINDER
        gbConstraints.weightx = 1.0
        gbConstraints.weighty = 0.0
        gbConstraints.fill = GridBagConstraints.BOTH
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(myNameField, gbConstraints)
        myNameField.preferredSize = Dimension(200, myNameField.preferredSize.height)

        return panel
    }

    override fun getPreferredFocusedComponent() = myNameField
}


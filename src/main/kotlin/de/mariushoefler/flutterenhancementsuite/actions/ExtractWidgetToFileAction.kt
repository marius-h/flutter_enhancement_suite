package de.mariushoefler.flutterenhancementsuite.actions

import com.intellij.CommonBundle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.util.PopupUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiTreeChangeListener
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.util.ui.JBUI
import com.jetbrains.lang.dart.ide.actions.DartStyleAction
import com.jetbrains.lang.dart.ide.refactoring.ServerRefactoringDialog
import com.jetbrains.lang.dart.psi.DartClassDefinition
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import de.mariushoefler.flutterenhancementsuite.utils.createImportStatement
import de.mariushoefler.flutterenhancementsuite.utils.toSnakeCase
import io.flutter.FlutterUtils
import io.flutter.refactoring.ExtractWidgetRefactoring
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

const val NAME_FIELD_WIDTH = 200
const val SMALL_PADDING = 4

/**
 * Extract a widget to a seperate file
 *
 * @since v1.3
 */
class ExtractWidgetToFileAction : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = event.dataContext
        val project = dataContext.getData(PlatformDataKeys.PROJECT)
        val file = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE)
        val editor = dataContext.getData(PlatformDataKeys.EDITOR)
        val caret = dataContext.getData(PlatformDataKeys.CARET)

        ifLet(project, file, editor, caret) { (project, file, editor, caret) ->
            createExtractDialog(caret as Caret, project as Project, file as VirtualFile, editor as Editor)
        }
    }

    private inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
        if (elements.all { it != null }) {
            closure(elements.filterNotNull())
        }
    }

    private fun createExtractDialog(
        caret: Caret,
        project: Project,
        file: VirtualFile,
        editor: Editor
    ) {
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

internal class ExtractWidgetDialog(
    project: Project,
    val file: VirtualFile,
    var editor: Editor?,
    myRefactoring: ExtractWidgetRefactoring
) : ServerRefactoringDialog<ExtractWidgetRefactoring>(project, editor, myRefactoring) {

    private val myNameField = JTextField()
    private val myTreeChangeListener = WidgetTreeChangeListener()

    init {
        title = "Extract Widget to New File"
        init()

        myNameField.text = getFilenameSuggestion()
        myNameField.selectAll()
        myNameField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                updateRefactoringOptions()
            }
        })

        updateRefactoringOptions()
    }

    /**
     * Suggests a name according to the widget which is being extracted
     *
     * @since v1.3.2
     */
    private fun getFilenameSuggestion(): String {
        editor?.caretModel?.currentCaret?.offset?.let { offset ->
            val psiElement = PsiManager.getInstance(project).findFile(file)?.findElementAt(offset) ?: return@let

            val classElement = PsiTreeUtil.getParentOfType(psiElement, DartClassDefinition::class.java) ?: return@let

            val widgetName = psiElement.text.capitalize().split(".")[0]

            return classElement.name + widgetName
        }

        return "NewWidgetFile"
    }

    private fun updateRefactoringOptions() {
        myRefactoring.setName(myNameField.text)
        myRefactoring.sendOptions()
    }

    override fun doAction() {
        PsiManager.getInstance(project).addPsiTreeChangeListener(myTreeChangeListener)
        super.doAction()
        FileDocumentManager.getInstance().saveAllDocuments()
    }

    override fun createCenterPanel(): JComponent? = null

    override fun createNorthPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbConstraints = GridBagConstraints()

        gbConstraints.insets = JBUI.insetsBottom(SMALL_PADDING)
        gbConstraints.gridx = 0
        gbConstraints.gridy = 0
        gbConstraints.gridwidth = 1
        gbConstraints.weightx = 0.0
        gbConstraints.weighty = 0.0
        gbConstraints.fill = GridBagConstraints.NONE
        gbConstraints.anchor = GridBagConstraints.WEST
        val nameLabel = JLabel("Widget name:")
        panel.add(nameLabel, gbConstraints)

        gbConstraints.insets = JBUI.insets(0, SMALL_PADDING, SMALL_PADDING, 0)
        gbConstraints.gridx = 1
        gbConstraints.gridy = 0
        gbConstraints.gridwidth = GridBagConstraints.REMAINDER
        gbConstraints.weightx = 1.0
        gbConstraints.weighty = 0.0
        gbConstraints.fill = GridBagConstraints.BOTH
        gbConstraints.anchor = GridBagConstraints.WEST
        panel.add(myNameField, gbConstraints)
        myNameField.preferredSize = Dimension(NAME_FIELD_WIDTH, myNameField.preferredSize.height)

        return panel
    }

    override fun getPreferredFocusedComponent() = myNameField

    inner class WidgetTreeChangeListener : PsiTreeChangeListenerImpl() {
        override fun childAdded(event: PsiTreeChangeEvent) {
            event.file?.let { eventFile ->
                if (eventFile.virtualFile.path == file.path && event.child.text.startsWith("class")) {
                    file.refresh(true, true) {
                        val originalFile = PsiManager.getInstance(project).findFile(file)
                        val fileName = myNameField.text.toSnakeCase() + ".dart"
                        val pubspecFile = PubspecYamlUtil.findPubspecYamlFile(project, file)

                        if (originalFile != null && pubspecFile != null) {
                            extractWidget(originalFile, fileName, pubspecFile, event)
                        } else {
                            PopupUtil.showBalloonForActiveComponent(
                                "Unable to find the pubspec.yaml file in your project",
                                MessageType.ERROR
                            )
                        }
                        PsiManager.getInstance(project).removePsiTreeChangeListener(myTreeChangeListener)
                    }
                }
            }
        }

        override fun childRemoved(event: PsiTreeChangeEvent) {}

        override fun childReplaced(event: PsiTreeChangeEvent) {}

        override fun childrenChanged(event: PsiTreeChangeEvent) {}

        private fun extractWidget(
            originalFile: PsiFile,
            fileName: String,
            pubspecFile: VirtualFile,
            event: PsiTreeChangeEvent
        ) {
            runUndoTransparentWriteAction {
                val newFile = originalFile.containingDirectory?.findFile(fileName)
                    ?: originalFile.containingDirectory?.createFile(fileName)

                newFile?.let {
                    val projectName = PubspecYamlUtil.getDartProjectName(pubspecFile)
                    val pathToNewFile = projectName + it.virtualFile.path.split("lib")[1]
                    val importStatementOrig = project.createImportStatement("package:$pathToNewFile")
                    val importStatement = project.createImportStatement("package:flutter/material.dart")

                    originalFile.addAfter(importStatementOrig, originalFile.firstChild)
                    it.add(importStatement)
                    it.add(event.child)
                    event.child.delete()
                    DartStyleAction.runDartfmt(project, mutableListOf(it.virtualFile, file))
                }
            }
        }
    }
}

abstract class PsiTreeChangeListenerImpl : PsiTreeChangeListener {
    override fun beforeChildAddition(event: PsiTreeChangeEvent) {}

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {}

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {}

    override fun beforeChildMovement(event: PsiTreeChangeEvent) {}

    override fun beforeChildrenChange(event: PsiTreeChangeEvent) {}

    override fun beforePropertyChange(event: PsiTreeChangeEvent) {}

    override fun childMoved(event: PsiTreeChangeEvent) {}

    override fun propertyChanged(event: PsiTreeChangeEvent) {}
}

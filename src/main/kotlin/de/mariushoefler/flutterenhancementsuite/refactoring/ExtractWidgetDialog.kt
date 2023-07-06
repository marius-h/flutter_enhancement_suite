package de.mariushoefler.flutterenhancementsuite.refactoring

import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.util.PopupUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiTreeChangeListener
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.util.ui.JBUI
import com.jetbrains.lang.dart.ide.actions.DartStyleAction
import com.jetbrains.lang.dart.ide.refactoring.ServerRefactoringDialog
import com.jetbrains.lang.dart.psi.DartClassDefinition
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import de.mariushoefler.flutterenhancementsuite.utils.createImportStatement
import de.mariushoefler.flutterenhancementsuite.utils.extractDartImportStatements
import de.mariushoefler.flutterenhancementsuite.utils.toSnakeCase
import io.flutter.refactoring.ExtractWidgetRefactoring
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

internal class ExtractWidgetDialog(
    project: Project,
    val file: VirtualFile,
    var editor: Editor?,
    myRefactoring: ExtractWidgetRefactoring
) : ServerRefactoringDialog<ExtractWidgetRefactoring>(project, editor, myRefactoring), Disposable {

    private val myNameField = JTextField()
    private val myWidgetTreeChangeListener = WidgetTreeChangeListener()

    init {
        title = "Extract Widget to New File"
        init()

        myNameField.apply {
            text = getFilenameSuggestion()
            selectAll()
            document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    updateRefactoringOptions()
                }
            })
        }

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

            val widgetName = psiElement.text.replaceFirstChar { it.uppercase() }.split(".")[0]

            return classElement.name + widgetName
        }

        return "NewWidgetFile"
    }

    private fun updateRefactoringOptions() {
        myRefactoring.setName(myNameField.text)
        myRefactoring.sendOptions()
    }

    override fun doAction() {
        PsiManager.getInstance(project).addPsiTreeChangeListener(myWidgetTreeChangeListener, this)
        super.doAction()
        FileDocumentManager.getInstance().saveAllDocuments()
    }

    override fun dispose() {
        super.dispose()
    }

    override fun createCenterPanel(): JComponent? = null

    override fun createNorthPanel() = JPanel(GridBagLayout()).apply {
        add(
            JLabel("Widget name:"),
            GridBagConstraints().apply {
                insets = JBUI.insetsBottom(SMALL_PADDING)
                gridx = 0
                gridy = 0
                gridwidth = 1
                weightx = 0.0
                weighty = 0.0
                fill = GridBagConstraints.NONE
                anchor = GridBagConstraints.WEST
            },
        )

        add(
            myNameField.apply {
                preferredSize = Dimension(NAME_FIELD_WIDTH, myNameField.preferredSize.height)
            },
            GridBagConstraints().apply {
                insets = JBUI.insets(0, SMALL_PADDING, SMALL_PADDING, 0)
                gridx = 1
                gridy = 0
                gridwidth = GridBagConstraints.REMAINDER
                weightx = 1.0
                weighty = 0.0
                fill = GridBagConstraints.BOTH
                anchor = GridBagConstraints.WEST
            },
        )
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
        ) = runUndoTransparentWriteAction {
            val newFile = originalFile.containingDirectory?.findFile(fileName)
                ?: originalFile.containingDirectory?.createFile(fileName)

            PsiManager.getInstance(project).removePsiTreeChangeListener(myWidgetTreeChangeListener)
            newFile?.let {
                val projectName = PubspecYamlUtil.getDartProjectName(pubspecFile)
                val pathToNewFile = projectName + it.virtualFile.path.split("lib")[1]
                val importStatementOrig = project.createImportStatement("package:$pathToNewFile")
                val space = PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n")

                originalFile.extractDartImportStatements().forEach { importStatement ->
                    it.addAfter(space, it.add(importStatement))
                }
                originalFile.addAfter(space, originalFile.addBefore(importStatementOrig, originalFile.firstChild))
                it.add(event.child)
                event.child.delete()
                formatFiles(mutableListOf(it, originalFile))
            }
        }

        private fun formatFiles(filesToFormat: MutableList<PsiFile>) {
            DartStyleAction.runDartfmt(project, filesToFormat.map { it.virtualFile })
            OptimizeImportsProcessor(project, filesToFormat.toTypedArray(), null).run()
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

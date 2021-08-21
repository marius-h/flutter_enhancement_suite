package de.mariushoefler.flutterenhancementsuite.actions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.jetbrains.lang.dart.ide.actions.AbstractDartFileProcessingAction
import de.mariushoefler.flutterenhancementsuite.utils.FlutterProjectUtils

/**
 * Runs `flutter pub get` from the context menu of a file or directory
 *
 * @author Marius Höfler
 * @since v1.6.0
 */
class RunPackagesGetAction : AbstractDartFileProcessingAction() {
    override fun getActionTextForEditor(): String = "Run Pub Get"

    override fun getActionTextForFiles(): String = "Run Pub Get..."

    override fun runOverEditor(project: Project, editor: Editor, file: PsiFile) {
        FlutterProjectUtils.runPackagesGet(file, project)
    }

    override fun runOverFiles(project: Project, files: MutableList<VirtualFile>) {
        PsiManager.getInstance(project).findFile(files.first())?.let {
            FlutterProjectUtils.runPackagesGet(it, project)
        }
    }
}

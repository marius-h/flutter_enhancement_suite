package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import io.flutter.pub.PubRoot
import io.flutter.sdk.FlutterSdk

object FlutterProjectUtils {
    /**
     * Runs `flutter pub get` in project
     *
     * @since v1.2
     */
    fun runPackagesGet(file: PsiFile, project: Project) {
        PubRoot.forDescendant(file.virtualFile, project)?.let { pubRoot ->
            PsiDocumentManager.getInstance(project)?.let {
                it.getDocument(file)?.let { doc ->
                    it.doPostponedOperationsAndUnblockDocument(doc)
                    FileDocumentManager.getInstance().saveAllDocuments()
                    executePubGet(pubRoot, project)
                }
            }
        }
    }

    private fun executePubGet(pubRoot: PubRoot, project: Project) {
        pubRoot.getModule(project)?.let { module ->
            FlutterSdk.getFlutterSdk(project)?.flutterPackagesGet(pubRoot)
                ?.startInModuleConsole(module, { pubRoot.refresh() }, null)
        }
    }
}

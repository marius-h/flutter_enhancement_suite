package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageBundle
import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageSuitesBundle
import com.intellij.coverage.SimpleCoverageAnnotator
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.utils.isDartFileInLib
import io.flutter.pub.PubRoot
import java.io.File

/**
 * Coverage Annotator for Flutter
 *
 * Annotates files and directories inside the "lib/" folder with the corresponding coverage info.
 *
 * @author Marius Höfler
 * @since v1.5.0
 */
class FlutterCoverageAnnotator(project: Project) : SimpleCoverageAnnotator(project) {
    companion object {
        fun getInstance(project: Project): FlutterCoverageAnnotator =
            ServiceManager.getService(project, FlutterCoverageAnnotator::class.java)
    }

    override fun shouldCollectCoverageInsideLibraryDirs() = false

    override fun fillInfoForUncoveredFile(file: File) = FileCoverageInfo()

    override fun getDirCoverageInformationString(
        directory: PsiDirectory,
        currentSuite: CoverageSuitesBundle,
        manager: CoverageDataManager
    ): String? {
        val coverageInfo = getDirCoverageInfo(directory, currentSuite) ?: return null

        return if (manager.isSubCoverageActive) {
            if (coverageInfo.coveredLineCount > 0) CoverageBundle.message("coverage.view.text.covered") else null
        } else getFilesCoverageInformationString(coverageInfo)?.let { filesCoverageInfo ->
            val builder = StringBuilder()
            builder.append(filesCoverageInfo)
            getLinesCoverageInformationString(coverageInfo)?.let {
                builder.append(": ").append(it)
            }
            builder.toString()
        }
    }

    override fun getFileCoverageInformationString(
        psiFile: PsiFile,
        currentSuite: CoverageSuitesBundle,
        manager: CoverageDataManager
    ): String? = if (PubRoot.forPsiFile(psiFile)?.isDartFileInLib(psiFile.virtualFile) == true) {
        super.getFileCoverageInformationString(psiFile, currentSuite, manager)
    } else null

    override fun getFilesCoverageInformationString(info: DirCoverageInfo): String? =
        when {
            info.totalFilesCount == 0 -> null
            info.coveredFilesCount == 0 -> "${info.coveredFilesCount} of ${info.totalFilesCount} files covered"
            else -> "${info.coveredFilesCount} of ${info.totalFilesCount} files"
        }

    override fun getLinesCoverageInformationString(info: FileCoverageInfo): String? =
        when {
            info.totalLineCount == 0 -> null
            info.coveredLineCount == 0 -> CoverageBundle.message("lines.covered.info.not.covered")
            else -> {
                val message = CoverageBundle.message("lines.covered.info.percent.lines.covered")
                "${calcCoveragePercentage(info)} $message"
            }
        }

    override fun getRoots(
        project: Project?,
        dataManager: CoverageDataManager,
        suite: CoverageSuitesBundle?
    ): Array<VirtualFile> {
        project?.let { p ->
            PubRoot.forFile(p.workspaceFile)?.lib?.let {
                return arrayOf(it)
            }
        }
        return emptyArray()
    }
}

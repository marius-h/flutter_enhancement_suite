package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageEngine
import com.intellij.coverage.CoverageSuitesBundle
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.WrappingRunConfiguration
import com.intellij.execution.testframework.AbstractTestProxy
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.psi.DartFile
import io.flutter.pub.PubRoot
import io.flutter.run.test.TestConfig
import java.io.File

abstract class FlutterBaseCoverageEngine : CoverageEngine() {
    companion object {
        internal fun getQName(sourceFile: PsiFile) = sourceFile.virtualFile?.path

        internal fun findRootDir(project: Project): VirtualFile? {
            return project.projectFile?.let {
                PubRoot.forDescendant(it, project)?.lib
            }
        }
    }

    override fun isApplicableTo(conf: RunConfigurationBase<*>) = if (conf is WrappingRunConfiguration<*>) {
        conf.peer
    } else {
        conf
    } is TestConfig

    override fun coverageEditorHighlightingApplicableTo(psiFile: PsiFile): Boolean {
        return psiFile is DartFile && psiFile.virtualFile.path.contains("/lib/")
    }

    override fun coverageProjectViewStatisticsApplicableTo(fileOrDir: VirtualFile): Boolean {
        return !fileOrDir.isDirectory && fileOrDir.fileType is DartFileType
    }

    override fun canHavePerTestCoverage(conf: RunConfigurationBase<*>) = false

    override fun recompileProjectAndRerunAction(
        module: Module,
        suite: CoverageSuitesBundle,
        chooseSuiteAction: Runnable
    ) = false

    override fun includeUntouchedFileInCoverage(
        qualifiedName: String,
        outputFile: File,
        sourceFile: PsiFile,
        suite: CoverageSuitesBundle
    ) = false

    override fun acceptedByFilters(psiFile: PsiFile, suite: CoverageSuitesBundle) = true

    override fun collectSrcLinesForUntouchedFile(classFile: File, suite: CoverageSuitesBundle): MutableList<Int>? =
        null

    override fun findTestsByNames(testNames: Array<out String>, project: Project): MutableList<PsiElement> =
        mutableListOf()

    override fun getTestMethodName(element: PsiElement, testProxy: AbstractTestProxy): String? = null
}

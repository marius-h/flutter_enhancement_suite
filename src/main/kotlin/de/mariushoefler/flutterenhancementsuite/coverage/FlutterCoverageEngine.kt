package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageAnnotator
import com.intellij.coverage.CoverageEditorAnnotator
import com.intellij.coverage.CoverageEngine
import com.intellij.coverage.CoverageFileProvider
import com.intellij.coverage.CoverageRunner
import com.intellij.coverage.CoverageSuite
import com.intellij.coverage.CoverageSuitesBundle
import com.intellij.coverage.view.CoverageListRootNode
import com.intellij.coverage.view.CoverageViewExtension
import com.intellij.coverage.view.CoverageViewManager
import com.intellij.coverage.view.DirectoryCoverageViewExtension
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.WrappingRunConfiguration
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.intellij.execution.testframework.AbstractTestProxy
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.containers.ContainerUtil
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.psi.DartFile
import io.flutter.pub.PubRoot
import io.flutter.run.test.TestConfig
import java.io.File
import java.util.*

class FlutterCoverageEngine : CoverageEngine() {
    companion object {
        private fun getQName(sourceFile: PsiFile) = sourceFile.virtualFile?.path
    }

    override fun isApplicableTo(conf: RunConfigurationBase<*>): Boolean {
        return if (conf is WrappingRunConfiguration<*>) {
            conf.peer
        } else {
            conf
        } is TestConfig
    }

    override fun canHavePerTestCoverage(conf: RunConfigurationBase<*>) = false

    override fun createCoverageEnabledConfiguration(conf: RunConfigurationBase<*>): CoverageEnabledConfiguration {
        return FlutterCoverageEnabledConfiguration(conf)
    }

    override fun createCoverageSuite(
        covRunner: CoverageRunner,
        name: String,
        coverageDataFileProvider: CoverageFileProvider,
        filters: Array<out String?>?,
        lastCoverageTimeStamp: Long,
        suiteToMerge: String?,
        coverageByTestEnabled: Boolean,
        tracingEnabled: Boolean,
        trackTestFolders: Boolean,
        project: Project
    ): CoverageSuite {
        return FlutterCoverageSuite(
            covRunner,
            name,
            coverageDataFileProvider,
            lastCoverageTimeStamp,
            coverageByTestEnabled,
            tracingEnabled,
            trackTestFolders,
            project,
            this
        )
    }

    override fun createCoverageSuite(
        covRunner: CoverageRunner,
        name: String,
        coverageDataFileProvider: CoverageFileProvider,
        config: CoverageEnabledConfiguration
    ): CoverageSuite? {
        return if (config is FlutterCoverageEnabledConfiguration) {
            val project = config.configuration.project
            this.createCoverageSuite(
                covRunner,
                name,
                coverageDataFileProvider,
                null as Array<String?>?,
                Date().time,
                null as String?,
                coverageByTestEnabled = false,
                tracingEnabled = false,
                trackTestFolders = true,
                project = project
            )
        } else {
            null
        }
    }

    override fun createEmptyCoverageSuite(coverageRunner: CoverageRunner): CoverageSuite {
        return FlutterCoverageSuite(this)
    }

    override fun getCoverageAnnotator(project: Project): CoverageAnnotator {
        return FlutterCoverageAnnotator.getInstance(project)
    }

    override fun coverageEditorHighlightingApplicableTo(psiFile: PsiFile): Boolean {
        return psiFile is DartFile && psiFile.virtualFile.path.contains("/lib/")
    }

    override fun acceptedByFilters(psiFile: PsiFile, suite: CoverageSuitesBundle): Boolean {
        return true
    }

    override fun recompileProjectAndRerunAction(
        module: Module,
        suite: CoverageSuitesBundle,
        chooseSuiteAction: Runnable
    ): Boolean {
        return false
    }

    override fun getQualifiedName(outputFile: File, sourceFile: PsiFile): String? {
        return getQName(sourceFile)
    }

    override fun getQualifiedNames(sourceFile: PsiFile): MutableSet<String> {
        return getQName(sourceFile)?.let {
            mutableSetOf(it)
        } ?: mutableSetOf()
    }

    override fun includeUntouchedFileInCoverage(
        qualifiedName: String,
        outputFile: File,
        sourceFile: PsiFile,
        suite: CoverageSuitesBundle
    ): Boolean {
        return false
    }

    override fun collectSrcLinesForUntouchedFile(classFile: File, suite: CoverageSuitesBundle): MutableList<Int>? {
        return null
    }

    override fun findTestsByNames(testNames: Array<out String>, project: Project): MutableList<PsiElement> {
        return mutableListOf()
    }

    override fun getTestMethodName(element: PsiElement, testProxy: AbstractTestProxy): String? {
        return null
    }

    override fun getPresentableText(): String {
        return "FlutterTestRunnerCoverage"
    }

    override fun coverageProjectViewStatisticsApplicableTo(fileOrDir: VirtualFile): Boolean {
        return !fileOrDir.isDirectory && fileOrDir.fileType is DartFileType
    }

    override fun createCoverageViewExtension(
        project: Project,
        suiteBundle: CoverageSuitesBundle,
        stateBean: CoverageViewManager.StateBean?
    ): CoverageViewExtension {
        return object : DirectoryCoverageViewExtension(project, getCoverageAnnotator(project), suiteBundle, stateBean) {
            override fun getChildrenNodes(node: AbstractTreeNode<*>?): MutableList<AbstractTreeNode<*>> {
                return ContainerUtil.filter(super.getChildrenNodes(node)) { child ->
                    return@filter !StringUtil.equals(child.name, ".idea")
                }
            }

            override fun createRootNode(): AbstractTreeNode<*> {
                return ReadAction.compute<AbstractTreeNode<*>, NullPointerException> {
                    val rootDir = findRootDir(project) ?: project.projectFile!!
                    val psiRootDir = PsiManager.getInstance(project).findDirectory(rootDir)!!

                    return@compute CoverageListRootNode(myProject, psiRootDir, mySuitesBundle, myStateBean)
                }
            }
        }
    }

    override fun shouldHighlightFullLines(): Boolean {
        return true
    }

    private fun findRootDir(project: Project): VirtualFile? {
        return project.projectFile?.let {
            PubRoot.forDescendant(it, project)?.lib
        }
    }
}

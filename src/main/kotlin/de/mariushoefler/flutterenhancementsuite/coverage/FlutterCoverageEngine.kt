package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageFileProvider
import com.intellij.coverage.CoverageRunner
import com.intellij.coverage.CoverageSuitesBundle
import com.intellij.coverage.view.CoverageListRootNode
import com.intellij.coverage.view.CoverageViewManager
import com.intellij.coverage.view.DirectoryCoverageViewExtension
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.containers.ContainerUtil
import java.io.File
import java.util.Date

/**
 * Coverage engine for Flutter
 *
 * Presents the coverage results to the user.
 *
 * @author Marius Höfler
 * @since v1.5.0
 */
class FlutterCoverageEngine : FlutterBaseCoverageEngine() {
    override fun getPresentableText() = "FlutterTestRunnerCoverage"

    override fun createCoverageEnabledConfiguration(conf: RunConfigurationBase<*>) =
        FlutterCoverageEnabledConfiguration(conf)

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
    ) = FlutterCoverageSuite(
        name,
        coverageDataFileProvider,
        lastCoverageTimeStamp,
        coverageByTestEnabled,
        tracingEnabled,
        trackTestFolders,
        covRunner,
        project,
        this
    )

    override fun createCoverageSuite(
        covRunner: CoverageRunner,
        name: String,
        coverageDataFileProvider: CoverageFileProvider,
        config: CoverageEnabledConfiguration
    ) = if (config is FlutterCoverageEnabledConfiguration) {
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
    } else null

    override fun createEmptyCoverageSuite(coverageRunner: CoverageRunner) = FlutterCoverageSuite(this)

    override fun getCoverageAnnotator(project: Project) =
        FlutterCoverageAnnotator.getInstance(project)

    override fun getQualifiedName(outputFile: File, sourceFile: PsiFile) = getQName(sourceFile)

    override fun getQualifiedNames(sourceFile: PsiFile) = getQName(sourceFile)?.let {
        mutableSetOf(it)
    } ?: mutableSetOf()

    override fun createCoverageViewExtension(
        project: Project,
        suiteBundle: CoverageSuitesBundle,
        stateBean: CoverageViewManager.StateBean?
    ) =
        object : DirectoryCoverageViewExtension(project, getCoverageAnnotator(project), suiteBundle, stateBean) {
            override fun getChildrenNodes(node: AbstractTreeNode<*>?): MutableList<AbstractTreeNode<*>> {
                return ContainerUtil.filter(super.getChildrenNodes(node)) { child ->
                    !StringUtil.equals(child.name, ".idea")
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

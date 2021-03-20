package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageRunner
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import io.flutter.pub.PubRootCache

/**
 * Coverage Enabled Configuration for Flutter
 *
 * Stores where the coverage report can be found at.
 *
 * @author Marius Höfler
 * @since v1.5.0
 */
class FlutterCoverageEnabledConfiguration(conf: RunConfigurationBase<*>) : CoverageEnabledConfiguration(conf) {
    var project = conf.project

    init {
        coverageRunner = CoverageRunner.getInstance(FlutterCoverageRunner::class.java)
        PubRootCache.getInstance(project).getRoot(project.projectFile)?.let {
            myCoverageFilePath = "${it.root.canonicalPath}/coverage/lcov.info"
        }
        currentCoverageSuite = CoverageDataManager.getInstance(project)
            .addCoverageSuite(this)
    }
}

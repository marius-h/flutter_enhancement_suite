package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageRunner
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import io.flutter.pub.PubRootCache
import java.io.File
import java.net.URI

class FlutterCoverageEnabledConfiguration(configuration: RunConfigurationBase<*>) :
    CoverageEnabledConfiguration(configuration) {
    var project = configuration.project

    init {
        coverageRunner = CoverageRunner.getInstance(FlutterCoverageRunner::class.java)
        PubRootCache.getInstance(project).getRoot(project.projectFile)?.let {
            print(it)
            myCoverageFilePath = File(URI("${it.root}/coverage/lcov.info")).path
        }
        currentCoverageSuite = CoverageDataManager.getInstance(project)
            .addCoverageSuite(this)
    }
}

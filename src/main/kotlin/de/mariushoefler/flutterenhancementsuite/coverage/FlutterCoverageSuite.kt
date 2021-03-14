package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.BaseCoverageSuite
import com.intellij.coverage.CoverageFileProvider
import com.intellij.coverage.CoverageRunner
import com.intellij.openapi.project.Project

/**
 * Coverage suite for Flutter
 *
 * This is a place to store coverage settings.
 *
 * @author Marius Höfler
 * @since v1.5.0
 */
class FlutterCoverageSuite : BaseCoverageSuite {
    private var flutterCoverageEngine: FlutterCoverageEngine

    constructor(flutterCoverageEngine: FlutterCoverageEngine) {
        this.flutterCoverageEngine = flutterCoverageEngine
    }

    @Suppress("LongParameterList")
    constructor(
        name: String?,
        fileProvider: CoverageFileProvider?,
        lastCoverageTimeStamp: Long,
        coverageByTestEnabled: Boolean,
        tracingEnabled: Boolean,
        trackTestFolders: Boolean,
        coverageRunner: CoverageRunner?,
        project: Project?,
        flutterCoverageEngine: FlutterCoverageEngine
    ) : super(
        name, fileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled, trackTestFolders,
        coverageRunner, project
    ) {
        this.flutterCoverageEngine = flutterCoverageEngine
    }

    override fun deleteCachedCoverageData() {
    }

    override fun getCoverageEngine() = flutterCoverageEngine
}

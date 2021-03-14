package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageEngine
import com.intellij.coverage.CoverageRunner
import com.intellij.coverage.CoverageSuite
import com.intellij.openapi.diagnostic.Logger
import com.intellij.rt.coverage.data.LineData
import com.intellij.rt.coverage.data.ProjectData
import de.mariushoefler.flutterenhancementsuite.utils.LcovCoverageReport
import java.io.File
import java.io.IOException

class FlutterCoverageRunner : CoverageRunner() {
    var workingDirectory: String? = null

    companion object {
        private val LOG = Logger.getInstance(FlutterCoverageRunner::class.java)

        fun getInstance(): FlutterCoverageRunner {
            return getInstance(FlutterCoverageRunner::class.java)
        }

        @Throws(IOException::class)
        private fun readProjectData(dataFile: File, basePathDir: File): ProjectData {
            val projectData = ProjectData()
            val report = LcovCoverageReport.Serialization.readLcov(dataFile, basePathDir.absolutePath)
            for ((filePath, lineHitsList) in report.records) {
                val classData = projectData.getOrCreateClassData(filePath)
                val max = lineHitsList.lastOrNull()?.lineNumber ?: 0
                val lines = arrayOfNulls<LineData>(max + 1)
                for (lineHits in lineHitsList) {
                    val lineData = LineData(lineHits.lineNumber, null)
                    lineData.hits = lineHits.hits
                    lines[lineHits.lineNumber] = lineData
                    classData.registerMethodSignature(lineData)
                }

                classData.setLines(lines)
            }
            return projectData
        }
    }

    override fun loadCoverageData(sessionDataFile: File, baseCoverageSuite: CoverageSuite?): ProjectData? {
        return try {
            readProjectData(sessionDataFile, getBaseDir())
        } catch (e: Exception) {
            LOG.warn("Can't read coverage data", e)
            null
        }
    }

    private fun getBaseDir(): File {
        return workingDirectory?.let { File(it) } ?: File(".")
    }

    override fun getPresentableName(): String {
        return "Flutter Test Run"
    }

    override fun getId(): String {
        return "FlutterTestRunnerCoverage"
    }

    override fun getDataFileExtension(): String {
        return "info"
    }

    override fun acceptsCoverageEngine(engine: CoverageEngine): Boolean {
        return engine is FlutterCoverageEngine
    }
}

package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageExecutor
import com.intellij.coverage.CoverageRunnerData
import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.ConfigurationInfoProvider
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.runners.executeState
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import io.flutter.pub.PubRootCache
import io.flutter.run.test.TestConfig
import java.io.File

/**
 * Coverage program runner for Flutter
 *
 * Enables the "Run with coverage" action in the toolbar and launches the process.
 *
 * @author Marius Höfler
 * @since v1.5.0
 */
class FlutterCoverageProgramRunner : ProgramRunner<RunnerSettings> {
    companion object {
        private val LOG = Logger.getInstance(FlutterCoverageProgramRunner::class.java)
        private val COVERAGE_RUNNER_ID = this::class.java.simpleName

        private fun updateCoverageView(env: ExecutionEnvironment) {
            val runConfiguration = env.runProfile as RunConfigurationBase<*>
            CoverageEnabledConfiguration.getOrCreate(runConfiguration).coverageFilePath?.let {
                val lcovFile = File(it)
                if (lcovFile.isFile) {
                    val runnerSettings = env.runnerSettings
                    if (runnerSettings != null) {
                        val coverageRunner: FlutterCoverageRunner = FlutterCoverageRunner.getInstance()
                        coverageRunner.workingDirectory =
                            PubRootCache.getInstance(env.project).getRoot(env.project.projectFile)?.root?.path

                        CoverageDataManager.getInstance(env.project)
                            .processGatheredCoverage(runConfiguration, runnerSettings)
                    }
                } else {
                    LOG.warn("Cannot find " + lcovFile.absolutePath)
                }
            }
        }
    }

    override fun getRunnerId(): String = COVERAGE_RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == CoverageExecutor.EXECUTOR_ID && profile is TestConfig
    }

    override fun createConfigurationData(settingsProvider: ConfigurationInfoProvider) =
        CoverageRunnerData()

    @Suppress("UnstableApiUsage")
    override fun execute(environment: ExecutionEnvironment) {
        val fakeEnvironment = ExecutionEnvironment(
            DefaultRunExecutor(),
            environment.runner,
            environment.runnerAndConfigurationSettings!!,
            environment.project
        )

        ExecutionManager.getInstance(environment.project).startRunProfile(fakeEnvironment) { state ->
            return@startRunProfile executeState(state, fakeEnvironment, this)?.also {
                doExecute(it, environment)
            }
        }
    }

    private fun doExecute(descriptor: RunContentDescriptor, environment: ExecutionEnvironment) {
        descriptor.processHandler?.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                ApplicationManager.getApplication().invokeLater(
                    {
                        updateCoverageView(environment)
                    },
                    environment.project.disposed
                )
            }
        })
    }
}

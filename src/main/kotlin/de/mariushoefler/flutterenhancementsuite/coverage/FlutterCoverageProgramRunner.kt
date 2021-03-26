package de.mariushoefler.flutterenhancementsuite.coverage

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageExecutor
import com.intellij.coverage.CoverageRunnerData
import com.intellij.execution.ExecutionManager
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationInfoProvider
import com.intellij.execution.configurations.RunConfiguration
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
import com.intellij.openapi.options.SettingsEditor
import io.flutter.run.test.TestConfig
import io.flutter.run.test.TestFields
import java.nio.file.Files
import java.nio.file.Paths

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
            val test = CoverageEnabledConfiguration.getOrCreate(runConfiguration)
            test.coverageFilePath?.let {
                val lcovFilePath = Paths.get(it)
                if (Files.exists(lcovFilePath)) {
                    val runnerSettings = env.runnerSettings
                    if (runnerSettings != null) {
                        val coverageRunner: FlutterCoverageRunner = FlutterCoverageRunner.getInstance()
                        coverageRunner.workingDirectory = env.project.basePath

                        CoverageDataManager.getInstance(env.project)
                            .processGatheredCoverage(runConfiguration, runnerSettings)
                    }
                } else LOG.warn("Cannot find $lcovFilePath")
            }
        }
    }

    override fun getRunnerId(): String = COVERAGE_RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == CoverageExecutor.EXECUTOR_ID && profile is TestConfig
    }

    override fun getSettingsEditor(
        executor: Executor?,
        configuration: RunConfiguration?
    ): SettingsEditor<RunnerSettings>? {
        return super.getSettingsEditor(executor, configuration)
    }

    override fun createConfigurationData(settingsProvider: ConfigurationInfoProvider) =
        CoverageRunnerData()

    @Suppress("UnstableApiUsage")
    override fun execute(environment: ExecutionEnvironment) {
        val fakeEnvironment = createFakeExecutionEnvironment(environment)

        ExecutionManager.getInstance(environment.project).startRunProfile(fakeEnvironment) { state ->
            return@startRunProfile executeState(state, fakeEnvironment, this)?.also {
                doExecute(it, environment)
            }
        }
    }

    private fun createFakeExecutionEnvironment(environment: ExecutionEnvironment): ExecutionEnvironment {
        val fakeEnvironment = ExecutionEnvironment(
            DefaultRunExecutor(),
            environment.runner,
            environment.runnerAndConfigurationSettings!!,
            environment.project
        )
        val executionEnvironmentClass = ExecutionEnvironment::class.java
        val myRunProfile = executionEnvironmentClass.getDeclaredField("myRunProfile").apply {
            isAccessible = true
        }
        val runProfile = myRunProfile.get(fakeEnvironment) as RunProfile

        if (runProfile is TestConfig) {
            val runProfileCopy = runProfile.clone() as TestConfig
            setCoverageParam(runProfileCopy)
            myRunProfile.set(fakeEnvironment, runProfileCopy)
        }

        return fakeEnvironment
    }

    private fun setCoverageParam(profile: TestConfig) {
        val testConfigClass = TestConfig::class.java
        val getFields = testConfigClass.getDeclaredMethod("getFields").apply {
            isAccessible = true
        }
        val setFields =
            testConfigClass.getDeclaredMethod("setFields", io.flutter.run.test.TestFields::class.java).apply {
                isAccessible = true
            }
        val fields = getFields(profile) as TestFields
        if (fields.additionalArgs?.contains("--coverage") == false) {
            fields.additionalArgs += "--coverage"
            setFields(profile, fields)
        } else if (fields.additionalArgs == null) {
            fields.additionalArgs = "--coverage"
            setFields(profile, fields)
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

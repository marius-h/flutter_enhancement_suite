package de.mariushoefler.flutterenhancementsuite.diagnostics

import com.intellij.ide.DataManager
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer
import de.mariushoefler.flutterenhancementsuite.utils.fetchDartPluginVersion
import de.mariushoefler.flutterenhancementsuite.utils.fetchFlutterPluginVersion
import de.mariushoefler.flutterenhancementsuite.utils.fetchIntelliJVersion
import de.mariushoefler.flutterenhancementsuite.utils.fetchThisPluginVersion
import de.mariushoefler.flutterenhancementsuite.utils.getFlutterVersion
import io.flutter.sdk.FlutterSdk
import java.awt.Component

class PluginBugReportSubmitter : ErrorReportSubmitter() {

    override fun getReportActionText(): String {
        return "Create Bug Report for Flutter Enhancement Suite"
    }

    override fun submit(
        events: Array<IdeaLoggingEvent>,
        info: String?,
        parent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val stackTrace: String? = null
        val errorMessage: String? = null

        val dataContext = DataManager.getInstance().getDataContext(parent)
        val project = PROJECT.getData(dataContext)
        val scratchRoot = ScratchRootType.getInstance()
        val file: VirtualFile? = project?.let {
            val text = buildBugContent(info, project, stackTrace, events, errorMessage)
            scratchRoot.createScratchFile(project, "bug-report.md", Language.ANY, text)
        }

        if (events.isEmpty() || project == null || file == null) {
            fail(consumer)
            return false
        }

        OpenFileDescriptor(project, file).navigate(true)

        consumer.consume(
            SubmittedReportInfo(
                null,
                "",
                SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
            )
        )

        return true
    }

    private fun buildBugContent(
        info: String?,
        project: Project,
        stackTrace: String?,
        events: Array<out IdeaLoggingEvent>,
        errorMessage: String?
    ): String {
        val builder = StringBuilder()

        builder.append("Please file this bug report at ")
        builder.append("https://github.com/marius-h/flutter_enhancement_suite/issues/new")
        builder.append(".\n")
        builder.append("\n")
        builder.append("---\n")
        builder.append("\n")

        builder.append("## What happened\n")
        builder.append("\n")
        if (info != null) {
            builder.append(info.trim()).append("\n")
        } else {
            builder.append("(please describe what you were doing when this exception occurred)\n")
        }
        builder.append("\n")

        addVersionInformation(builder, project)

        addStacktrace(stackTrace, events, builder, errorMessage)

        return builder.toString().trim() + "\n"
    }

    private fun addVersionInformation(builder: StringBuilder, project: Project) {
        builder.append("## Version information\n")
        builder.append("\n")

        fetchIntelliJVersion(builder)
        fetchThisPluginVersion(builder)
        fetchFlutterPluginVersion(builder)
        fetchDartPluginVersion(builder)
        fetchFlutterSdkVersion(project, builder)
    }

    private fun addStacktrace(
        stackTrace: String?,
        events: Array<out IdeaLoggingEvent>,
        builder: StringBuilder,
        errorMessage: String?
    ) {
        if (stackTrace == null) {
            for (event in events) {
                builder.append("## Exception\n")
                builder.append("\n")
                builder.append(event.message).append("\n")
                builder.append("\n")

                if (event.throwable != null) {
                    builder.append("```\n")
                    builder.append(event.throwableText.trim()).append("\n")
                    builder.append("```\n")
                    builder.append("\n")
                }
            }
        } else {
            builder.append("## Exception\n")
            builder.append("\n")
            builder.append(errorMessage).append("\n")
            builder.append("\n")
            builder.append("```\n")
            builder.append(stackTrace.replace("\\\\n", "\n")).append("\n")
            builder.append("```\n")
            builder.append("\n")
        }
    }

    private fun fetchFlutterSdkVersion(project: Project, builder: StringBuilder) {
        val sdk = FlutterSdk.getFlutterSdk(project)
        if (sdk == null) {
            builder.append("No Flutter sdk configured.\n")
        } else {
            val flutterVersion = getFlutterVersion(sdk)
            if (flutterVersion != null) {
                builder.append(flutterVersion.trim()).append("\n")
            } else {
                builder.append("Error getting Flutter sdk information.\n")
            }
        }
        builder.append("\n")
    }

    private fun fail(consumer: Consumer<in SubmittedReportInfo>) {
        consumer.consume(
            SubmittedReportInfo(
                null,
                null,
                SubmittedReportInfo.SubmissionStatus.FAILED
            )
        )
    }
}

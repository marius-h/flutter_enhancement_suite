package de.mariushoefler.flutter_enhancement_suite

import com.intellij.ide.DataManager
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.util.Consumer
import io.flutter.FlutterUtils
import io.flutter.run.daemon.DaemonApi.COMPLETION_EXCEPTION_PREFIX
import io.flutter.sdk.FlutterSdk
import java.awt.Component
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


class PluginBugReportSubmitter : ErrorReportSubmitter() {

	override fun getReportActionText(): String {
		return "Create Bug Report for Flutter Enhancement Suite"
	}

	override fun submit(events: Array<out IdeaLoggingEvent>, info: String?, parent: Component, consumer: Consumer<SubmittedReportInfo>): Boolean {
		if (events.isEmpty()) {
			fail(consumer)
			return false
		}

		val stackTrace: String? = null
		val errorMessage: String? = null

		for (event in events) {
			val stackTraceText = event.throwableText
			if (stackTraceText.startsWith(COMPLETION_EXCEPTION_PREFIX)) {

			}
		}


		val dataContext = DataManager.getInstance().getDataContext(parent)
		val project = PROJECT.getData(dataContext)
		if (project == null) {
			fail(consumer)
			return false
		}

		val text = buildBugContent(info, project, stackTrace, events, errorMessage)


		val scratchRoot = ScratchRootType.getInstance()
		val file = scratchRoot.createScratchFile(project, "bug-report.md", Language.ANY, text)

		if (file == null) {
			fail(consumer)
			return false
		}

		OpenFileDescriptor(project, file).navigate(true)

		consumer.consume(SubmittedReportInfo(
				null,
				"",
				SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
		))

		return true
	}

	private fun buildBugContent(info: String?, project: Project, stackTrace: String?, events: Array<out IdeaLoggingEvent>, errorMessage: String?): String {
		val builder = StringBuilder()

		builder.append("Please file this bug report at ")
		builder.append("https://github.com/marius-h/flutter_enhancement_suite/issues/new")
		builder.append(".\n");
		builder.append("\n");
		builder.append("---\n");
		builder.append("\n");

		builder.append("## What happened\n");
		builder.append("\n");
		if (info != null) {
			builder.append(info.trim()).append("\n");
		} else {
			builder.append("(please describe what you were doing when this exception occurred)\n");
		}
		builder.append("\n");

		builder.append("## Version information\n");
		builder.append("\n");

		// IntelliJ version
		val applicationInfo = ApplicationInfo.getInstance()
		builder.append(applicationInfo.versionName).append(" `").append(applicationInfo.fullVersion).append("`")

		val pid = FlutterUtils.getPluginId()
		val flutterPlugin = PluginManager.getPlugin(pid)
		if (flutterPlugin != null) {
			builder.append(" • Flutter plugin `").append(pid.idString).append(' ').append(flutterPlugin.version).append("`")
		}

		val dartPlugin = PluginManager.getPlugin(PluginId.getId("Dart"))
		if (dartPlugin != null) {
			builder.append(" • Dart plugin `").append(dartPlugin.version).append("`")
		}
		builder.append("\n\n")

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

		return builder.toString().trim() + "\n"
	}

	private fun getFlutterVersion(sdk: FlutterSdk): String? {
		try {
			val flutterPath = sdk.homePath + "/bin/flutter"
			val builder = ProcessBuilder(flutterPath, "--version")
			val process = builder.start()
			if (!process.waitFor(3, TimeUnit.SECONDS)) {
				return null
			}
			return String(readFully(process.inputStream), StandardCharsets.UTF_8)
		} catch (e: IOException) {
			return null;
		} catch (e: InterruptedException) {
			return null;
		}
	}

	private fun fail(consumer: Consumer<SubmittedReportInfo>) {
		consumer.consume(SubmittedReportInfo(
				null,
				null,
				SubmittedReportInfo.SubmissionStatus.FAILED))
	}

	@Throws(IOException::class)
	private fun readFully(inputStream: InputStream): ByteArray {
		val out = ByteArrayOutputStream()
		val temp = ByteArray(4096)
		var count = inputStream.read(temp)
		while (count > 0) {
			out.write(temp, 0, count)
			count = inputStream.read(temp)
		}
		return out.toByteArray()
	}
}
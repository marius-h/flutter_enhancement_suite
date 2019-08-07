package de.mariushoefler.flutter_enhancement_suite.utils

import com.intellij.psi.PsiFile
import de.mariushoefler.flutter_enhancement_suite.pub.DependencyChecker
import de.mariushoefler.flutter_enhancement_suite.pub.UnableToGetLatestVersionException
import kotlinx.coroutines.*
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

const val REGEX_DEPENDENCY = ".*(?!version|sdk)\\b\\S+:.+\\.[0-9]+\\.[0-9]+(.*)"
const val YML_EXTENSIONS = "yml"

class FileParser(
    private val file: PsiFile,
    private val dependencyChecker: DependencyChecker
) : CoroutineScope {

	private val scope = CoroutineScope(Dispatchers.IO)

	override val coroutineContext: CoroutineContext
		get() = scope.coroutineContext

    suspend fun checkFile(): List<VersionDescription> {

        return if (file.isPubspecFile()) {
            return getVersionsFromFile()
        } else {
            emptyList()
        }
    }

    private suspend fun getVersionsFromFile(): MutableList<VersionDescription> {
		return coroutineScope {
			val problemDescriptionList = mutableListOf<VersionDescription>()
			val lines = file.readPackageLines().map { async { mapToVersionDescription(it) } }.awaitAll()
			lines.forEach { versionDescription ->
				try {
					if (versionDescription.latestVersion != versionDescription.currentVersion) {
						problemDescriptionList.add(versionDescription)
					}
				} catch (e: UnableToGetLatestVersionException) {
					//no-op
				}
			}
			return@coroutineScope problemDescriptionList
		}
	}

    @Throws(UnableToGetLatestVersionException::class)
    private fun mapToVersionDescription(it: Pair<String, Int>): VersionDescription {
        val dependency = it.first
        val counter = it.second

        val latestVersion = dependencyChecker.getLatestVersion(dependency)
        val currentVersion = getCurrentVersion(dependency)

        return VersionDescription(counter, currentVersion, latestVersion)
    }
}

private fun PsiFile.isPubspecFile(): Boolean {
    return fileType.defaultExtension == YML_EXTENSIONS && name.contains("pubspec")
}

private fun PsiFile.readPackageLines(): List<Pair<String, Int>> {
    val linesList = mutableListOf<Pair<String, Int>>()
    var line = ""
    var counter = 0
    text.forEach {
        counter++
        if (it == '\n') {
            line = line.trim()
            if (!line.startsWith("#") && line.isPackageName() && line.contains("^")) {
                linesList.add(line to counter - 2)
            }
            line = ""
        } else {
            line += it
        }
    }
    return linesList
}


fun String.isPackageName(): Boolean {
    val regexPattern = Pattern.compile(REGEX_DEPENDENCY)
    return regexPattern.matcher(this).matches()
}


private fun getCurrentVersion(dependency: String): String {
	return dependency.split(':')[1].replace("^", "").trim()
}


data class VersionDescription(
    val counter: Int,
    val currentVersion: String,
    val latestVersion: String
)
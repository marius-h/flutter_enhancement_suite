package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.psi.PsiFile
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import de.mariushoefler.flutterenhancementsuite.pub.DependencyChecker
import de.mariushoefler.flutterenhancementsuite.pub.UnableToGetLatestVersionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

const val REGEX_DEPENDENCY = ".*(?!version|sdk)\\b\\S+:.+(\\.[0-9]+\\.[0-9]+(.*)|any)"

class FileParser(
    private val file: PsiFile,
    private val dependencyChecker: DependencyChecker
) : CoroutineScope {

    private val parentJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)

    override val coroutineContext: CoroutineContext
        get() = scope.coroutineContext

    companion object {
        private suspend fun getVersionsFromFile(fileParser: FileParser): MutableList<VersionDescription> {
            val problemDescriptionList = mutableListOf<VersionDescription>()
            val lines = fileParser.file.readPackageLines().map {
                fileParser.async { fileParser.mapToVersionDescription(it) }
            }.awaitAll()

            lines.forEach { versionDescription ->
                if (versionDescription.latestVersion != versionDescription.currentVersion) {
                    problemDescriptionList.add(versionDescription)
                }
            }
            return problemDescriptionList
        }
    }

    suspend fun checkFile(): List<VersionDescription> {
        parentJob.cancelChildren()

        return if (file.isPubspecFile()) {
            return getVersionsFromFile(this)
        } else {
            emptyList()
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

fun PsiFile.isPubspecFile(): Boolean {
    return PubspecYamlUtil.isPubspecFile(this.virtualFile)
}

private fun PsiFile.readPackageLines(): List<Pair<String, Int>> {
    val linesList = mutableListOf<Pair<String, Int>>()
    var line = ""
    var counter = 0
    text.forEach {
        counter++
        if (it == '\n') {
            line = line.trim()
            if (!line.startsWith("#") && line.isPubPackageName() && line.contains("^")) {
                linesList.add(line to counter - 2)
            }
            line = ""
        } else {
            line += it
        }
    }
    return linesList
}

fun String.isPubPackageName(): Boolean {
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

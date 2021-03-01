package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.exceptions.GetLatestPackageVersionException
import de.mariushoefler.flutterenhancementsuite.pub.DependencyChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

class FileParser(
    private val file: PsiFile,
    private val dependencyChecker: DependencyChecker
) : CoroutineScope {

    private val parentJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)

    override val coroutineContext: CoroutineContext
        get() = scope.coroutineContext

    suspend fun checkFile(): List<VersionDescription> {
        parentJob.cancelChildren(cause = null)

        return if (file.isPubspecFile()) {
            return getVersionsFromFile()
        } else {
            emptyList()
        }
    }

    private suspend fun getVersionsFromFile(): MutableList<VersionDescription> {
        val problemDescriptionList = mutableListOf<VersionDescription>()

        val lines: List<VersionDescription> =
            file.readPackageLines().map { coroutineScope { async { mapToVersionDescription(it) } } }.awaitAll()

        lines.forEach { versionDescription ->
            try {
                if (versionDescription.latestVersion != versionDescription.currentVersion) {
                    problemDescriptionList.add(versionDescription)
                }
            } catch (e: GetLatestPackageVersionException) {
                print(e)
            }
        }
        return problemDescriptionList
    }

    @Throws(GetLatestPackageVersionException::class)
    private fun mapToVersionDescription(it: Pair<String, Int>): VersionDescription {
        val dependency = it.first
        val counter = it.second

        val latestVersion = dependencyChecker.getLatestVersion(dependency)
        val currentVersion = dependency.getCurrentPubPackageVersion()

        return VersionDescription(counter, currentVersion, latestVersion)
    }
}

data class VersionDescription(
    val counter: Int,
    val currentVersion: String,
    val latestVersion: String
)

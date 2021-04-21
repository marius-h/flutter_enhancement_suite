package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.exceptions.GetLatestPackageVersionException
import de.mariushoefler.flutterenhancementsuite.models.VersionDescription

class FileParser(private val file: PsiFile) {

    fun checkFile(): List<VersionDescription> {
        return if (file.isPubspecFile()) {
            getVersionsFromFile()
        } else {
            emptyList()
        }
    }

    private fun getVersionsFromFile(): MutableList<VersionDescription> {
        val problemDescriptionList = mutableListOf<VersionDescription>()
        val packageLines = file.readPackageLines()
        packageLines
            .map { pair -> mapToVersionDescription(pair) }
            .forEach { versionDescription ->
                if (versionDescription.latestVersion != versionDescription.currentVersion) {
                    problemDescriptionList.add(versionDescription)
                }
            }
        return problemDescriptionList
    }

    @Throws(GetLatestPackageVersionException::class)
    private fun mapToVersionDescription(it: Pair<String, Int>): VersionDescription {
        val dependency = it.first
        val counter = it.second

        val latestVersion = PubApi.getPackageLatestVersion(dependency.getPubPackageName())
        val currentVersion = dependency.getCurrentPubPackageVersion()

        return VersionDescription(counter, currentVersion, latestVersion)
    }
}

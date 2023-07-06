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
                val currentVersionWithoutExtras = versionDescription.currentVersion.split("-").first()
                println(
                    "currentVersionWithoutExtras = $currentVersionWithoutExtras to ${versionDescription.latestVersion}"
                )
                if (isNewerVersion(currentVersionWithoutExtras, versionDescription.latestVersion)) {
                    problemDescriptionList.add(versionDescription)
                }
            }
        return problemDescriptionList
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        var vnum1 = 0
        var vnum2 = 0
        var i = 0
        var j = 0
        while (i < current.length || j < latest.length) {
            while (i < current.length && current[i] != '.') {
                vnum1 = (vnum1 * 10 + (current[i] - '0'))
                i++
            }
            while (j < latest.length && latest[j] != '.') {
                vnum2 = (vnum2 * 10 + (latest[j] - '0'))
                j++
            }
            if (vnum1 > vnum2) return false
            if (vnum2 > vnum1) return true

            vnum2 = 0
            vnum1 = vnum2
            i++
            j++
        }
        return false
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

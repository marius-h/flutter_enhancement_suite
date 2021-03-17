package de.mariushoefler.flutterenhancementsuite.pub

import com.google.gson.Gson
import com.intellij.util.containers.getIfSingle
import de.mariushoefler.flutterenhancementsuite.exceptions.GetLatestPackageVersionException
import de.mariushoefler.flutterenhancementsuite.utils.getPubPackageName
import java.net.HttpURLConnection
import java.net.URL

const val PUB_API_URL = "https://pub.dartlang.org/api/packages/"

class DependencyChecker {

    private val dependencyList = mutableListOf<Dependency>()

    fun getLatestVersion(dependency: String): String {
        val packageName = dependency.getPubPackageName()
        val url = URL(PUB_API_URL + packageName)

        val cachedDependency = dependencyList.find { it.packageName == packageName }

        if (cachedDependency != null) {
            return cachedDependency.version
        } else {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val reader = connection.inputStream.bufferedReader()

            reader.lines().getIfSingle()?.let {
                val response = parsePackageResponse(it)
                // Check for latest stable release
                val latest = response.latest.takeUnless { v ->
                    v.version.matches(Regex("^[\\d.]+-.*"))
                } ?: response.versions.reversed().first { v ->
                    !v.version.matches(Regex("^[\\d.]+-.*"))
                }
                val latestVersion = latest.version.trim()
                dependencyList.add(Dependency(packageName, latestVersion))
                return latestVersion
            }

            throw GetLatestPackageVersionException(dependency)
        }
    }

    private fun parsePackageResponse(responseString: String): Response {
        return Gson().fromJson(responseString, Response::class.java)
    }
}

data class Dependency(
    val packageName: String,
    val version: String
)

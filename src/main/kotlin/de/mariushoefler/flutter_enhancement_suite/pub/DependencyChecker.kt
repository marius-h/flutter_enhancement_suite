package de.mariushoefler.flutter_enhancement_suite.pub

import com.google.gson.Gson
import com.intellij.util.containers.getIfSingle
import java.net.HttpURLConnection
import java.net.URL

const val PUB_API_URL = "https://pub.dartlang.org/api/packages/"


class DependencyChecker {

	private val dependencyList = mutableListOf<Dependency>()

	fun getLatestVersion(dependency: String): String {
		val packageName = dependency.trim().split(':')[0]
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
				val latest = response.latest
				val latestVersion = latest.version.trim()
				dependencyList.add(Dependency(packageName, latestVersion))
				return latestVersion
			}

			throw UnableToGetLatestVersionException(dependency)
		}
	}

	private fun parsePackageResponse(responseString: String): Response {
		return Gson().fromJson(responseString, Response::class.java)
	}
}

class UnableToGetLatestVersionException(dependency: String) :
	Exception("Cannot get the latest version number for dependency: $dependency")

data class Dependency(
	val packageName: String,
	val version: String
)

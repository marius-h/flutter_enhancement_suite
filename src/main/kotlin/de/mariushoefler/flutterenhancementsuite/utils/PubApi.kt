package de.mariushoefler.flutterenhancementsuite.utils

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.io.HttpRequests
import de.mariushoefler.flutterenhancementsuite.exceptions.GetLatestPackageVersionException
import de.mariushoefler.flutterenhancementsuite.exceptions.PubApiCouldNotBeReached
import de.mariushoefler.flutterenhancementsuite.exceptions.PubApiUnknownFormat
import de.mariushoefler.flutterenhancementsuite.models.PubPackage
import de.mariushoefler.flutterenhancementsuite.models.PubPackageSearch
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PubApi {
    private val dependencyCache = mutableMapOf<String, String>()

    init {
        FuelManager.instance.basePath = "https://pub.dev/api/"
    }

    private var lastPackages = mapOf<String, PubPackage>()

    fun searchPackage(query: String, page: Int): PubPackageSearch? {
        // +--- Uncomment to test bug report dialog
        // |
        // V
        // val test = listOf(1,2,3)
        // println(test[4])

        try {
            return runWithCheckCanceled {
                val q = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                val response = HttpRequests
                    .request("https://pub.dartlang.org/api/search?q=$q&page=$page")
                    .readString(ProgressManager.getInstance().progressIndicator)
                Gson().fromJson(response, PubPackageSearch::class.java)
            }
        } catch (e: IOException) {
            throw PubApiCouldNotBeReached(e)
        } catch (e: JsonSyntaxException) {
            throw PubApiUnknownFormat(e)
        }
    }

    fun getPackage(name: String): PubPackage? {
        var result: PubPackage? = null

        "packages/${URLEncoder.encode(name, StandardCharsets.UTF_8.toString())}"
            .httpGet()
            .responseObject(PubPackage.Deserializer()) { _, _, res ->
                res.component1()?.let {
                    result = res.get()
                }
            }.join()

        if (!lastPackages.containsKey(name)) {
            lastPackages.map { result to name }
        }

        return result
    }

    @Throws(GetLatestPackageVersionException::class)
    fun getPackageLatestVersion(packageName: String): String {
        dependencyCache[packageName]?.let {
            return it
        }

        getPackage(packageName)?.let {
            val latestVersion = it.getLatestVersion()
            dependencyCache[packageName] = latestVersion
            return latestVersion
        }

        throw GetLatestPackageVersionException(packageName)
    }

    fun getPackageChangelog(packageName: String): String? {
        val pubPackage = lastPackages[packageName]
            ?: getPackage(packageName)

        if (pubPackage?.latest?.pubspec?.homepage == null) return null

        val result = StringBuilder()
        result.append("<html>")
        result.append("<h1>$packageName Changelog</h1>")

        val homepage = pubPackage.latest.pubspec.homepage
        val src: String? = if (homepage.startsWith("https://github.com")) {
            fetchContentsFromGithubFile(homepage, "changelog")
        } else null

        if (!src.isNullOrEmpty()) {
            result.append(GithubApi.formatReadmeAsHtml(src, pubPackage.latest.pubspec.homepage))
        }

        return result.toString()
    }

    fun getPackageDoc(packageName: String, short: Boolean = false): String? {
        val pubPackage = lastPackages[packageName]
            ?: getPackage(packageName)
            ?: return null

        val result = StringBuilder()
        result.append("<html>")
        result.append("<h1>$packageName</h1>")

        pubPackage.latest.pubspec.getAuthorName().let { authors ->
            if (authors.isNotEmpty()) {
                result.append("<small><i>by $authors</i></small><br><br>")
            }
        }

        result.append("<p>${pubPackage.latest.pubspec.description}</p><br>")

        if (!short && pubPackage.latest.pubspec.homepage != null) {
            generateFullDoc(pubPackage.latest.pubspec.homepage, result)
        }

        result.append("</html>")

        return result.toString()
    }

    private fun generateFullDoc(homepage: String, result: StringBuilder) {
        val src: String? = if (homepage.startsWith("https://github.com")) {
            fetchContentsFromGithubFile(homepage, "readme")
        } else null

        result.append("<a href=\"${homepage}\">Visit package's homepage</a><br><br>")

        if (homepage.startsWith("https://github.com")) {
            val examplePath: String = if (homepage.contains("/tree/master")) {
                ""
            } else {
                "/tree/master"
            }
            result.append(
                "<a href=\"${homepage}$examplePath/example\">" +
                    "Show an example of how to use the package</a><br><br>"
            )
        }

        if (!src.isNullOrEmpty()) {
            var html = ""

            src.lines().forEachIndexed { index, line ->
                if (index == 0) {
                    html = line.replace(Regex("^.*#.*"), "")
                } else {
                    html += line.replace(Regex("^.*\\(.*\\.svg.*|^.*img.shields.io.*"), "") + "\n"
                }
            }

            result.append("<br><h2><u>Documentation</u></h2>")

            result.append(GithubApi.formatReadmeAsHtml(html, homepage))
        }
    }

    private fun fetchContentsFromGithubFile(repoUrl: String, filename: String): String? {
        val home = repoUrl
            .removePrefix("https://github.com/")
            .replace("bloc/", "")
            .replace("blob/", "")
            .replace("/pubspec.yaml", "")
            .replace("tree/", "")
        var fileUrl = "https://raw.githubusercontent.com/$home"
        if (!fileUrl.contains("/master")) {
            fileUrl += "/master"
        }

        return (
            fetchReadme("$fileUrl/${filename.toUpperCase()}.md")
                ?: fetchReadme("$fileUrl/${filename.toLowerCase()}.md")
            )?.let { src ->
            if (src.startsWith("./")) {
                // File is referenced in a sub-folder
                fileUrl += src.replaceFirst(".", "")
                fetchReadme(fileUrl)
            } else src
        }
    }

    private fun fetchReadme(filePath: String): String? {
        val response = filePath.httpGet().responseString().third
        if (response.component2() == null) {
            return response.get()
        }
        return null
    }
}

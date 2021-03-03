package de.mariushoefler.flutterenhancementsuite.utils

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.application.ex.ApplicationUtil.runWithCheckCanceled
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.io.HttpRequests
import de.mariushoefler.flutterenhancementsuite.models.PubPackage
import de.mariushoefler.flutterenhancementsuite.models.PubPackageSearch
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PubApi {

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

        return try {
            runWithCheckCanceled {
                val q = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                val response = HttpRequests
                    .request("https://pub.dartlang.org/api/search?q=$q&page=$page")
                    .readString(ProgressManager.getInstance().progressIndicator)
                Gson().fromJson(response, PubPackageSearch::class.java)
            }
        } catch (e: IOException) {
            // context.project.showBalloon("Could not reach pub", NotificationType.WARNING)
            null
        } catch (e: JsonSyntaxException) {
            // context.project.showBalloon("Bad answer from pub", NotificationType.WARNING)
            null
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

    fun getPackageChangelog(packageName: String): String? {
        val pubPackage = lastPackages[packageName]
            ?: getPackage(packageName)

        if (pubPackage?.homepage == null) return null

        val result = StringBuilder()
        result.append("<html>")
        result.append("<h1>$packageName Changelog</h1>")

        val homepage = pubPackage.homepage
        val src: String? = if (homepage.startsWith("https://github.com")) {
            fetchContentsFromGithubFile(homepage, "changelog")
        } else null

        if (!src.isNullOrEmpty()) {
            result.append(GithubApi.formatReadmeAsHtml(src, pubPackage.homepage))
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

        pubPackage.getAuthorName().let { authors ->
            if (authors.isNotEmpty()) {
                result.append("<small><i>by $authors</i></small><br><br>")
            }
        }

        result.append("<p>${pubPackage.description}</p><br>")

        if (!short && pubPackage.homepage != null) {
            generateFullDoc(pubPackage.homepage, result)
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
        var src: String? = null
        val h = repoUrl
            .removePrefix("https://github.com/")
            .replace("bloc/", "")
            .replace("blob/", "")
            .replace("/pubspec.yaml", "")
            .replace("tree/", "")
        var fileUrl = "https://raw.githubusercontent.com/$h"
        if (!fileUrl.contains("/master")) {
            fileUrl += "/master"
        }

        var filePath = "$fileUrl/${filename.toUpperCase()}.md"
        for (i in 1..2) {
            val response = filePath.httpGet().responseString().third
            if (response.component2() == null) {
                src = response.get()
                break
            } else {
                filePath = "$fileUrl/${filename.toLowerCase()}.md"
            }
        }

        if (src != null && src.startsWith("./")) {
            // File is referenced in a sub-folder
            fileUrl += src.replaceFirst(".", "")
            src = fileUrl.httpGet().responseString().third.get()
        }
        return src
    }
}

fun <T> runWithCheckCanceled(callable: () -> T): T =
    runWithCheckCanceled(callable, ProgressManager.getInstance().progressIndicator)

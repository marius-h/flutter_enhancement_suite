package de.mariushoefler.flutterenhancementsuite.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.io.HttpRequests
import de.mariushoefler.flutterenhancementsuite.exceptions.GetLatestPackageVersionException
import de.mariushoefler.flutterenhancementsuite.exceptions.PubApiCouldNotBeReached
import de.mariushoefler.flutterenhancementsuite.exceptions.PubApiUnknownFormat
import de.mariushoefler.flutterenhancementsuite.models.PubPackage
import de.mariushoefler.flutterenhancementsuite.models.PubPackageSearch
import de.mariushoefler.flutterenhancementsuite.models.PubScore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PubApi {
    private val dependencyCache = mutableMapOf<String, String>()

    private var lastPackages = mapOf<String, PubPackage>()

    private val pubApiService by lazy {
        PubApiService.create()
    }

    fun searchPackage(query: String, page: Int): PubPackageSearch? {
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

    @Throws(PubApiCouldNotBeReached::class)
    fun getPackage(name: String, function: (response: Response<PubPackage>) -> Unit) {
        pubApiService.getPackage(name).enqueue(object : Callback<PubPackage> {
            override fun onResponse(call: Call<PubPackage>, response: Response<PubPackage>) {
                if (!lastPackages.containsKey(name)) {
                    lastPackages.map { response.body() to name }
                }

                return function(response)
            }

            override fun onFailure(call: Call<PubPackage>, t: Throwable) {
                throw PubApiCouldNotBeReached(Exception(t))
            }
        })
    }

    @Throws(PubApiCouldNotBeReached::class)
    fun getPackageScore(name: String, function: (response: Response<PubScore>) -> Unit) {
        pubApiService.getPackageScore(name).enqueue(object : Callback<PubScore> {
            override fun onResponse(call: Call<PubScore>, response: Response<PubScore>) {
                return function(response)
            }

            override fun onFailure(call: Call<PubScore>, t: Throwable) {
                throw PubApiCouldNotBeReached(Exception(t))
            }
        })
    }

    @Throws(GetLatestPackageVersionException::class)
    fun getPackageLatestVersion(packageName: String): String {
        dependencyCache[packageName]?.let {
            return it
        }

        pubApiService.getPackage(packageName).execute().let { response ->
            response.body()?.let {
                it.getLatestVersion()?.let { latestVersion ->
                    dependencyCache[packageName] = latestVersion
                    return latestVersion
                }
            }
        }

        throw GetLatestPackageVersionException(packageName)
    }

    fun getPackageChangelog(packageName: String): String? {
        val pubPackage = lastPackages[packageName] ?: pubApiService.getPackage(packageName).execute().body()

        if (pubPackage?.latest?.pubspec?.repository == null) return null

        val result = StringBuilder()
        result.append("<html>")
        result.append("<h1>$packageName Changelog</h1>")

        val repository = pubPackage.latest.pubspec.repository
        val src: String? = if (repository.startsWith("https://github.com")) {
            GithubApi.fetchContentsFromFile(repository, "changelog")
        } else null

        return if (!src.isNullOrEmpty()) {
            result.append(GithubApi.formatMarkdownAsHtml(src, pubPackage.latest.pubspec.repository))
            result.toString()
        } else null
    }

    fun getPackageDoc(packageName: String, short: Boolean = false): String? {
        val pubPackage =
            lastPackages[packageName] ?: pubApiService.getPackage(packageName).execute().body() ?: return null

        val result = StringBuilder()
        result.append("<html>")
        result.append("<h1>$packageName</h1>")

        val pubspec = pubPackage.latest.pubspec
        pubspec.getAuthorName().let { authors ->
            if (authors.isNotEmpty()) {
                result.append("<small><i>by $authors</i></small><br><br>")
            }
        }

        result.append("<p>${pubspec.description}</p><br>")

        if (!short) {
            if (pubspec.repository != null) {
                generateFullPackageDoc(pubspec.repository, result)
            } else if (pubspec.homepage != null) {
                generateFullPackageDoc(pubspec.homepage, result)
            }
        }

        result.append("</html>")

        return result.toString()
    }

    private fun generateFullPackageDoc(repository: String, result: StringBuilder) {
        val src: String? = if (repository.startsWith("https://github.com")) {
            GithubApi.fetchContentsFromFile(repository, "readme")
        } else null

        result.append("<a href=\"${repository}\">Visit package's homepage</a><br><br>")

        if (repository.startsWith("https://github.com")) {
            val examplePath: String = if (repository.contains("/tree/master")) {
                ""
            } else {
                "/tree/master"
            }
            result.append(
                "<a href=\"${repository}$examplePath/example\">" + "Show an example of how to use the package</a><br><br>"
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

            result.append(GithubApi.formatMarkdownAsHtml(html, repository))
        }
    }
}

private interface PubApiService {
    @GET("packages/{name}")
    fun getPackage(@Path("name") name: String): Call<PubPackage>

    @GET("packages/{name}/score")
    fun getPackageScore(@Path("name") name: String): Call<PubScore>

    companion object {
        fun create(): PubApiService {
            val retrofit =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://pub.dev/api/")
                    .build()
            return retrofit.create<PubApiService>()
        }
    }
}

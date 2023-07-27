package de.mariushoefler.flutterenhancementsuite.utils

import de.mariushoefler.flutterenhancementsuite.exceptions.MarkdownParseException
import de.mariushoefler.flutterenhancementsuite.models.RepositoryInfo
import de.mariushoefler.flutterenhancementsuite.models.RepositoryReadme
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url
import java.net.URL

object GithubApi {
    private val githubApiService by lazy { GithubApiService.create() }

    private val jsonGithubApiService by lazy { JsonGithubService.create() }

    fun formatMarkdownAsHtml(text: String, repoUrl: String): String {
        val context = repoUrl.replace(Regex("https?://github.com/"), "")

        val jsonObj = JSONObject()
        jsonObj.put("text", text)
        jsonObj.put("mode", "gfm")
        jsonObj.put("context", context)

        return githubApiService.postMarkdown(jsonObj.toString()).execute().body() ?: throw MarkdownParseException(text)
    }

    fun fetchContentsFromFile(repoUrl: String, filename: String): String? {
        var fileUrl = repoUrl
            .replace("github.com/", "raw.githubusercontent.com/")
            .replace("tree/", "")
            .replace("blob/", "")

        val path = URL(fileUrl).path
        val pathParts = path.split("/")
        if (pathParts.size < 2) return null
        val owner = pathParts[1]
        val repo = pathParts[2]
        jsonGithubApiService.getRepoInfo(owner = owner, repo = repo).execute().body()?.let { repoInfo ->
            val defaultBranch = repoInfo.defaultBranch
            if (!fileUrl.contains("/$defaultBranch")) {
                fileUrl += "/$defaultBranch"
            }
        }

        return fetchRawFile(fileUrl, filename) ?: jsonGithubApiService.getRepoReadme(owner, repo).execute()
            .body()?.content?.let { decodeBase64(it) }
    }

    private fun fetchRawFile(fileUrl: String, filename: String): String? {
        return fetchFileContents("$fileUrl/${filename.uppercase()}.md")
            ?: fetchFileContents("$fileUrl/${filename.lowercase()}.md")
    }

    private fun fetchFileContents(filePath: String): String? {
        return githubApiService.getTextFromFile(filePath).execute().body()
    }
}

private interface GithubApiService {
    @Headers("Content-Type: text/plain")
    @POST("markdown")
    fun postMarkdown(@Body text: String): Call<String>

    @GET
    fun getTextFromFile(@Url filePath: String): Call<String>

    companion object {
        fun create(): GithubApiService {
            return Retrofit.Builder().baseUrl("https://api.github.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create()
        }
    }
}

private interface JsonGithubService {
    @GET("repos/{owner}/{repo}")
    fun getRepoInfo(@Path("owner") owner: String, @Path("repo") repo: String): Call<RepositoryInfo>

    @GET("repos/{owner}/{repo}/readme")
    fun getRepoReadme(@Path("owner") owner: String, @Path("repo") repo: String): Call<RepositoryReadme>

    companion object {
        fun create(): JsonGithubService {
            return Retrofit.Builder().baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create()
        }
    }
}

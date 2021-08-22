package de.mariushoefler.flutterenhancementsuite.utils

import de.mariushoefler.flutterenhancementsuite.exceptions.MarkdownParseException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

object GithubApi {
    private val githubApiService by lazy {
        GithubApiService.create()
    }

    fun formatMarkdownAsHtml(text: String, repoUrl: String): String {
        val context = repoUrl.replace(Regex("https?://github.com/"), "")

        val jsonObj = JSONObject()
        jsonObj.put("text", text)
        jsonObj.put("mode", "gfm")
        jsonObj.put("context", context)

        return githubApiService.postMarkdown(jsonObj.toString()).execute().body() ?: throw MarkdownParseException(text)
    }

    fun fetchContentsFromFile(repoUrl: String, filename: String): String? {
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
            fetchFileContents("$fileUrl/${filename.toUpperCase()}.md")
                ?: fetchFileContents("$fileUrl/${filename.toLowerCase()}.md")
            )?.let { src ->
                if (src.startsWith("./")) {
                    // File is referenced in a sub-folder
                    fileUrl += src.replaceFirst(".", "")
                    fetchFileContents(fileUrl)
                } else src
            }
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
            val retrofit =
                Retrofit.Builder().baseUrl("https://api.github.com/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()

            return retrofit.create(GithubApiService::class.java)
        }
    }
}

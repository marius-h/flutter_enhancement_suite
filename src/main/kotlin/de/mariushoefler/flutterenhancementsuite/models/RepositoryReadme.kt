package de.mariushoefler.flutterenhancementsuite.models

import com.google.gson.annotations.SerializedName

data class RepositoryReadme(
    val content: String,
    @SerializedName("download_url")
    val downloadUrl: String,
    val encoding: String,
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val type: String,
    val url: String
)

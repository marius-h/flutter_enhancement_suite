package de.mariushoefler.flutterenhancementsuite.models

import com.google.gson.annotations.SerializedName

data class RepositoryInfo(@SerializedName("default_branch") val defaultBranch: String)

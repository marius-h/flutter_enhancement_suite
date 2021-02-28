package de.mariushoefler.flutterenhancementsuite.models

import com.google.gson.annotations.SerializedName

data class PubPackageSearch(val packages: List<PubPackageResult>, val next: String?)

data class PubPackageResult(@SerializedName("package") val name: String)

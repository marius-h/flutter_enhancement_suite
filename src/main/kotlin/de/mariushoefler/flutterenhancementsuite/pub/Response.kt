package de.mariushoefler.flutterenhancementsuite.pub

data class Response(
    val latest: Version,
    val versions: List<Version>
)

data class Version(
    val version: String
)

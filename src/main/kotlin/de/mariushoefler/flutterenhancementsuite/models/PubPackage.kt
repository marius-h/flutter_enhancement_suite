package de.mariushoefler.flutterenhancementsuite.models

data class PubPackage(
    val name: String,
    val latest: Version,
    val versions: List<Version>
) {
    fun generateDependencyString() = "$name: ^${getLatestVersion()}"

    fun getLatestVersion(): String? {
        // Check for latest stable release
        val latestVersion = latest.takeUnless { v ->
            v.version.matches(Regex("^[\\d.]+-.*"))
        } ?: versions.reversed().firstOrNull { v ->
            !v.version.matches(Regex("^[\\d.]+-.*"))
        }
        return latestVersion?.version?.trim()
    }

    data class Version(
        val version: String,
        val pubspec: PubspecInfo
    )

    data class PubspecInfo(
        private val version: String,
        private val author: String?,
        private val authors: ArrayList<String>?,
        val description: String?,
        val homepage: String?
    ) {
        fun getAuthorName(): String {
            return if (author != null) {
                author.split("<")[0].trim()
            } else if (!authors.isNullOrEmpty()) {
                val authorsString = if (authors.size - 1 > 0) {
                    " & ${authors.size - 1} more"
                } else {
                    ""
                }
                authors[0].split("<")[0].trim() + authorsString
            } else ""
        }
    }
}

package de.mariushoefler.flutterenhancementsuite.models

data class PubScore(
    val grantedPoints: Int,
    val maxPoints: Int,
    val likeCount: Int,
    val popularityScore: Double,
    val tags: List<String>,
    val lastUpdated: String
)

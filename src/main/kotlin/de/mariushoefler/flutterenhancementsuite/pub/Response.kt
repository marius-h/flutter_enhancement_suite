package de.mariushoefler.flutterenhancementsuite.pub

data class Response(
	val latest: Latest
)

data class Latest(
	val version: String
)

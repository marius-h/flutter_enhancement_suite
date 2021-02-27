package de.mariushoefler.flutter_enhancement_suite.pub

data class Response(
	val latest: Latest
)

data class Latest(
	val version: String
)

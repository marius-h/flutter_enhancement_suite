package pubassist

data class Response(
		val latest: Latest
)

data class Latest(
		val version: String
)
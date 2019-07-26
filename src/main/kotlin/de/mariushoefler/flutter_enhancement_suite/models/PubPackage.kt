package de.mariushoefler.flutter_enhancement_suite.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

class PubPackage(val name: String, private val version: String, private val dependencies: Map<String, Any>, private val author: Any, val homepage: String?) {

	fun authorName() = try {
		val a = author as String
		a.split("<")[0]
	} catch (err: Exception) {
		try {
			println(author)
			val b = author as List<*>
			var authors = ""
			b.forEach {
				val c = it as String
				authors += c
				if (c != b.last() as String) authors += ", "
			}
			authors
		} catch (err: Exception) {
			""
		}
	}

	fun isFlutterCompatible() = dependencies["flutter"] != null

	fun generateDependencyString() = "$name: ^$version"

	class Deserializer : ResponseDeserializable<PubPackage> {
		override fun deserialize(content: String): PubPackage {
			return Gson().fromJson(content, Root::class.java).latest.pubspec
		}
	}

	data class Root(val latest: Latest)

	data class Latest(val pubspec: PubPackage)
}
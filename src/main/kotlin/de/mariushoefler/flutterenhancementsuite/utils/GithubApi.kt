package de.mariushoefler.flutterenhancementsuite.utils

import com.github.kittinunf.fuel.httpPost
import org.json.JSONObject

object GithubApi {

	fun formatReadmeAsHtml(text: String, repoUrl: String): String {
		var context: String
		repoUrl.replace(Regex("https?://github.com/"), "")
			.split("/")
			.let {
				context = "${it[0]}/${it[1]}"
			}

		val jsonObj = JSONObject()
		jsonObj.put("text", text)
		jsonObj.put("mode", "gfm")
		jsonObj.put("context", context)

		var message = ""

		"https://api.github.com/markdown"
			.httpPost()
			.set("Content-Type", "text/plain")
			.body(jsonObj.toString())
			.responseString { _, _, result -> message = result.get() }
			.join()

		return message
	}
}

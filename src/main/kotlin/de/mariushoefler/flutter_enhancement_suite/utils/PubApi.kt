package de.mariushoefler.flutter_enhancement_suite.utils

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.application.ex.ApplicationUtil.runWithCheckCanceled
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.io.HttpRequests
import de.mariushoefler.flutter_enhancement_suite.models.PubPackage
import de.mariushoefler.flutter_enhancement_suite.models.PubPackageSearch
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PubApi {

	init {
		FuelManager.instance.basePath = "https://pub.dartlang.org/api/"
	}

	fun searchPackage(query: String, page: Int): PubPackageSearch? {
		return try {
			runWithCheckCanceled {
				val response = HttpRequests
						.request("https://pub.dartlang.org/api/search?q=${URLEncoder.encode(query, StandardCharsets.UTF_8.toString())}&page=$page")
						.readString(ProgressManager.getInstance().progressIndicator)
				Gson().fromJson(response, PubPackageSearch::class.java)
			}
		} catch (e: IOException) {
			//context.project.showBalloon("Could not reach crates.io", NotificationType.WARNING)
			null
		} catch (e: JsonSyntaxException) {
			//context.project.showBalloon("Bad answer from crates.io", NotificationType.WARNING)
			null
		}
	}

	fun getPackage(name: String): PubPackage? {
		var result: PubPackage? = null

		"packages/${URLEncoder.encode(name, StandardCharsets.UTF_8.toString())}"
				.httpGet()
				.responseObject(PubPackage.Deserializer()) { _, _, res ->
					when (res) {
						is Result.Failure -> throw res.getException()
						is Result.Success -> result = res.get()
					}
				}.join()

		return result
	}
}

fun <T> runWithCheckCanceled(callable: () -> T): T = runWithCheckCanceled(callable, ProgressManager.getInstance().progressIndicator)
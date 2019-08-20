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

	// https://pub.dartlang.org/api/
	init {
		FuelManager.instance.basePath = "https://pub.dartlang.org/api/"
	}

	var lastPackages = mapOf<String, PubPackage>()

	fun searchPackage(query: String, page: Int): PubPackageSearch? {
//		+--- Uncomment to test bug report dialog
//		|
//		V
//		val test = listOf(1,2,3)
//		println(test[4])

		return try {
			runWithCheckCanceled {
				val response = HttpRequests
						.request("https://pub.dartlang.org/api/search?q=${URLEncoder.encode(query, StandardCharsets.UTF_8.toString())}&page=$page")
						.readString(ProgressManager.getInstance().progressIndicator)
				Gson().fromJson(response, PubPackageSearch::class.java)
			}
		} catch (e: IOException) {
			//context.project.showBalloon("Could not reach pub", NotificationType.WARNING)
			null
		} catch (e: JsonSyntaxException) {
			//context.project.showBalloon("Bad answer from pub", NotificationType.WARNING)
			null
		}
	}

	fun getPackage(name: String): PubPackage? {
		var result: PubPackage? = null

		"packages/${URLEncoder.encode(name, StandardCharsets.UTF_8.toString())}"
				.httpGet()
				.responseObject(PubPackage.Deserializer()) { _, _, res ->
					when (res) {
						is Result.Failure -> println("Error")
						is Result.Success -> result = res.get()
					}
				}.join()

		if (!lastPackages.containsKey(name)) {
			lastPackages.map { result to name }
		}

		return result
	}
}

fun <T> runWithCheckCanceled(callable: () -> T): T = runWithCheckCanceled(callable, ProgressManager.getInstance().progressIndicator)
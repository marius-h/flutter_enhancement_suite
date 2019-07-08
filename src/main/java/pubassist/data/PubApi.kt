package pubassist.data

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import pubassist.models.PubPackage
import pubassist.models.PubPackageSearch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PubApi {

	init {
		FuelManager.instance.basePath = "https://pub.dartlang.org/api/"
	}

	fun searchPackage(query: String): PubPackageSearch? {
		var result: PubPackageSearch? = null

		"/search?q=${URLEncoder.encode(query, StandardCharsets.UTF_8.toString())}"
				.httpGet()
				.responseObject<PubPackageSearch> { _, _, res ->
					when (res) {
						is Result.Failure -> throw res.getException()
						is Result.Success -> result = res.get()
					}
				}.join()

		return result
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
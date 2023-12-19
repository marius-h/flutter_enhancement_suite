package de.mariushoefler.flutterenhancementsuite.exceptions

import com.google.gson.JsonSyntaxException

class GetLatestPackageVersionException(p: String) :
    Exception("Cannot get the latest version number for package: $p")

class GetCurrentPackageVersionException(p: String) :
    Exception("Cannot read current version number for package: $p")

class PubApiCouldNotBeReachedException(p: Exception) : Exception("Pub api could not be reached: $p")

class PubApiUnknownFormatException(p: JsonSyntaxException) : Exception("Unexpected response from Pub api: $p")

class AutoDisposeChangeNotifierProviderFamilyThisNameIsWayTooLongToBeDisplayed<T, R>()

fun foo (a: AutoDisposeChangeNotifierProviderFamilyThisNameIsWayTooLongToBeDisplayed<String, String>, b: String) {

}

fun boo() {
    foo(w, b)
}

class Fee {
    private val pubApiService by lazy {
        Tee.create()
    }
}

class Tee {
    companion object {
        fun create(): AutoDisposeChangeNotifierProviderFamilyThisNameIsWayTooLongToBeDisplayed<String, String> {
            return AutoDisposeChangeNotifierProviderFamilyThisNameIsWayTooLongToBeDisplayed()
        }
    }
}

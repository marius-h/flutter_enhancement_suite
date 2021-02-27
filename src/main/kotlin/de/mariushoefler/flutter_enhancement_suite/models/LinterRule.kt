package de.mariushoefler.flutter_enhancement_suite.models

data class LinterRule(val name: String, val description: String) {

	var enabled: Boolean = false

	fun getAsArray(): ArrayList<Any> {
		return arrayListOf(enabled, name, description)
	}
}

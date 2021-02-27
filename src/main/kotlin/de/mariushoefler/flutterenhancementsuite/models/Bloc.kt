package de.mariushoefler.flutter_enhancement_suite.models

import de.mariushoefler.flutter_enhancement_suite.utils.toSnakeCase

data class Bloc(
	val name: String,
	val className: String,
	val projectName: String,
	val stateFilename: String,
	val blocFilename: String,
	val eventFilename: String
) {

	companion object {
		fun build(name: String, projectName: String): Bloc {
			val nameSnakeCase = name.toSnakeCase()
			return Bloc(
				nameSnakeCase,
				name,
				projectName,
				"${nameSnakeCase}_state.dart",
				"${nameSnakeCase}_bloc.dart",
				"${nameSnakeCase}_event.dart"
			)
		}
	}
}

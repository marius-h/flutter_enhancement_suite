package de.mariushoefler.flutterenhancementsuite.models

import de.mariushoefler.flutterenhancementsuite.utils.toSnakeCase

@Deprecated("Use https://plugins.jetbrains.com/plugin/12129-bloc.")
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

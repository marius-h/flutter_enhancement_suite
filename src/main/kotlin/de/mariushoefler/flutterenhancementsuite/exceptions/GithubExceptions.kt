package de.mariushoefler.flutterenhancementsuite.exceptions

class MarkdownParseException(p: String) : Exception("Cannot parse markdown to html: $p")

class NoConnectionException : Exception("Cannot establish connection to Github")

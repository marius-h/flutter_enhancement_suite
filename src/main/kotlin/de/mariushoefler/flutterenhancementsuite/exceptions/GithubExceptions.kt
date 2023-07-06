package de.mariushoefler.flutterenhancementsuite.exceptions

class MarkdownParseException(p: String) : Exception("Cannot parse markdown to html: $p")


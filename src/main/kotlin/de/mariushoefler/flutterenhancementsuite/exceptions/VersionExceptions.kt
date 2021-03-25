@file:Suppress("unused")

package de.mariushoefler.flutterenhancementsuite.exceptions

import java.io.IOException

class FlutterVersionNotFoundException(p: IOException) :
    Exception("Cannot find version of Flutter used: $p")

class DartVersionNotFoundException(p: IOException) :
    Exception("Cannot find version of Dart used: $p")

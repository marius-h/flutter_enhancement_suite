package pubassist

import com.intellij.openapi.diagnostic.Logger

private val LOG = Logger.getInstance(PubPackagesInspection::class.java)

fun printMessage(message: String) {
    println(message)
    LOG.info(message)
}
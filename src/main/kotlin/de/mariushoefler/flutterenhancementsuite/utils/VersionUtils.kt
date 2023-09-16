package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.extensions.PluginId
import de.mariushoefler.flutterenhancementsuite.exceptions.FlutterVersionNotFoundException
import io.flutter.FlutterUtils
import io.flutter.sdk.FlutterSdk
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

const val OUTPUT_STREAM_SIZE = 4096
const val FLUTTER_VERSION_TIMEOUT = 3L

fun fetchThisPluginVersion(builder: StringBuilder) {
    val plugin = PluginManagerCore.getPlugin(PluginId.getId("de.mariushoefler.flutterenhancementsuite"))
    if (plugin != null) {
        builder.append(" • Flutter Enhancement Suite plugin `").append(plugin.version).append("`")
    }
}

fun fetchDartPluginVersion(builder: StringBuilder) {
    val dartPlugin = PluginManagerCore.getPlugin(PluginId.getId("Dart"))
    if (dartPlugin != null) {
        builder.append(" • Dart plugin `").append(dartPlugin.version).append("`")
    }
}

fun fetchFlutterPluginVersion(builder: StringBuilder) {
    val pid = FlutterUtils.getPluginId()
    val flutterPlugin = PluginManagerCore.getPlugin(pid)
    if (flutterPlugin != null) {
        builder.append(" • Flutter plugin `").append(pid.idString).append(' ').append(flutterPlugin.version)
            .append("`")
    }
}

fun fetchIntelliJVersion(builder: StringBuilder) {
    val applicationInfo = ApplicationInfo.getInstance()
    builder.append(applicationInfo.versionName).append(" `").append(applicationInfo.fullVersion).append("`")
}

fun getFlutterVersion(sdk: FlutterSdk): String? {
    return try {
        val flutterPath = sdk.homePath + "/bin/flutter"
        val builder = ProcessBuilder(flutterPath, "--version")
        val process = builder.start()
        if (!process.waitFor(FLUTTER_VERSION_TIMEOUT, TimeUnit.SECONDS)) {
            null
        } else {
            String(readFully(process.inputStream), StandardCharsets.UTF_8)
        }
    } catch (e: IOException) {
        throw FlutterVersionNotFoundException(e)
    }
}

@Throws(IOException::class)
private fun readFully(inputStream: InputStream): ByteArray {
    val out = ByteArrayOutputStream()
    val temp = ByteArray(OUTPUT_STREAM_SIZE)
    var count = inputStream.read(temp)
    while (count > 0) {
        out.write(temp, 0, count)
        count = inputStream.read(temp)
    }
    return out.toByteArray()
}

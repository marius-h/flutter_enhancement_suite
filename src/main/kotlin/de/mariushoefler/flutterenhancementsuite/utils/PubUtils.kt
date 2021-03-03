package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.psi.PsiFile
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import de.mariushoefler.flutterenhancementsuite.exceptions.GetCurrentPackageVersionException
import java.util.regex.Pattern

const val REGEX_DEPENDENCY =
    """^\s*(?!version|sdk|ref|url)\S+:\s*[<|=|>|^]*([0-9]+\.[0-9]+\.[0-9]+\+?\S*)"""
const val PACKAGE_VERSION_OFFSET = 4

fun String.isPubPackageName(): Boolean {
    val regexPattern = Pattern.compile(REGEX_DEPENDENCY)
    return regexPattern.matcher(this).find()
}

fun String.getPubPackageName(): String {
    return this.trim().split(':')[0]
}

fun String.getCurrentPubPackageVersion(): String {
    val regex = REGEX_DEPENDENCY.toRegex()
    return regex.find(this)?.groupValues?.get(1) ?: throw GetCurrentPackageVersionException(this)
}

fun PsiFile.isPubspecFile(): Boolean {
    return PubspecYamlUtil.isPubspecFile(this.virtualFile)
}

fun PsiFile.readPackageLines(): List<Pair<String, Int>> {
    val linesList = mutableListOf<Pair<String, Int>>()
    var line = ""
    var counter = 0
    var dependenciesReached = false
    text.forEach {
        counter++
        if (it == '\n') {
            line = line.trim()
            if (line == "dependencies:") dependenciesReached = true
            if (!line.startsWith("#") && line.isPubPackageName() && dependenciesReached) {
                val lineOffset = line.split(":")[0].length + PACKAGE_VERSION_OFFSET
                linesList.add(line to counter - line.length + lineOffset)
            }
            line = ""
        } else {
            line += it
        }
    }
    return linesList
}

package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.util.DartElementGenerator
import io.flutter.FlutterUtils
import io.flutter.pub.PubRoot

// import 'package:flutter/material.dart';
fun Project.createImportStatement(libraryName: String): PsiElement {
    return DartElementGenerator.createDummyFile(this, "import '$libraryName';").firstChild
}

fun PubRoot.isDartFileInLib(virtualFile: VirtualFile): Boolean {
    return getRelativePath(virtualFile)
        ?.startsWith("lib") == true && FlutterUtils.isDartFile(virtualFile)
}

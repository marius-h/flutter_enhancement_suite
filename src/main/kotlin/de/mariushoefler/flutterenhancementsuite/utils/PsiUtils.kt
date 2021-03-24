package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.lang.dart.psi.DartImportStatement
import com.jetbrains.lang.dart.util.DartElementGenerator
import io.flutter.FlutterUtils
import io.flutter.pub.PubRoot

// import 'package:flutter/material.dart';
fun Project.createImportStatement(libraryName: String): PsiElement {
    return DartElementGenerator.createDummyFile(this, "import '$libraryName';").firstChild
}

fun PsiFile.extractDartImportStatements(): List<PsiElement> {
    val importStatements = mutableListOf<PsiElement>()

    this.children.forEach {
        if (it is DartImportStatement) {
            importStatements.add(it)
        } else if (it !is PsiWhiteSpace) {
            return@forEach
        }
    }

    return importStatements
}

fun PubRoot.isDartFileInLib(virtualFile: VirtualFile): Boolean {
    return getRelativePath(virtualFile)
        ?.startsWith("lib") == true && FlutterUtils.isDartFile(virtualFile)
}

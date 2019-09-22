package de.mariushoefler.flutter_enhancement_suite.utils

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.util.DartElementGenerator

// import 'package:flutter/material.dart';
fun Project.createImportStatement(libraryName: String): PsiElement {
    return DartElementGenerator.createDummyFile(this, "import '$libraryName';").firstChild
}
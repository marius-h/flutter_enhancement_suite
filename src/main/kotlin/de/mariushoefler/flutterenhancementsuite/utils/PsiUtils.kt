package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.lang.dart.psi.DartClassDefinition
import com.jetbrains.lang.dart.psi.DartClassMembers
import com.jetbrains.lang.dart.psi.DartComponent
import com.jetbrains.lang.dart.psi.DartEnumDefinition
import com.jetbrains.lang.dart.psi.DartFunctionDeclarationWithBodyOrNative
import com.jetbrains.lang.dart.psi.DartImportStatement
import com.jetbrains.lang.dart.psi.DartVarDeclarationList
import com.jetbrains.lang.dart.util.DartElementGenerator

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

fun PsiElement.enablesCodeVision(): Boolean {
    if (!manager.isInProject(this)) return false

    return (this is DartComponent || this is DartVarDeclarationList) && (parent is DartClassMembers || parent is DartEnumDefinition) || this is DartClassDefinition || this is DartFunctionDeclarationWithBodyOrNative || this is DartEnumDefinition
}

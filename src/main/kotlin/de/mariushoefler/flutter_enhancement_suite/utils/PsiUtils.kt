package de.mariushoefler.flutter_enhancement_suite.utils

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference
import com.jetbrains.jsonSchema.impl.JsonPointerReferenceProvider
import com.jetbrains.jsonSchema.impl.JsonPointerReferenceProvider.JsonSchemaIdReference
import com.jetbrains.lang.dart.util.DartElementGenerator


// import 'package:flutter/material.dart';
fun Project.createImportStatement(libraryName: String): PsiElement {
	return DartElementGenerator.createDummyFile(this, "import '$libraryName';").firstChild
}

fun Array<PsiReference>.hasFileOrPointerReferences(): Boolean {
	for (reference in this) {
		if (reference is PsiFileReference
				|| reference is JsonSchemaIdReference
				|| reference is JsonPointerReferenceProvider) return true
	}
	return false
}
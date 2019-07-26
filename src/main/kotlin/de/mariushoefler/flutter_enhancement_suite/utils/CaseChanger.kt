package de.mariushoefler.flutter_enhancement_suite.utils

import com.google.common.base.CaseFormat

//fun String.toSnakeCase(): String {
//	var text = ""
//	var isFirst = true
//	this.forEach {
//		if (it.isUpperCase()) {
//			if (isFirst) isFirst = false
//			else text += "_"
//			text += it.toLowerCase()
//		} else {
//			text += it
//		}
//	}
//	return text
//}

fun String.toSnakeCase(): String = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)

fun String.toCamelCase(): String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, this)
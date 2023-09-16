package de.mariushoefler.flutterenhancementsuite.utils

import com.google.common.base.CaseFormat

fun String.toSnakeCase(): String = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)

fun String.toCamelCase(): String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, this)

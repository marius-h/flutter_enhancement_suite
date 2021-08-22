package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.openapi.application.ex.ApplicationUtil
import com.intellij.openapi.progress.ProgressManager

inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}

fun <T> runWithCheckCanceled(callable: () -> T): T =
    ApplicationUtil.runWithCheckCanceled(callable, ProgressManager.getInstance().progressIndicator)


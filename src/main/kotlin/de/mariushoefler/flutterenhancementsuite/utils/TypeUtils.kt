package de.mariushoefler.flutterenhancementsuite.utils

import java.text.DecimalFormat
import java.util.Base64
import kotlin.math.abs

fun formatNumberWithK(likeCount: Int): String {
    return if (abs(likeCount) > 999) DecimalFormat("#.#").format(
        likeCount.toDouble() / 1000.0
    ) + "k" else likeCount.toString()
}

fun decodeBase64(base64String: String): String {
    val cleanedBase64String = base64String.replace("\n", "")
    val decodedBytes = Base64.getDecoder().decode(cleanedBase64String)
    return String(decodedBytes)
}

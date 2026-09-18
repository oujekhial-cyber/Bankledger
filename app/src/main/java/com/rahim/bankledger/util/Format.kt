package com.rahim.bankledger.util

import java.text.NumberFormat
import java.util.Locale

private val faLocale: Locale = Locale.Builder().setLanguage("fa").setRegion("IR").build()

fun toToman(rial: Long): Long = rial / 10

/** Formats a whole number with Persian digit grouping, e.g. ۱٬۲۵۰٬۰۰۰ */
fun formatFaNumber(n: Long): String {
    return NumberFormat.getNumberInstance(faLocale).format(n)
}

fun formatAmount(rial: Long, unit: String): String {
    val value = if (unit == "toman") toToman(rial) else rial
    return formatFaNumber(value)
}

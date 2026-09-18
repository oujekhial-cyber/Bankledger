package com.rahim.bankledger.sms

object BankSmsParser {

    data class ParsedSms(
        val type: String, // "income" or "expense"
        val amountRial: Long,
        val cardHint: String?,
        val bankHint: String?,
        val raw: String
    )

    private const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"
    private const val ARABIC_DIGITS = "٠١٢٣٤٥٦٧٨٩"

    val BANKS = listOf(
        "ملی", "ملت", "صادرات", "تجارت", "سامان", "پاسارگاد",
        "سپه", "کشاورزی", "رفاه", "شهر", "پارسیان"
    )

    private val incomeRegex = Regex("واریز|دریافت|بستانکار")
    private val expenseRegex = Regex("برداشت|خرید|پرداخت|بدهکار")
    private val amountRegex = Regex("([\\d,٬]{4,})\\s*(ریال|تومان)")
    private val cardRegex = Regex("(?:کارت|حساب)[^\\d]{0,15}(\\d{4})")

    fun toLatinDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (c in input) {
            val p = PERSIAN_DIGITS.indexOf(c)
            val a = ARABIC_DIGITS.indexOf(c)
            when {
                p >= 0 -> sb.append(p)
                a >= 0 -> sb.append(a)
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    /** Returns null when the text doesn't look like a bank transaction message at all. */
    fun parse(raw: String): ParsedSms? {
        val text = toLatinDigits(raw)
        val isIncome = incomeRegex.containsMatchIn(text)
        val isExpense = expenseRegex.containsMatchIn(text)
        if (!isIncome && !isExpense) return null

        val amountMatch = amountRegex.find(text) ?: return null
        var amount = amountMatch.groupValues[1].replace(",", "").replace("٬", "").toLongOrNull() ?: return null
        if (amountMatch.groupValues[2] == "تومان") amount *= 10

        val cardHint = cardRegex.find(text)?.groupValues?.getOrNull(1)
        val bankHint = BANKS.firstOrNull { text.contains(it) }

        return ParsedSms(
            type = if (isIncome) "income" else "expense",
            amountRial = amount,
            cardHint = cardHint,
            bankHint = bankHint,
            raw = raw
        )
    }
}

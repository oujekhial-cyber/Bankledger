package com.rahim.bankledger.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.rahim.bankledger.ui.theme.Amber400
import com.rahim.bankledger.ui.theme.Emerald500
import com.rahim.bankledger.ui.theme.Fuchsia500
import com.rahim.bankledger.ui.theme.Orange400
import com.rahim.bankledger.ui.theme.Pink500
import com.rahim.bankledger.ui.theme.Rose500
import com.rahim.bankledger.ui.theme.Teal500
import com.rahim.bankledger.ui.theme.Violet600

data class CategoryMeta(val name: String, val emoji: String, val gradient: Brush)

private val Slate500 = Color(0xFF64748B)
private val Sky500 = Color(0xFF0EA5E9)
private val Blue500 = Color(0xFF3B82F6)
private val Lime500 = Color(0xFF84CC16)
private val Gray400 = Color(0xFF9CA3AF)

val EXPENSE_CATEGORIES = listOf(
    CategoryMeta("خوراک و سوپرمارکت", "🛒", Brush.linearGradient(listOf(Amber400, Orange400))),
    CategoryMeta("حمل‌ونقل و سوخت", "⛽", Brush.linearGradient(listOf(Sky500, Blue500))),
    CategoryMeta("قبض و اجاره", "🏠", Brush.linearGradient(listOf(Violet600, Fuchsia500))),
    CategoryMeta("درمان و دارو", "💊", Brush.linearGradient(listOf(Rose500, Color(0xFFEF4444)))),
    CategoryMeta("خرید کالا یا قطعه", "📦", Brush.linearGradient(listOf(Teal500, Emerald500))),
    CategoryMeta("تعمیر خودرو", "🔧", Brush.linearGradient(listOf(Slate500, Color(0xFF475569)))),
    CategoryMeta("برداشت نقدی", "💵", Brush.linearGradient(listOf(Lime500, Emerald500))),
    CategoryMeta("سایر", "✨", Brush.linearGradient(listOf(Gray400, Color(0xFF6B7280))))
)

val INCOME_CATEGORIES = listOf(
    CategoryMeta("فروش خودرو", "🚗", Brush.linearGradient(listOf(Emerald500, Teal500))),
    CategoryMeta("حقوق", "💼", Brush.linearGradient(listOf(Blue500, Color(0xFF6366F1)))),
    CategoryMeta("انتقال از مشتری یا همکار", "🤝", Brush.linearGradient(listOf(Fuchsia500, Pink500))),
    CategoryMeta("سود بانکی", "🏦", Brush.linearGradient(listOf(Amber400, Color(0xFFEAB308)))),
    CategoryMeta("سایر", "✨", Brush.linearGradient(listOf(Gray400, Color(0xFF6B7280))))
)

fun findCategoryMeta(name: String?): CategoryMeta {
    return (EXPENSE_CATEGORIES + INCOME_CATEGORIES).firstOrNull { it.name == name }
        ?: CategoryMeta(name ?: "", "✨", Brush.linearGradient(listOf(Gray400, Color(0xFF6B7280))))
}

val ACCOUNT_GRADIENTS = listOf(
    Brush.linearGradient(listOf(Violet600, Color(0xFF9333EA))),
    Brush.linearGradient(listOf(Blue500, Color(0xFF06B6D4))),
    Brush.linearGradient(listOf(Emerald500, Color(0xFF0D9488))),
    Brush.linearGradient(listOf(Rose500, Pink500)),
    Brush.linearGradient(listOf(Amber400, Orange400))
)

val BANKS = listOf("ملی", "ملت", "صادرات", "تجارت", "سامان", "پاسارگاد", "سپه", "کشاورزی", "رفاه", "شهر", "پارسیان", "دیگر")

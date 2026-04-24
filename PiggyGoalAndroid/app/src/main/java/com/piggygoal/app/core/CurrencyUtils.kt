package com.piggygoal.app.core

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatCurrency(symbol: String, amount: Double): String {
    val formatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
    return "$symbol${formatter.format(amount)}"
}

fun progressFor(currentAmount: Double, targetAmount: Double): Float {
    if (targetAmount <= 0.0) return 0f
    return (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)
}

package com.piggygoal.app.domain.model

data class UserSettings(
    val currencySymbol: String = "₱",
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
)

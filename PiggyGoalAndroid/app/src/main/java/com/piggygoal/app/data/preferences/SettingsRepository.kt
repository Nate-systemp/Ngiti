package com.piggygoal.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.piggygoal.app.domain.model.AppThemeMode
import com.piggygoal.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "piggygoal_settings")

class SettingsRepository(private val context: Context) {
    val userSettings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            currencySymbol = preferences[Keys.CurrencySymbol] ?: "₱",
            themeMode = preferences[Keys.ThemeMode]?.let(AppThemeMode::valueOf) ?: AppThemeMode.SYSTEM,
        )
    }

    suspend fun updateCurrencySymbol(symbol: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.CurrencySymbol] = symbol
        }
    }

    suspend fun updateThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ThemeMode] = themeMode.name
        }
    }

    private object Keys {
        val CurrencySymbol: Preferences.Key<String> = stringPreferencesKey("currency_symbol")
        val ThemeMode: Preferences.Key<String> = stringPreferencesKey("theme_mode")
    }
}

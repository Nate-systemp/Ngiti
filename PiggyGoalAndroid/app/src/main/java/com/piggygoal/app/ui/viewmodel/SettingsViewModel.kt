package com.piggygoal.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piggygoal.app.data.preferences.SettingsRepository
import com.piggygoal.app.domain.model.AppThemeMode
import com.piggygoal.app.domain.model.UserSettings
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val currencySymbol: String = "₱",
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
)

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = repository.userSettings
        .map { settings ->
            SettingsUiState(
                currencySymbol = settings.currencySymbol,
                themeMode = settings.themeMode,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun updateCurrency(symbol: String) {
        viewModelScope.launch {
            repository.updateCurrencySymbol(symbol)
        }
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            repository.updateThemeMode(themeMode)
        }
    }
}

class SettingsViewModelFactory(
    private val repository: SettingsRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository) as T
    }
}

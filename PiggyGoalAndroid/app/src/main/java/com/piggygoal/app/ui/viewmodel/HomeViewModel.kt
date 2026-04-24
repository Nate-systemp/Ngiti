package com.piggygoal.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piggygoal.app.data.repository.PiggyGoalRepository
import com.piggygoal.app.domain.model.Goal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val goals: List<Goal> = emptyList(),
)

class HomeViewModel(
    repository: PiggyGoalRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = repository.observeGoals()
        .map { HomeUiState(goals = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )
}

class HomeViewModelFactory(
    private val repository: PiggyGoalRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}

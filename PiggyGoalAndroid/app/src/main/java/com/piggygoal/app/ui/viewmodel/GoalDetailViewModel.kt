package com.piggygoal.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piggygoal.app.data.repository.PiggyGoalRepository
import com.piggygoal.app.domain.model.GoalDetail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalDetailUiState(
    val detail: GoalDetail? = null,
)

sealed interface GoalDetailEvent {
    data object Deleted : GoalDetailEvent
}

class GoalDetailViewModel(
    private val goalId: Long,
    private val repository: PiggyGoalRepository,
) : ViewModel() {
    val uiState: StateFlow<GoalDetailUiState> = repository.observeGoalDetail(goalId)
        .map { GoalDetailUiState(detail = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GoalDetailUiState(),
        )

    private val _events = MutableSharedFlow<GoalDetailEvent>()
    val events: SharedFlow<GoalDetailEvent> = _events.asSharedFlow()

    fun addDeposit(amount: Double, note: String?) {
        viewModelScope.launch {
            if (amount > 0) {
                repository.addDeposit(goalId, amount, note)
            }
        }
    }

    fun deleteGoal() {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            _events.emit(GoalDetailEvent.Deleted)
        }
    }
}

class GoalDetailViewModelFactory(
    private val goalId: Long,
    private val repository: PiggyGoalRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalDetailViewModel(goalId, repository) as T
    }
}

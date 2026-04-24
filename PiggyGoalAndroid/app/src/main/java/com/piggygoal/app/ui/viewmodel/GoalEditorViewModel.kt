package com.piggygoal.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piggygoal.app.core.GoalValidator
import com.piggygoal.app.data.preferences.SettingsRepository
import com.piggygoal.app.data.repository.PiggyGoalRepository
import com.piggygoal.app.domain.model.GoalInput
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalEditorUiState(
    val goalId: Long? = null,
    val name: String = "",
    val selectedEmoji: String = "🐷",
    val targetAmount: String = "",
    val currentAmount: String = "",
    val targetDate: LocalDate = LocalDate.now().plusDays(30),
    val currency: String = "₱",
    val isEditMode: Boolean = false,
    val validationMessage: String? = null,
)

sealed interface GoalEditorEvent {
    data object Saved : GoalEditorEvent
}

class GoalEditorViewModel(
    private val goalId: Long?,
    private val repository: PiggyGoalRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalEditorUiState(goalId = goalId, isEditMode = goalId != null))
    val uiState: StateFlow<GoalEditorUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GoalEditorEvent>()
    val events: SharedFlow<GoalEditorEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.userSettings.first()
            if (goalId == null) {
                _uiState.update { it.copy(currency = settings.currencySymbol) }
            } else {
                repository.getGoal(goalId)?.let { goal ->
                    _uiState.update {
                        it.copy(
                            goalId = goal.id,
                            name = goal.name,
                            selectedEmoji = goal.emoji,
                            targetAmount = goal.targetAmount.toString(),
                            currentAmount = goal.currentAmount.toString(),
                            targetDate = goal.targetDate,
                            currency = goal.currency,
                            isEditMode = true,
                        )
                    }
                }
            }
        }
    }

    fun onNameChanged(value: String) = updateState { copy(name = value, validationMessage = null) }
    fun onEmojiSelected(value: String) = updateState { copy(selectedEmoji = value) }
    fun onTargetAmountChanged(value: String) = updateState { copy(targetAmount = value.filterNumeric(), validationMessage = null) }
    fun onCurrentAmountChanged(value: String) = updateState { copy(currentAmount = value.filterNumeric(), validationMessage = null) }
    fun onTargetDateChanged(value: LocalDate) = updateState { copy(targetDate = value, validationMessage = null) }

    fun save() {
        viewModelScope.launch {
            val input = uiState.value.toGoalInput() ?: run {
                _uiState.update { it.copy(validationMessage = "Enter valid numeric values before saving.") }
                return@launch
            }
            val validationMessage = GoalValidator.validate(input)
            if (validationMessage != null) {
                _uiState.update { it.copy(validationMessage = validationMessage) }
                return@launch
            }

            if (goalId == null) {
                repository.createGoal(input)
            } else {
                repository.updateGoal(goalId, input)
            }
            _events.emit(GoalEditorEvent.Saved)
        }
    }

    private fun GoalEditorUiState.toGoalInput(): GoalInput? {
        val target = targetAmount.toDoubleOrNull() ?: return null
        val current = currentAmount.ifBlank { "0" }.toDoubleOrNull() ?: return null
        return GoalInput(
            name = name.trim(),
            emoji = selectedEmoji,
            targetAmount = target,
            currentAmount = current,
            targetDate = targetDate,
            currency = currency,
        )
    }

    private fun updateState(transform: GoalEditorUiState.() -> GoalEditorUiState) {
        _uiState.update(transform)
    }

    private fun String.filterNumeric(): String = filter { it.isDigit() || it == '.' }
}

class GoalEditorViewModelFactory(
    private val goalId: Long?,
    private val repository: PiggyGoalRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalEditorViewModel(goalId, repository, settingsRepository) as T
    }
}

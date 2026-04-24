package com.piggygoal.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.piggygoal.app.core.toDisplayString
import com.piggygoal.app.core.toLocalDateFromUtcMillis
import com.piggygoal.app.ui.viewmodel.GoalEditorEvent
import com.piggygoal.app.ui.viewmodel.GoalEditorViewModel
import java.time.LocalDate

private val emojiChoices = listOf("🐷", "🏖️", "🚗", "🏠", "🎓", "✈️", "💍", "📱")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GoalEditorScreen(
    viewModel: GoalEditorViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is GoalEditorEvent.Saved) onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Goal" else "New Goal") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Goal name") },
                singleLine = true,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pick an emoji", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    emojiChoices.forEach { emoji ->
                        Surface(
                            modifier = Modifier.clickable { viewModel.onEmojiSelected(emoji) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (uiState.selectedEmoji == emoji) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            },
                        ) {
                            Text(
                                text = emoji,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }
            }
            OutlinedTextField(
                value = uiState.targetAmount,
                onValueChange = viewModel::onTargetAmountChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Target amount") },
                prefix = { Text(uiState.currency) },
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.currentAmount,
                onValueChange = viewModel::onCurrentAmountChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Current saved amount") },
                prefix = { Text(uiState.currency) },
                singleLine = true,
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Target date", style = MaterialTheme.typography.labelLarge)
                    Text(uiState.targetDate.toDisplayString(), style = MaterialTheme.typography.titleMedium)
                }
            }
            uiState.validationMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Text("Save Goal")
            }
        }
    }

    if (showDatePicker) {
        GoalDatePicker(
            initialDate = uiState.targetDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                viewModel.onTargetDateChanged(it)
                showDatePicker = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDatePicker(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val initialMillis = remember(initialDate) {
        initialDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis.toLocalDateFromUtcMillis().isAfter(LocalDate.now())
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.toLocalDateFromUtcMillis()?.let(onDateSelected)
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

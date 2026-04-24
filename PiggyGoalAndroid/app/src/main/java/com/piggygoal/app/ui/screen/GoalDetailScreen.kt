package com.piggygoal.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.piggygoal.app.core.daysRemainingFromToday
import com.piggygoal.app.core.formatCurrency
import com.piggygoal.app.core.progressFor
import com.piggygoal.app.domain.model.Deposit
import com.piggygoal.app.ui.viewmodel.GoalDetailEvent
import com.piggygoal.app.ui.viewmodel.GoalDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalDetailViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDepositSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is GoalDetailEvent.Deleted) onDeleted()
        }
    }

    val detail = uiState.detail
    if (detail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Goal not found.")
        }
        return
    }

    val goal = detail.goal
    val remainingAmount = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
    val daysLeft = goal.targetDate.daysRemainingFromToday()
    val progress = progressFor(goal.currentAmount, goal.targetAmount)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${goal.emoji} ${goal.name}") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit goal")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete goal")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(190.dp),
                            strokeWidth = 14.dp,
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text("Saved", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            title = "Saved",
                            value = formatCurrency(goal.currency, goal.currentAmount),
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            title = "Remaining",
                            value = formatCurrency(goal.currency, remainingAmount),
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            title = "Days left",
                            value = daysLeft.toString(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Button(
                        onClick = { showDepositSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Add Deposit")
                    }
                }
            }
            item {
                Text("Deposit History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            if (detail.deposits.isEmpty()) {
                item {
                    Text(
                        text = "No deposits yet. Add one to start building momentum.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(detail.deposits, key = { it.id }) { deposit ->
                    DepositRow(currency = goal.currency, deposit = deposit)
                }
            }
        }
    }

    if (showDepositSheet) {
        AddDepositSheet(
            currency = goal.currency,
            onDismiss = { showDepositSheet = false },
            onSave = { amount, note ->
                viewModel.addDeposit(amount, note)
                showDepositSheet = false
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this goal?") },
            text = { Text("This removes the goal and all deposit history stored locally on the device.") },
            confirmButton = {
                TextButton(onClick = viewModel::deleteGoal) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.height(96.dp), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DepositRow(
    currency: String,
    deposit: Deposit,
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(formatCurrency(currency, deposit.amount), style = MaterialTheme.typography.titleMedium)
            Text(
                text = deposit.date.toLocalDate().toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            deposit.note?.let { note ->
                Text(note, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDepositSheet(
    currency: String,
    onDismiss: () -> Unit,
    onSave: (Double, String?) -> Unit,
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Add Deposit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { char -> char.isDigit() || char == '.' } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Amount") },
                prefix = { Text(currency) },
                singleLine = true,
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Note (optional)") },
                singleLine = false,
            )
            Button(
                onClick = {
                    amount.toDoubleOrNull()?.takeIf { it > 0 }?.let { onSave(it, note) }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save Deposit")
            }
        }
    }
}

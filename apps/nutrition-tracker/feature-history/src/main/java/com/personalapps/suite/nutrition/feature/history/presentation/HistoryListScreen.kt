package com.personalapps.suite.nutrition.feature.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.nutrition.feature.api.model.HistoryEntry
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.designsystem.carbsColor
import com.personalapps.suite.shared.designsystem.fatColor
import com.personalapps.suite.shared.designsystem.proteinColor
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.SwipeToDeleteContainer
import java.time.format.DateTimeFormatter

@Composable
fun HistoryListScreen(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    PersonalScaffold(
        title = "History",
        onBackClick = onBackClick,
        modifier = modifier
    ) { padding ->
        if (state.history.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No history available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.history,
                    key = { it.date.toString() }
                ) { entry ->
                    SwipeToDeleteContainer(
                        onDelete = { viewModel.deleteHistoryEntry(entry) },
                        confirmTitle = "Delete History Entry",
                        confirmMessage = "Are you sure you want to delete the history entry for ${entry.date}?"
                    ) {
                        HistoryEntryItem(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryEntryItem(
    entry: HistoryEntry,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
    
    PersonalCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = entry.date.format(dateFormatter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${entry.totalCalories} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NutrientSummary(label = "P", value = entry.totalProtein, color = proteinColor)
                NutrientSummary(label = "C", value = entry.totalCarbs, color = carbsColor)
                NutrientSummary(label = "F", value = entry.totalFat, color = fatColor)
            }
        }
    }
}

@Composable
private fun NutrientSummary(label: String, value: Float, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = "$label: ${value.toInt()}g",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

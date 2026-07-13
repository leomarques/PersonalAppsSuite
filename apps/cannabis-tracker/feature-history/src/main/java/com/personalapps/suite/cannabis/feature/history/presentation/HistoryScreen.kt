package com.personalapps.suite.cannabis.feature.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.shared.common.DateUtils
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is HistoryEffect.LogDeleted -> snackbarHostState.showSnackbar("Consumption entry deleted")
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(state.logs, searchQuery) {
        if (searchQuery.isBlank()) {
            state.logs
        } else {
            state.logs.filter {
                it.strainName.contains(searchQuery, ignoreCase = true) ||
                        it.method.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    PersonalScaffold(
        title = "Consumption Logs",
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            PersonalTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search by strain or method...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Log History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredLogs.isEmpty()) {
                val msg = if (searchQuery.isBlank()) {
                    "No consumption entries logged yet."
                } else {
                    "No entries matching '$searchQuery'."
                }
                EmptyScreen(message = msg)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredLogs) { log ->
                        HistoryLogItemCard(
                            log = log,
                            onDelete = { viewModel.deleteLog(log) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryLogItemCard(
    log: CannabisLog,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    PersonalCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${log.strainName} (${log.method})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${log.amountGrams} g",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateUtils.formatDateTime(log.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (log.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notes: ${log.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete log entry",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

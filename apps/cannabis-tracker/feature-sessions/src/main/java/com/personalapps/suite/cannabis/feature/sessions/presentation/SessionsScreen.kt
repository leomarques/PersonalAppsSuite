package com.personalapps.suite.cannabis.feature.sessions.presentation

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.personalapps.suite.shared.designsystem.PersonalButton
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalDropdownMenu
import com.personalapps.suite.shared.uicomponents.PersonalScaffold
import com.personalapps.suite.shared.uicomponents.PersonalTextField
import com.personalapps.suite.shared.uicomponents.components.SessionCard

@Composable
fun SessionsScreen(
    viewModel: SessionsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeSessionState.collectAsState()
    val allLogs by viewModel.logsState.collectAsState()

    var sessionTitleInput by remember { mutableStateOf("") }
    var strainName by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("Vape") }
    var amountStr by remember { mutableStateOf("0.25") }
    var notes by remember { mutableStateOf("") }

    val methods = listOf("Vape", "Joint", "Edible", "Pipe", "Bong")

    PersonalScaffold(
        title = "Cannabis Tracker",
        onBackClick = onBackClick,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (activeSession == null) {
                    PersonalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Start Usage Session",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            PersonalTextField(
                                value = sessionTitleInput,
                                onValueChange = { sessionTitleInput = it },
                                label = "Session Title (e.g. Evening Chill)"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PersonalButton(
                                text = "Start Session",
                                onClick = {
                                    if (sessionTitleInput.isNotBlank()) {
                                        viewModel.startSession(sessionTitleInput)
                                        sessionTitleInput = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    val currentSession = activeSession!!
                    SessionCard(
                        title = "Active Session: ${currentSession.title}",
                        startTime = currentSession.startTime,
                        onEndClick = { viewModel.endActiveSession(currentSession) }
                    )
                }
            }

            item {
                PersonalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (activeSession != null) "Log Usage (In Session)" else "Quick Log (Out of Session)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        PersonalTextField(
                            value = strainName,
                            onValueChange = { strainName = it },
                            label = "Strain / Variety (e.g. Sour Diesel)"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            PersonalDropdownMenu(
                                options = methods,
                                selectedOption = method,
                                onOptionSelected = { method = it },
                                label = "Method",
                                modifier = Modifier.weight(1.2f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PersonalTextField(
                                value = amountStr,
                                onValueChange = { amountStr = it },
                                label = "Amount (g)",
                                modifier = Modifier.weight(0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PersonalTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "Notes (e.g. creative, relaxed)"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PersonalButton(
                            text = "Record Consumption",
                            onClick = {
                                val amount = amountStr.toFloatOrNull() ?: 0f
                                if (strainName.isNotBlank() && amount > 0f) {
                                    viewModel.logUsage(
                                        sessionId = activeSession?.id,
                                        strainName = strainName,
                                        method = method,
                                        amountGrams = amount,
                                        notes = notes
                                    )
                                    strainName = ""
                                    notes = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            val currentSessionId = activeSession?.id
            if (currentSessionId != null) {
                val sessionLogs = allLogs.filter { it.sessionId == currentSessionId }
                if (sessionLogs.isNotEmpty()) {
                    item {
                        Text(
                            text = "Logged in this session",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(sessionLogs) { log ->
                        ActiveSessionLogCard(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveSessionLogCard(
    log: CannabisLog,
    modifier: Modifier = Modifier
) {
    PersonalCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${log.strainName} (${log.method})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (log.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = log.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${log.amountGrams} g",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

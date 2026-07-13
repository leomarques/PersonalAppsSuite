package com.personalapps.suite.cannabis.feature.stats.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.designsystem.EmptyScreen
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.uicomponents.PersonalScaffold

data class MethodShare(
    val method: String,
    val totalAmount: Float,
    val percentage: Float,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.logsState.collectAsState()
    val sessions by viewModel.sessionsState.collectAsState()

    // Calculate core statistics (using domain models: CannabisLog, CannabisSession)
    val totalLogs = logs.size
    val totalSessions = sessions.size
    val totalAmountGrams = remember(logs) { logs.sumOf { it.amountGrams.toDouble() }.toFloat() }
    val avgConsumption = remember(totalAmountGrams, totalLogs) {
        if (totalLogs == 0) 0f else totalAmountGrams / totalLogs
    }

    // Pie chart colors
    val chartColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.outline,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer
    )

    // Calculate method shares
    val methodShares = remember(logs, totalAmountGrams) {
        if (totalAmountGrams == 0f) {
            emptyList()
        } else {
            logs.groupBy { it.method }
                .mapValues { entry -> entry.value.sumOf { it.amountGrams.toDouble() }.toFloat() }
                .entries
                .sortedByDescending { it.value }
                .mapIndexed { index, entry ->
                    val color = chartColors[index % chartColors.size]
                    MethodShare(
                        method = entry.key,
                        totalAmount = entry.value,
                        percentage = (entry.value / totalAmountGrams) * 100f,
                        color = color
                    )
                }
        }
    }

    PersonalScaffold(
        title = "Personal Stats",
        onBackClick = onBackClick,
        modifier = modifier
    ) { padding ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyScreen(message = "Log consumption entries to view personal consumption metrics and statistics.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Consumption Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            label = "Total Weight",
                            value = "%.2f g".format(totalAmountGrams),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Average / Log",
                            value = "%.2f g".format(avgConsumption),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            label = "Total Logs",
                            value = totalLogs.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Sessions Logged",
                            value = totalSessions.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Method Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    PersonalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(160.dp)
                            ) {
                                PieChart(shares = methodShares, modifier = Modifier.fillMaxSize())
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                methodShares.forEach { share ->
                                    LegendItem(share = share)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    PersonalCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PieChart(
    shares: List<MethodShare>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        var startAngle = -90f
        shares.forEach { share ->
            val sweepAngle = (share.percentage / 100f) * 360f
            drawArc(
                color = share.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LegendItem(
    share: MethodShare,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Surface(
            shape = CircleShape,
            color = share.color,
            modifier = Modifier.size(10.dp)
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${share.method}: %.1f%%".format(share.percentage),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

package com.personalapps.suite.cannabis.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personalapps.suite.cannabis.R
import com.personalapps.suite.cannabis.data.ProjectionPreferences
import com.personalapps.suite.cannabis.data.ThemePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val projectionMinutes by viewModel.projectionIntervalMinutes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.theme_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(Modifier.selectableGroup()) {
                ThemeOptionRow(
                    label = stringResource(R.string.theme_light),
                    selected = theme == ThemePreference.LIGHT,
                    onClick = { viewModel.setTheme(ThemePreference.LIGHT) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                ThemeOptionRow(
                    label = stringResource(R.string.theme_dark),
                    selected = theme == ThemePreference.DARK,
                    onClick = { viewModel.setTheme(ThemePreference.DARK) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                ThemeOptionRow(
                    label = stringResource(R.string.theme_dynamic),
                    selected = theme == ThemePreference.DYNAMIC,
                    onClick = { viewModel.setTheme(ThemePreference.DYNAMIC) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.projection_interval),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.projection_interval_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = formatProjectionInterval(projectionMinutes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Slider(
                value = projectionMinutes.toFloat(),
                onValueChange = { viewModel.setProjectionIntervalMinutes(it.toInt()) },
                valueRange = ProjectionPreferences.MIN_MINUTES.toFloat()..ProjectionPreferences.MAX_MINUTES.toFloat(),
                steps = ((ProjectionPreferences.MAX_MINUTES - ProjectionPreferences.MIN_MINUTES) / 30) - 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private fun formatProjectionInterval(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0 && mins > 0) {
        "${hours}h ${mins}m"
    } else if (hours > 0) {
        "${hours}h"
    } else {
        "${mins}m"
    }
}

package com.personalapps.suite.shared.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun NutrientPortionDialog(
    title: String,
    proteinPerServing: Float,
    carbsPerServing: Float,
    fatPerServing: Float,
    caloriesPerServing: Int,
    onDismiss: () -> Unit,
    onConfirm: (amountGrams: Float) -> Unit,
    initialAmountGrams: Float = 100f,
    gramsPerServing: Float = 100f,
    confirmLabel: String? = null
) {
    val finalConfirmLabel = confirmLabel ?: stringResource(R.string.add_to_day)
    var logUnit by remember { mutableStateOf("Servings") }
    var amountStr by remember(initialAmountGrams, gramsPerServing, logUnit) { 
        mutableStateOf(
            if (logUnit == "Servings") (initialAmountGrams / gramsPerServing).let { if (it % 1 == 0f) it.toInt().toString() else "%.1f".format(it).replace(',', '.') }
            else initialAmountGrams.let { if (it % 1 == 0f) it.toInt().toString() else "%.1f".format(it).replace(',', '.') }
        )
    }

    val updateAmount: (Float) -> Unit = { delta ->
        val current = amountStr.replace(',', '.').toFloatOrNull() ?: 0f
        val newValue = (current + delta).coerceAtLeast(0f)
        amountStr = if (newValue % 1 == 0f) newValue.toInt().toString() else "%.1f".format(newValue).replace(',', '.')
    }

    val focusRequester = remember { FocusRequester() }

    val amountValue = amountStr.replace(',', '.').toFloatOrNull() ?: 0f
    val currentAmountGrams = if (logUnit == "Servings") amountValue * gramsPerServing else amountValue
    val factor = if (gramsPerServing > 0) currentAmountGrams / gramsPerServing else 0f
    
    val totalProtein = proteinPerServing * factor
    val totalCarbs = carbsPerServing * factor
    val totalFat = fatPerServing * factor
    val totalCalories = (caloriesPerServing * factor).toInt()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NutrientRow(
                    protein = totalProtein,
                    carbs = totalCarbs,
                    fat = totalFat,
                    leadingSubtitle = stringResource(R.string.total_calories_with_grams, totalCalories, currentAmountGrams.toInt())
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (logUnit == "Servings") {
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.servings))
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                val currentGrams = if (logUnit == "Servings") (amountStr.replace(',', '.').toFloatOrNull() ?: 0f) * gramsPerServing else amountStr.replace(',', '.').toFloatOrNull() ?: 0f
                                logUnit = "Servings"
                                val servings = currentGrams / gramsPerServing
                                amountStr = if (servings % 1 == 0f) servings.toInt().toString() else "%.1f".format(servings).replace(',', '.')
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.servings))
                        }
                    }

                    if (logUnit == "Grams") {
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.grams_g))
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                val currentGrams = if (logUnit == "Servings") (amountStr.replace(',', '.').toFloatOrNull() ?: 0f) * gramsPerServing else amountStr.replace(',', '.').toFloatOrNull() ?: 0f
                                logUnit = "Grams"
                                amountStr = if (currentGrams % 1 == 0f) currentGrams.toInt().toString() else "%.1f".format(currentGrams).replace(',', '.')
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.grams_g))
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { updateAmount(if (logUnit == "Servings") -0.5f else -50f) }
                    ) {
                        Text(
                            text = "−", // Using Unicode minus sign
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    PersonalTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = if (logUnit == "Grams") stringResource(R.string.amount_grams) else stringResource(R.string.servings_with_grams, gramsPerServing.toInt()),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        focusRequester = focusRequester,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { updateAmount(if (logUnit == "Servings") 0.5f else 50f) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.increase)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amountStr.replace(',', '.').toFloatOrNull() ?: 0f
                    if (amountValue > 0f) {
                        val amountGrams = if (logUnit == "Servings") amountValue * gramsPerServing else amountValue
                        onConfirm(amountGrams)
                    }
                }
            ) {
                Text(finalConfirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

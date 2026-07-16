package com.personalapps.suite.shared.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NutrientPortionDialog(
    title: String,
    proteinPer100g: Float,
    carbsPer100g: Float,
    fatPer100g: Float,
    caloriesPer100g: Int,
    onDismiss: () -> Unit,
    onConfirm: (amountGrams: Float) -> Unit,
    initialAmountGrams: Float = 100f,
    confirmLabel: String = "Add to Day"
) {
    var logUnit by remember { mutableStateOf("Servings") }
    var amountStr by remember { 
        mutableStateOf(
            if (logUnit == "Servings") (initialAmountGrams / 100f).let { if (it % 1 == 0f) it.toInt().toString() else "%.1f".format(it) }
            else initialAmountGrams.let { if (it % 1 == 0f) it.toInt().toString() else "%.1f".format(it) }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NutrientRow(
                    protein = proteinPer100g,
                    carbs = carbsPer100g,
                    fat = fatPer100g,
                    leadingSubtitle = "Per 100g: $caloriesPer100g kcal"
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
                            Text("Servings")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                logUnit = "Servings"
                                amountStr = "1"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Servings")
                        }
                    }

                    if (logUnit == "Grams") {
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Grams (g)")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                logUnit = "Grams"
                                amountStr = "100"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Grams (g)")
                        }
                    }
                }

                PersonalTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = if (logUnit == "Grams") "Amount (grams)" else "Number of servings (1 serving = 100g)"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amountStr.toFloatOrNull() ?: 0f
                    if (amountValue > 0f) {
                        val amountGrams = if (logUnit == "Servings") amountValue * 100f else amountValue
                        onConfirm(amountGrams)
                    }
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

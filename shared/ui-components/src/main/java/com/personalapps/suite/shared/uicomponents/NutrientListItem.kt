package com.personalapps.suite.shared.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personalapps.suite.shared.designsystem.PersonalCard
import com.personalapps.suite.shared.designsystem.carbsColor
import com.personalapps.suite.shared.designsystem.fatColor
import com.personalapps.suite.shared.designsystem.proteinColor

@Composable
fun NutrientListItem(
    title: String,
    protein: Float,
    carbs: Float,
    fat: Float,
    modifier: Modifier = Modifier,
    calories: Int? = null,
    leadingSubtitle: String? = null,
    trailingSubtitle: String? = null,
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    PersonalCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = titleStyle,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                NutrientRow(
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    leadingSubtitle = leadingSubtitle,
                    trailingSubtitle = trailingSubtitle
                )
            }
            
            if (trailingContent != null) {
                trailingContent()
            } else if (calories != null) {
                Text(
                    text = "$calories kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun NutrientRow(
    protein: Float,
    carbs: Float,
    fat: Float,
    modifier: Modifier = Modifier,
    leadingSubtitle: String? = null,
    trailingSubtitle: String? = null,
) {
    Column(modifier = modifier){
        if (leadingSubtitle != null) {
            Text(
                text = leadingSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NutrientText(label = "P", value = protein, color = proteinColor)
            Bullet()
            NutrientText(label = "C", value = carbs, color = carbsColor)
            Bullet()
            NutrientText(label = "F", value = fat, color = fatColor)

            if (trailingSubtitle != null) {
                Text(
                    text = trailingSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun NutrientText(label: String, value: Float, color: Color) {
    val formattedValue = if (value % 1 == 0f) value.toInt().toString() else "%.1f".format(value)
    Text(
        text = "$label: ${formattedValue}g",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun Bullet() {
    Text(
        text = "•",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

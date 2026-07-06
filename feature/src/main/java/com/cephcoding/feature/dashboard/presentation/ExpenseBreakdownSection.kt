package com.cephcoding.feature.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.ui.theme.CategoryInventory
import com.cephcoding.core.ui.theme.CategoryTransport
import com.cephcoding.core.ui.theme.CategoryUtilities
import com.cephcoding.core.ui.theme.TextSecondary

@Composable
 fun ExpenseBreakdownSection(
    breakdown: Map<ExpenseCategory, Double>,
    totalExpenses: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Expense Allocation", style = MaterialTheme.typography.titleLarge)

            breakdown.forEach { (category, amount) ->
                val ratio = if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0f
                val color = when (category) {
                    ExpenseCategory.INVENTORY -> CategoryInventory
                    ExpenseCategory.TRANSPORT -> CategoryTransport
                    ExpenseCategory.UTILITIES -> CategoryUtilities
                    else -> TextSecondary
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = category.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "%.0f%%".format(ratio * 100), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = ratio,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = color,
                        trackColor = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
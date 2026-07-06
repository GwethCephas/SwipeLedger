package com.cephcoding.feature.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cephcoding.core.ui.theme.GreenIncome
import com.cephcoding.core.ui.theme.RedExpense
import com.cephcoding.core.ui.theme.TextSecondary

@Composable
fun MetricsHeaderCard(
    netCashFlow: Double,
    income: Double,
    expenses: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Net Cash Flow", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))

            // Format to 2 decimal points smoothly
            Text(
                text = "Ksh %,.2f".format(netCashFlow),
                style = MaterialTheme.typography.displayLarge,
                color = if (netCashFlow >= 0) GreenIncome else RedExpense
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Total Income", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Text(text = "Ksh %,.2f".format(income), style = MaterialTheme.typography.titleLarge, color = GreenIncome)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Total Expenses", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Text(text = "Ksh %,.2f".format(expenses), style = MaterialTheme.typography.titleLarge, color = RedExpense)
                }
            }
        }
    }
}
package com.cephcoding.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.ui.theme.GreenIncome
import com.cephcoding.core.ui.theme.RedExpense

@Composable
fun TransactionRow(transaction: RawTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = transaction.party,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            // Styled small pill badge highlighting the assigned tracking category
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = (transaction.category ?: ExpenseCategory.UNCATEGORIZED).name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} Ksh%,.2f".format(
                transaction.amount
            ),
            style = MaterialTheme.typography.titleLarge,
            color = if (transaction.type == TransactionType.INCOME) GreenIncome else RedExpense,
            fontWeight = FontWeight.Bold
        )
    }
}
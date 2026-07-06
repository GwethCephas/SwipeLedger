package com.cephcoding.feature.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cephcoding.core.ui.theme.TextSecondary

@Composable
fun DashboardContent(state: DashboardUiState.Success) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Business Cash Metrics Summary Card
        item {
            MetricsHeaderCard(
                netCashFlow = state.netCashFlow,
                income = state.totalIncome,
                expenses = state.totalExpenses
            )
        }

        // 2. Budget Categorization Breakdown Block
        if (state.expenseBreakdown.isNotEmpty()) {
            item {
                ExpenseBreakdownSection(breakdown = state.expenseBreakdown, totalExpenses = state.totalExpenses)
            }
        }

        // 3. Historical Text Logs Header Label
        item {
            Text(
                text = "Recent Ledgers",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Empty Feed Fallback Layout Hook
        if (state.transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions parsed yet.\nIncoming business SMS updates will show up here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // 4. Reactive List Rows
        items(state.transactions, key = { it.transactionId }) { transaction ->
            TransactionRow(transaction = transaction)
        }
    }
}
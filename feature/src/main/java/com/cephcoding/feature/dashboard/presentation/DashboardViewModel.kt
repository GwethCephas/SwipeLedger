package com.cephcoding.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val repository: TransactionRepository
): ViewModel() {

    val uiState: StateFlow<DashboardUiState> = repository.getAllTransactions()
        .map { transactions ->
            if (transactions.isEmpty()) {
                DashboardUiState.Success(
                    transactions = emptyList(),
                    totalIncome = 0.0,
                    totalExpenses = 0.0,
                    netCashFlow = 0.0,
                    expenseBreakdown = emptyMap()
                )
            } else {
                val totalIncome = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val totalExpenses = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                // Group expenses by category type and calculate the total weight per category
                val breakdown = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category ?: ExpenseCategory.UNCATEGORIZED }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                DashboardUiState.Success(
                    transactions = transactions,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    netCashFlow = totalIncome - totalExpenses,
                    expenseBreakdown = breakdown
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

}
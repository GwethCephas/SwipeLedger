package com.cephcoding.feature.dashboard.presentation

import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.feature.dashboard.model.DailyFlow

interface DashboardUiState {
    object Loading : DashboardUiState

    data class Success(
        val transactions: List<RawTransaction>,
        val totalIncome: Double,
        val totalExpenses: Double,
        val netCashFlow: Double,
        val expenseBreakdown: Map<ExpenseCategory, Double>,
        val weeklyFlow: List<DailyFlow>
    ) : DashboardUiState
}
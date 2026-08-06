package com.cephcoding.feature.dashboard.presentation

import com.cephcoding.core.domain.model.Currency
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionCategory
import com.cephcoding.feature.dashboard.model.DailyFlow

interface DashboardUiState {
    object Loading : DashboardUiState

    data class Success(
        val transactions: List<RawTransaction>,
        val totalIncome: Double,
        val totalExpenses: Double,
        val netCashFlow: Double,
        val expenseBreakdown: Map<TransactionCategory, Double>,
        val weeklyFlow: List<DailyFlow>,
        val currency: Currency
    ) : DashboardUiState
}
package com.cephcoding.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cephcoding.core.domain.model.category
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.domain.repository.CurrencyPreferenceRepository
import com.cephcoding.core.domain.repository.TransactionRepository
import com.cephcoding.feature.dashboard.model.DailyFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardViewModel(
    private val repository: TransactionRepository,
    private val currencyPreferenceRepository: CurrencyPreferenceRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAllTransactions(),
        currencyPreferenceRepository.selectedCurrency
    ) { transactions, currency ->
            val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            if (transactions.isEmpty()) {
                DashboardUiState.Success(
                    transactions = emptyList(),
                    totalIncome = 0.0,
                    totalExpenses = 0.0,
                    netCashFlow = 0.0,
                    expenseBreakdown = emptyMap(),
                    weeklyFlow = daysOfWeek.map { DailyFlow(it, 0f) },
                    currency = currency
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
                    .groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val dateFormat = SimpleDateFormat("EEE", Locale.US)
                val weeklyFlowMap = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = it.timestamp
                        }
                        dateFormat.format(calendar.time)
                    }
                    .mapValues { it.value.sumOf { it.amount }.toFloat() }

                val weeklyFlow = daysOfWeek.map { day ->
                    DailyFlow(day, weeklyFlowMap[day] ?: 0f)
                }

                DashboardUiState.Success(
                    transactions = transactions,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    netCashFlow = totalIncome - totalExpenses,
                    expenseBreakdown = breakdown,
                    weeklyFlow = weeklyFlow,
                    currency = currency
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )
}
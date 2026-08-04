package com.cephcoding.feature.dashboard.presentation

import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionCategory
import com.cephcoding.core.domain.model.TransactionSubcategory
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.domain.repository.TransactionRepository
import com.cephcoding.feature.dashboard.common.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TransactionRepository = mockk()
    private val transactionFlow = MutableStateFlow<List<RawTransaction>>(emptyList())

    private lateinit var viewModel: DashboardViewModel

    private fun setupViewModel() {
        every { repository.getAllTransactions() } returns transactionFlow
        viewModel = DashboardViewModel(repository)
    }

    @Test
    fun `initial state emits Loading before data flows`() = runTest {
        setupViewModel()

        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `empty transaction list maps cleanly to zero values`() = runTest {
        setupViewModel()

        transactionFlow.value = emptyList()

        val state = viewModel.uiState.first { it is DashboardUiState.Success }
        assertTrue(state is DashboardUiState.Success)

        val successState = state as DashboardUiState.Success
        assertEquals(0.0, successState.totalIncome, 0.0)
        assertEquals(0.0, successState.totalExpenses, 0.0)
        assertEquals(0.0, successState.netCashFlow, 0.0)
        assertTrue(successState.expenseBreakdown.isEmpty())
    }

    @Test
    fun `populated records compute math aggregates and grouped categories accurately`() = runTest {
        setupViewModel()

        val mockTransactions = listOf(
            RawTransaction(
                transactionId = "1",
                amount = 5000.0,
                party = "Client Pay",
                type = TransactionType.INCOME,
                subcategory = TransactionSubcategory.GENERAL_INCOME,
                rawBody = "Text"
            ),
            RawTransaction(
                transactionId = "2",
                amount = 1500.0,
                party = "Wholesale Ltd",
                type = TransactionType.EXPENSE,
                subcategory = TransactionSubcategory.SHOPPING_AND_ELECTRONICS,
                rawBody = "Text"
            ),
            RawTransaction(
                transactionId = "3",
                amount = 500.0,
                party = "Shell Station",
                type = TransactionType.EXPENSE,
                subcategory = TransactionSubcategory.FUEL_AND_GAS_STATIONS,
                rawBody = "Text"
            ),
            RawTransaction(
                transactionId = "4",
                amount = 1000.0,
                party = "Stock Supplier",
                type = TransactionType.EXPENSE,
                subcategory = TransactionSubcategory.SHOPPING_AND_ELECTRONICS,
                rawBody = "Text"
            )
        )

        transactionFlow.value = mockTransactions

        val state = viewModel.uiState.first { it is DashboardUiState.Success }
        assertTrue(state is DashboardUiState.Success)

        val successState = state as DashboardUiState.Success

        assertEquals(5000.0, successState.totalIncome, 0.0)
        assertEquals(3000.0, successState.totalExpenses, 0.0)
        assertEquals(2000.0, successState.netCashFlow, 0.0)

        val breakdown = successState.expenseBreakdown
        assertEquals(2500.0, breakdown[TransactionCategory.PERSONAL_CARE_AND_SHOPPING] ?: 0.0, 0.0)
        assertEquals(500.0, breakdown[TransactionCategory.TRANSPORTATION] ?: 0.0, 0.0)
    }
}
package com.cephcoding.feature.transactions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMuted
import com.cephcoding.feature.dashboard.presentation.DashboardUiState
import com.cephcoding.feature.dashboard.presentation.DashboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionsScreen(
    dashboardViewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var debouncedSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedSearchQuery = searchQuery
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomSearch(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        TransactionPicker(
            selectedIndex = pagerState.currentPage,
            onIndexSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        when (uiState) {
            DashboardUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DashboardUiState.Success -> {
                val state = (uiState as DashboardUiState.Success)

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) { pageIndex ->
                    val filteredTransactions = remember(pageIndex, state.transactions, debouncedSearchQuery) {
                        val typeFiltered = when (pageIndex) {
                            1 -> state.transactions.filter { it.type == TransactionType.INCOME }
                            2 -> state.transactions.filter { it.type == TransactionType.EXPENSE }
                            else -> state.transactions
                        }

                        if (debouncedSearchQuery.isEmpty()) {
                            typeFiltered
                        } else {
                            typeFiltered.filter { transaction ->
                                transaction.party.contains(debouncedSearchQuery, ignoreCase = true) ||
                                        transaction.category?.name?.contains(debouncedSearchQuery, ignoreCase = true) == true ||
                                        transaction.amount.toString().contains(debouncedSearchQuery)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            val title = when (pageIndex) {
                                1 -> "Recent Incomes"
                                2 -> "Recent Expenses"
                                else -> "Recent Ledgers"
                            }
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextHighEmphasis
                            )
                        }


                        if (filteredTransactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val emptyMessage = when (pageIndex) {
                                        1 -> "No income transactions found."
                                        2 -> "No expense transactions found."
                                        else -> "No transactions parsed yet.\nIncoming business SMS updates will show up here."
                                    }
                                    Text(
                                        text = emptyMessage,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextMuted,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }

                        items(
                            filteredTransactions,
                            key = { it.transactionId }
                        ) { transaction ->
                            TransactionRow(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}
package com.cephcoding.feature.dashboard.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cephcoding.core.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cephcoding.core.common.util.AutoStartPermissionManager
import com.cephcoding.core.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                item {
                    CustomTopBar()
                }

                item {
                    MetricsHeaderCard(
                        netCashFlow = state.netCashFlow,
                        income = state.totalIncome,
                        expenses = state.totalExpenses
                    )
                }
                item {
                    WeeklyCashFlowCard(
                        data = state.weeklyFlow
                    )
                }

                if (state.expenseBreakdown.isNotEmpty()) {
                    item {
                        ExpenseBreakdownSection(
                            breakdown = state.expenseBreakdown,
                            totalExpenses = state.totalExpenses
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun CustomTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = "SWIPELEDGER",
                color = DarkTealPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Financial Overview",
                style = MaterialTheme.typography.titleLarge,
                color = TextMuted,
                fontSize = 16.sp,
                letterSpacing = 1.5.sp
            )
        }
        Row(
            modifier = Modifier
                .border(
                    1.dp,
                    DarkTealPrimary,
                    RoundedCornerShape(20.dp)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(15.dp),
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = "Secure Local only",
                tint = BrightCyanAccent
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                text = "Secure · Local-Only",
                style = MaterialTheme.typography.labelMedium,
                color = BrightCyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

        }
    }
}
package com.cephcoding.swipeledger.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cephcoding.core.domain.model.NavRoutes
import com.cephcoding.core.ui.theme.ObsidianBg
import com.cephcoding.feature.common.BottomNavBar
import com.cephcoding.feature.dashboard.presentation.DashboardScreen
import com.cephcoding.feature.dashboard.presentation.DashboardViewModel
import com.cephcoding.feature.events.presentation.EventsScreen
import com.cephcoding.feature.profile.presentation.ProfileScreen
import com.cephcoding.feature.transactions.presentation.TransactionsScreen


@Composable
fun NavGraph(
    dashboardViewModel: DashboardViewModel
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        containerColor = ObsidianBg
    ) { innerPadding ->

        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = NavRoutes.Dashboard.route
        ) {
            composable(NavRoutes.Dashboard.route) {
                DashboardScreen(dashboardViewModel = dashboardViewModel)
            }
            composable(NavRoutes.Transactions.route) {
                TransactionsScreen(dashboardViewModel = dashboardViewModel)
            }
            composable(NavRoutes.Events.route) {
                EventsScreen()
            }
            composable(NavRoutes.Profile.route) {
                ProfileScreen()
            }
        }
    }


}
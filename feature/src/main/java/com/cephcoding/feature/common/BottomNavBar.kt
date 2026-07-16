package com.cephcoding.feature.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cephcoding.core.domain.model.NavRoutes
import com.cephcoding.core.ui.theme.*
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.getValue

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val bottomBarList = listOf(
        NavRoutes.Dashboard,
        NavRoutes.Transactions,
        NavRoutes.Events,
        NavRoutes.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        containerColor = ObsidianBg
    ) {
        bottomBarList.forEach { navRoute ->
            val isSelected = navRoute.route == currentRoute
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(navRoute.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrightCyanAccent,
                    selectedTextColor = BrightCyanAccent,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = DarkTealPrimary.copy(alpha = 0.2f)
                ),
                icon = {
                    navRoute.icon?.let { iconRes ->
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = navRoute.title,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                },
                label = {
                    Text(
                        text = navRoute.title
                    )
                }
            )
        }
    }
}

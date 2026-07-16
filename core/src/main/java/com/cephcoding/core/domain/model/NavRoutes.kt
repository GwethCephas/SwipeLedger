package com.cephcoding.core.domain.model

import com.cephcoding.core.R

sealed class NavRoutes(
    val route: String,
    val title: String,
    val icon: Int? = null
) {
    object Dashboard : NavRoutes(
        route = "overview",
        title = "Overview",
        icon = R.drawable.ic_overview,
    )

    object Transactions : NavRoutes(
        route = "transactions",
        title = "Transactions",
        icon = R.drawable.ic_transactions,
    )

    object Events : NavRoutes(
        route = "events",
        title = "Events",
        icon = R.drawable.ic_events,
    )

    object Profile : NavRoutes(
        route = "profile",
        title = "Profile",
        icon = R.drawable.ic_profile,
    )

}
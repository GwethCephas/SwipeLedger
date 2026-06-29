package com.cephcoding.feature.dashboard.di

import com.cephcoding.feature.dashboard.presentation.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dashboardModule = module {

    viewModel { DashboardViewModel(get()) }
}
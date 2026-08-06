package com.cephcoding.feature.profile.presentation

import com.cephcoding.core.domain.model.Currency

interface ProfileUiState {
    object Loading : ProfileUiState

    data class Success(
        val selectedCurrency: Currency,
        val isBusy: Boolean = false
    ) : ProfileUiState
}

package com.cephcoding.core.domain.repository

import com.cephcoding.core.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyPreferenceRepository {
    val selectedCurrency: Flow<Currency>
    suspend fun setCurrency(currency: Currency)
}

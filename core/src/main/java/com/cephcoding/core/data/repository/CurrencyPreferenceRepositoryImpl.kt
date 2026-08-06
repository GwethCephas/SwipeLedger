package com.cephcoding.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cephcoding.core.data.datastore.userPreferencesDataStore
import com.cephcoding.core.domain.model.Currency
import com.cephcoding.core.domain.repository.CurrencyPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyPreferenceRepositoryImpl(
    private val context: Context
) : CurrencyPreferenceRepository {

    private object Keys {
        val CURRENCY = stringPreferencesKey("selected_currency")
    }

    override val selectedCurrency: Flow<Currency> = context.userPreferencesDataStore.data
        .map { prefs -> Currency.entries.find { it.name == prefs[Keys.CURRENCY] } ?: Currency.KES }

    override suspend fun setCurrency(currency: Currency) {
        context.userPreferencesDataStore.edit { it[Keys.CURRENCY] = currency.name }
    }
}

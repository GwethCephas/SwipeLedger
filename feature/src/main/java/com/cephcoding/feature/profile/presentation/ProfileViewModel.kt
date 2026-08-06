package com.cephcoding.feature.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cephcoding.core.domain.model.Currency
import com.cephcoding.core.domain.repository.CurrencyPreferenceRepository
import com.cephcoding.core.domain.repository.LocalDataRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val currencyPreferenceRepository: CurrencyPreferenceRepository,
    private val localDataRepository: LocalDataRepository
) : ViewModel() {

    private val isBusy = MutableStateFlow(false)

    val uiState: StateFlow<ProfileUiState> = combine(
        currencyPreferenceRepository.selectedCurrency, isBusy
    ) { currency, busy ->
        ProfileUiState.Success(selectedCurrency = currency, isBusy = busy)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState.Loading
    )

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onCurrencySelected(currency: Currency) {
        viewModelScope.launch { currencyPreferenceRepository.setCurrency(currency) }
    }

    fun onBackupRequested(uri: Uri) = runBusy {
        localDataRepository.backupDatabase(uri)
            .onSuccess { _events.send(ProfileEvent.Message("Backup saved successfully.")) }
            .onFailure { _events.send(ProfileEvent.Message("Backup failed: ${it.message}")) }
    }

    fun onCsvExportRequested(uri: Uri) = runBusy {
        localDataRepository.exportTransactionsCsv(uri)
            .onSuccess { _events.send(ProfileEvent.Message("CSV exported successfully.")) }
            .onFailure { _events.send(ProfileEvent.Message("CSV export failed: ${it.message}")) }
    }

    fun onClearAllDataConfirmed() = runBusy {
        localDataRepository.clearAllTransactions()
            .onSuccess { _events.send(ProfileEvent.Message("All transaction data cleared.")) }
            .onFailure { _events.send(ProfileEvent.Message("Failed to clear data: ${it.message}")) }
    }

    private fun runBusy(block: suspend () -> Unit) {
        viewModelScope.launch {
            isBusy.value = true
            try {
                block()
            } finally {
                isBusy.value = false
            }
        }
    }
}

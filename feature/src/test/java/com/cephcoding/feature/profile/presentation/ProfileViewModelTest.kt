package com.cephcoding.feature.profile.presentation

import android.net.Uri
import com.cephcoding.core.domain.model.Currency
import com.cephcoding.core.domain.repository.CurrencyPreferenceRepository
import com.cephcoding.core.domain.repository.LocalDataRepository
import com.cephcoding.feature.dashboard.common.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val currencyPreferenceRepository: CurrencyPreferenceRepository = mockk()
    private val localDataRepository: LocalDataRepository = mockk()
    private val currencyFlow = MutableStateFlow(Currency.KES)
    private val uri: Uri = mockk()

    private fun setupViewModel(): ProfileViewModel {
        every { currencyPreferenceRepository.selectedCurrency } returns currencyFlow
        return ProfileViewModel(currencyPreferenceRepository, localDataRepository)
    }

    @Test
    fun `selecting a currency delegates to the preference repository`() = runTest {
        val viewModel = setupViewModel()
        coEvery { currencyPreferenceRepository.setCurrency(Currency.USD) } returns Unit

        viewModel.onCurrencySelected(Currency.USD)
        advanceUntilIdle()

        coVerify { currencyPreferenceRepository.setCurrency(Currency.USD) }
    }

    @Test
    fun `successful backup emits a success message and clears busy state`() = runTest {
        val viewModel = setupViewModel()
        coEvery { localDataRepository.backupDatabase(uri) } returns Result.success(Unit)

        val events = mutableListOf<ProfileEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.onBackupRequested(uri)
        advanceUntilIdle()

        val state = viewModel.uiState.first { it is ProfileUiState.Success } as ProfileUiState.Success
        assertEquals(false, state.isBusy)
        assertEquals(1, events.size)
        assertEquals("Backup saved successfully.", (events[0] as ProfileEvent.Message).text)
        job.cancel()
    }

    @Test
    fun `failed CSV export surfaces the failure message`() = runTest {
        val viewModel = setupViewModel()
        coEvery { localDataRepository.exportTransactionsCsv(uri) } returns
            Result.failure(IllegalStateException("disk full"))

        val events = mutableListOf<ProfileEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.onCsvExportRequested(uri)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals("CSV export failed: disk full", (events[0] as ProfileEvent.Message).text)
        job.cancel()
    }

    @Test
    fun `clearing all data delegates to the local data repository`() = runTest {
        val viewModel = setupViewModel()
        coEvery { localDataRepository.clearAllTransactions() } returns Result.success(Unit)

        val events = mutableListOf<ProfileEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.onClearAllDataConfirmed()
        advanceUntilIdle()

        coVerify { localDataRepository.clearAllTransactions() }
        assertEquals("All transaction data cleared.", (events[0] as ProfileEvent.Message).text)
        job.cancel()
    }
}

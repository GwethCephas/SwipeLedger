package com.cephcoding.core.domain.repository

import android.net.Uri

interface LocalDataRepository {
    suspend fun backupDatabase(uri: Uri): Result<Unit>
    suspend fun exportTransactionsCsv(uri: Uri): Result<Unit>
    suspend fun clearAllTransactions(): Result<Unit>
}

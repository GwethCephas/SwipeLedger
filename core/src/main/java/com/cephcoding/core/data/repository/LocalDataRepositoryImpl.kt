package com.cephcoding.core.data.repository

import android.content.Context
import android.net.Uri
import com.cephcoding.core.data.csv.buildTransactionsCsv
import com.cephcoding.core.data.database.TransactionDatabase
import com.cephcoding.core.domain.repository.LocalDataRepository
import com.cephcoding.core.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LocalDataRepositoryImpl(
    private val context: Context,
    private val transactionDatabase: TransactionDatabase,
    private val transactionRepository: TransactionRepository
) : LocalDataRepository {

    override suspend fun backupDatabase(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            transactionDatabase.openHelper.writableDatabase
                .query("PRAGMA wal_checkpoint(TRUNCATE)", arrayOf())
                .close()

            val dbFile = context.getDatabasePath("Transaction_db")
            val out = context.contentResolver.openOutputStream(uri)
                ?: error("Unable to open output stream for backup")
            out.use { stream -> dbFile.inputStream().use { it.copyTo(stream) } }
            Unit
        }
    }

    override suspend fun exportTransactionsCsv(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val transactions = transactionRepository.getAllTransactions().first()
            val out = context.contentResolver.openOutputStream(uri)
                ?: error("Unable to open output stream for CSV export")
            out.bufferedWriter().use { it.write(buildTransactionsCsv(transactions)) }
        }
    }

    override suspend fun clearAllTransactions(): Result<Unit> = runCatching {
        transactionRepository.deleteAllTransactions()
    }
}

package com.cephcoding.core.data.repository

import android.util.Log
import com.cephcoding.core.data.database.TransactionsDao
import com.cephcoding.core.data.mapper.toRawTransaction
import com.cephcoding.core.data.mapper.toTransactionEntity
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val transactionsDao: TransactionsDao
) : TransactionRepository {
    override suspend fun insertTransaction(transaction: RawTransaction) {
        try {
            Log.d("SwipeLedgerDB", "Attempting to insert transaction: ${transaction.transactionId}")
            transactionsDao.insertTransaction(transactionEntity = transaction.toTransactionEntity())
            Log.d("SwipeLedgerDB", "Successfully inserted transaction: ${transaction.transactionId}")
        } catch (e: Exception) {
            Log.e("SwipeLedgerDB", "Failed to insert transaction ${transaction.transactionId}: ${e.message}", e)
        }
    }

    override fun getAllTransactions(): Flow<List<RawTransaction>> {
        return transactionsDao.getAllTransactions().map { entities ->
            entities.map { entity -> entity.toRawTransaction() }
        }

    }

    override fun getAllTransactionsByType(type: String): Flow<List<RawTransaction>> {
        return transactionsDao.getTransactionsByType(type).map { entities ->
            entities.map { entity -> entity.toRawTransaction() }
        }
    }
}
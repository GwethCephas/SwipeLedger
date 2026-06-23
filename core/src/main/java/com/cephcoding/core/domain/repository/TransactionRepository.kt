package com.cephcoding.core.domain.repository

import com.cephcoding.core.domain.model.RawTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insertTransaction(transaction: RawTransaction)
    fun getAllTransactions(): Flow<List<RawTransaction>>

    fun getAllTransactionsByType(type: String): Flow<List<RawTransaction>>
}
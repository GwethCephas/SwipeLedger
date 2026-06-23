package com.cephcoding.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val amount: Double,
    val party: String,
    val type: String,
    val category: String,
    val rawBody: String,
    val timestamp: Long = System.currentTimeMillis()
)
package com.cephcoding.core.data.database

import androidx.room.ColumnInfo
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
    // defaultValue must match Migrations.MIGRATION_1_2's `ALTER TABLE ... DEFAULT 'UNCATEGORIZED'`
    // exactly -- Room validates column defaultValue against the live schema at open time, and a
    // mismatch here crashes every upgraded install even though a fresh v2 CREATE TABLE would be fine.
    @ColumnInfo(defaultValue = "UNCATEGORIZED")
    val subcategory: String,
    val rawBody: String,
    val timestamp: Long = System.currentTimeMillis()
)
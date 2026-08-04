package com.cephcoding.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 2,
    exportSchema = true
)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
}
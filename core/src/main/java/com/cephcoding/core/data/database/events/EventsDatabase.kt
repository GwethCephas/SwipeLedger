package com.cephcoding.core.data.database.events

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LedgerEventEntity::class],
    version = 1,
    exportSchema = true
)
abstract class EventsDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}

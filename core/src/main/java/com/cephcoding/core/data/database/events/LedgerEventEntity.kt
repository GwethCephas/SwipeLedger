package com.cephcoding.core.data.database.events

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class LedgerEventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val currency: String,
    val nextDueDateTimestamp: Long,
    val recurrenceInterval: String,
    val isAutoAdded: Boolean,
    val notificationTiming: String
)

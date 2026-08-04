package com.cephcoding.core.data.database.events

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: LedgerEventEntity)

    // Template rows (isAutoAdded = false) are the live recurring definitions.
    @Query("SELECT * FROM events WHERE isAutoAdded = 0 ORDER BY nextDueDateTimestamp ASC")
    fun getUpcomingEvents(): Flow<List<LedgerEventEntity>>

    // Occurrence rows (isAutoAdded = true) are fired/materialized history entries.
    @Query("SELECT * FROM events WHERE isAutoAdded = 1 ORDER BY nextDueDateTimestamp DESC LIMIT 50")
    fun getPastEvents(): Flow<List<LedgerEventEntity>>

    @Query("SELECT * FROM events WHERE isAutoAdded = 0")
    suspend fun getAllUpcomingEventsOnce(): List<LedgerEventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): LedgerEventEntity?

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEvent(id: String)

    @Query("UPDATE events SET nextDueDateTimestamp = :nextDueDateTimestamp WHERE id = :id")
    suspend fun advanceTemplate(id: String, nextDueDateTimestamp: Long)

    @Query("UPDATE events SET isAutoAdded = 1, nextDueDateTimestamp = :firedAtTimestamp WHERE id = :id")
    suspend fun flipToOccurrence(id: String, firedAtTimestamp: Long)
}

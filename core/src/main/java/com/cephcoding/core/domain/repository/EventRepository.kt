package com.cephcoding.core.domain.repository

import com.cephcoding.core.domain.model.LedgerEvent
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun insertEvent(event: LedgerEvent)
    suspend fun deleteEvent(id: String)

    fun getUpcomingEvents(): Flow<List<LedgerEvent>>
    fun getPastEvents(): Flow<List<LedgerEvent>>

    suspend fun getUpcomingEventsSnapshot(): List<LedgerEvent>
    suspend fun getEventSnapshot(id: String): LedgerEvent?

    suspend fun handleEventFired(eventId: String): LedgerEvent?
}

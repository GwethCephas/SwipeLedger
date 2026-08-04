package com.cephcoding.core.data.repository

import androidx.room.withTransaction
import com.cephcoding.core.data.database.events.EventDao
import com.cephcoding.core.data.database.events.EventsDatabase
import com.cephcoding.core.data.mapper.toEntity
import com.cephcoding.core.data.mapper.toLedgerEvent
import com.cephcoding.core.domain.model.LedgerEvent
import com.cephcoding.core.domain.model.RecurrenceInterval
import com.cephcoding.core.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.UUID

class EventRepositoryImpl(
    private val eventDao: EventDao,
    private val eventsDatabase: EventsDatabase
) : EventRepository {

    override suspend fun insertEvent(event: LedgerEvent) {
        eventDao.insertEvent(event.toEntity())
    }

    override suspend fun deleteEvent(id: String) {
        eventDao.deleteEvent(id)
    }

    override fun getUpcomingEvents(): Flow<List<LedgerEvent>> {
        return eventDao.getUpcomingEvents().map { entities -> entities.map { it.toLedgerEvent() } }
    }

    override fun getPastEvents(): Flow<List<LedgerEvent>> {
        return eventDao.getPastEvents().map { entities -> entities.map { it.toLedgerEvent() } }
    }

    override suspend fun getUpcomingEventsSnapshot(): List<LedgerEvent> {
        return eventDao.getAllUpcomingEventsOnce().map { it.toLedgerEvent() }
    }

    override suspend fun getEventSnapshot(id: String): LedgerEvent? {
        return eventDao.getEventById(id)?.toLedgerEvent()
    }

    override suspend fun handleEventFired(eventId: String): LedgerEvent? {
        return eventsDatabase.withTransaction {
            val template = eventDao.getEventById(eventId) ?: return@withTransaction null
            val firedAt = template.nextDueDateTimestamp

            if (template.recurrenceInterval == RecurrenceInterval.NONE.name) {
                eventDao.flipToOccurrence(id = template.id, firedAtTimestamp = firedAt)
                return@withTransaction null
            }

            val occurrence = template.copy(
                id = UUID.randomUUID().toString(),
                isAutoAdded = true,
                nextDueDateTimestamp = firedAt
            )
            eventDao.insertEvent(occurrence)

            val interval = try {
                RecurrenceInterval.valueOf(template.recurrenceInterval)
            } catch (e: IllegalArgumentException) {
                RecurrenceInterval.NONE
            }

            val nextDueDate = Calendar.getInstance().apply {
                timeInMillis = firedAt
                advanceBy(interval)
            }.timeInMillis

            eventDao.advanceTemplate(id = template.id, nextDueDateTimestamp = nextDueDate)
            template.copy(nextDueDateTimestamp = nextDueDate).toLedgerEvent()
        }
    }

    private fun Calendar.advanceBy(interval: RecurrenceInterval) {
        when (interval) {
            RecurrenceInterval.WEEKLY -> add(Calendar.WEEK_OF_YEAR, 1)
            RecurrenceInterval.MONTHLY -> add(Calendar.MONTH, 1)
            RecurrenceInterval.YEARLY -> add(Calendar.YEAR, 1)
            RecurrenceInterval.NONE -> Unit
        }
    }
}

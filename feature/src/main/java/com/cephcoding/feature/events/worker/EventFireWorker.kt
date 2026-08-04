package com.cephcoding.feature.events.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cephcoding.core.domain.repository.EventRepository
import com.cephcoding.feature.events.alarm.AlarmScheduler
import com.cephcoding.feature.events.notification.EventNotificationHelper

class EventFireWorker(
    context: Context,
    params: WorkerParameters,
    private val eventRepository: EventRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationHelper: EventNotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getString(KEY_EVENT_ID) ?: return Result.failure()

        return try {
            val dueEvent = eventRepository.getEventSnapshot(eventId) ?: return Result.failure()
            val nextTemplate = eventRepository.handleEventFired(eventId)
            if (nextTemplate != null) {
                alarmScheduler.schedule(nextTemplate)
            }
            notificationHelper.showEventDueNotification(dueEvent)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_EVENT_ID = "event_id"
    }
}

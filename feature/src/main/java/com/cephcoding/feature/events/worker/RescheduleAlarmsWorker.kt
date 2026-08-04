package com.cephcoding.feature.events.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cephcoding.core.domain.repository.EventRepository
import com.cephcoding.feature.events.alarm.AlarmScheduler

/** AlarmManager alarms are cleared on reboot -- re-arms every still-upcoming event's alarm. */
class RescheduleAlarmsWorker(
    context: Context,
    params: WorkerParameters,
    private val eventRepository: EventRepository,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            eventRepository.getUpcomingEventsSnapshot().forEach { event ->
                alarmScheduler.schedule(event)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

package com.cephcoding.feature.events.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.cephcoding.core.domain.model.LedgerEvent

class AlarmScheduler(private val context: Context) {

    fun schedule(event: LedgerEvent) {
        val triggerAt = event.nextDueDateTimestamp + event.notificationTiming.offsetMillis
        if (triggerAt <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntentFor(event.id))
    }

    fun cancel(eventId: String) {
        val pendingIntent = pendingIntentFor(eventId)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun pendingIntentFor(eventId: String): PendingIntent {
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EventAlarmReceiver.EXTRA_EVENT_ID, eventId)
        }
        return PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

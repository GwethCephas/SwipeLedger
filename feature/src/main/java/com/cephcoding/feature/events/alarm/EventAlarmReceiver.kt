package com.cephcoding.feature.events.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.cephcoding.feature.events.worker.EventFireWorker

class EventAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: return

        val inputData = workDataOf(EventFireWorker.KEY_EVENT_ID to eventId)
        val workRequest = OneTimeWorkRequestBuilder<EventFireWorker>()
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
    }

    companion object {
        const val EXTRA_EVENT_ID = "event_id"
    }
}

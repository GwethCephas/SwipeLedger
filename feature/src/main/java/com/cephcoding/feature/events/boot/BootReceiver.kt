package com.cephcoding.feature.events.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.cephcoding.feature.events.worker.RescheduleAlarmsWorker

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>().build()
        WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
    }
}

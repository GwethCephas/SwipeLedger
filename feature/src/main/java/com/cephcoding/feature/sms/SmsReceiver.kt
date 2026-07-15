package com.cephcoding.feature.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.cephcoding.feature.sms.worker.SmsParseWorker
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SwipeLedgerSMS", "Broadcast received! Action: ${intent.action}")

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (sms in messages) {
                val sender = sms.originatingAddress ?: "Unknown"
                val body = sms.messageBody ?: ""

                val inputData = workDataOf(
                    SmsParseWorker.KEY_SENDER to sender,
                    SmsParseWorker.KEY_BODY to body
                )

                // Build an expedited background work request
                val workRequest = OneTimeWorkRequestBuilder<SmsParseWorker>()
                    .setInputData(inputData)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                    .build()

                WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
            }
        }
    }
}
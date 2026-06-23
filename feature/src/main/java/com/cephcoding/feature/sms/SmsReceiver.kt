package com.cephcoding.feature.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val transactionProcessor: TransactionProcessor by inject()
    private val scope = CoroutineScope(Dispatchers.Default)
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (sms in messages) {
                val sender = sms.displayOriginatingAddress ?: continue
                val messageBody = sms.displayMessageBody ?: continue

                scope.launch {
                    transactionProcessor.processIncomingSms(sender = sender, body = messageBody)
                }
            }

        }
    }
}
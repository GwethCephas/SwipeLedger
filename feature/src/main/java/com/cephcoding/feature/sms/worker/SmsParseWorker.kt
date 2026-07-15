package com.cephcoding.feature.sms.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.cephcoding.feature.sms.TransactionProcessor

class SmsParseWorker(
    context: Context,
    params: WorkerParameters,
    private val transactionProcessor: TransactionProcessor
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val sender = inputData.getString(KEY_SENDER) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()

        return try {
            Log.d("SwipeLedgerWorker", "Executing expedited work for: $sender")
            transactionProcessor.processIncomingSms(sender, body)
            Result.success()
        } catch (e: Exception) {
            Log.e("SwipeLedgerWorker", "Failed parsing transaction in background", e)
            Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

    companion object {
        const val KEY_SENDER = "sender"
        const val KEY_BODY = "body"
    }
}
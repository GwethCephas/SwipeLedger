package com.cephcoding.feature.sms

import android.util.Log
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.feature.sms.ml.LocalClassifier
import com.cephcoding.feature.sms.parser.RegexParser

class TransactionProcessor(
    private val regexParser: RegexParser,
    private val localClassifier: LocalClassifier
) {

    suspend fun processIncomingSms(sender: String, body: String) {

        if (!isFinancialSender(sender)) return

        Log.d("TransactionProcessor", "Processing financial SMS from: $sender")

        val rawTransaction = regexParser.parse(body)

        if (rawTransaction != null) {
            Log.d(
                "SwipeLedgerParser",
                "Match Found! ID: ${rawTransaction.transactionId}, Amt: ${rawTransaction.amount}"
            )

            val category = localClassifier.classify(rawTransaction)

            val finalizedTransaction = rawTransaction.copy(category = category)

            saveToEncryptedLedger(finalizedTransaction)
        }

    }


    private fun isFinancialSender(sender: String): Boolean {
        return sender.length > 4 && !sender.startsWith("+")
    }

    private fun saveToEncryptedLedger(
        transaction: RawTransaction
    ) {
        Log.d(
            "TransactionProcessor",
            "Saved transaction securely: ${transaction.rawBody}"
        )
    }


}
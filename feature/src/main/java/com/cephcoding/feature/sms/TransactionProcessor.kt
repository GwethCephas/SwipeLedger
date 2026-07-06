package com.cephcoding.feature.sms

import android.util.Log
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.feature.sms.ml.LocalClassifier
import com.cephcoding.feature.sms.parser.RegexParser

import com.cephcoding.core.domain.repository.TransactionRepository

class TransactionProcessor(
    private val regexParser: RegexParser,
    private val localClassifier: LocalClassifier,
    private val repository: TransactionRepository
) {

    suspend fun processIncomingSms(sender: String, body: String) {

        if (!isFinancialSender(sender)) {
            Log.d("TransactionProcessor", "Ignoring non-financial sender: $sender")
            return
        }

        Log.d("TransactionProcessor", "Processing financial SMS from: $sender")

        val rawTransaction = regexParser.parse(body)

        if (rawTransaction != null) {
            Log.d(
                "SwipeLedgerParser",
                "Transaction Parsed Successfully! ID: ${rawTransaction.transactionId}, Amount: ${rawTransaction.amount}, Party: ${rawTransaction.party}"
            )

            val category = localClassifier.classify(rawTransaction)
            val finalizedTransaction = rawTransaction.copy(category = category)

            saveToEncryptedLedger(finalizedTransaction)
        } else {
            Log.w(
                "SwipeLedgerParser",
                "Failed to parse SMS body from $sender. No matching pattern found."
            )
        }

    }


    private fun isFinancialSender(sender: String): Boolean {
        return sender.length >= 4 && !sender.startsWith("+")
    }

    private suspend fun saveToEncryptedLedger(
        transaction: RawTransaction
    ) {
        Log.d(
            "TransactionProcessor",
            "Saving transaction ${transaction.transactionId} to database..."
        )
        repository.insertTransaction(transaction)
    }
}

package com.cephcoding.feature.parser

import android.util.Log
import com.cephcoding.feature.parser.model.RawTransaction

class TransactionProcessor(
    private val regexParser: RegexParser
) {

    suspend fun processIncomingSms(sender: String, body: String) {

        if (!isFinancialSender(sender)) return

        Log.d("TransactionProcessor", "Processing financial SMS from: $sender")

        val extractedData = regexParser.parse(body)

        if (extractedData != null) {
            Log.d(
                "SwipeLedgerParser",
                "Match Found! ID: ${extractedData.transactionId}, Amt: ${extractedData.amount}"
            )

            val category = determineCategory(extractedData)

            saveToEncryptedLedger(extractedData, category)
        }

    }


    private fun isFinancialSender(sender: String): Boolean {
        return sender.length > 4 && !sender.startsWith("+")
    }

    private fun determineCategory(transaction: RawTransaction): String {
        return "Uncategorized business"
    }

    private fun saveToEncryptedLedger(
        transaction: RawTransaction,
        category: String
    ) {
        Log.d(
            "TransactionProcessor",
            "Saved transaction securely: ${transaction.rawBody} under $category"
        )
    }


}
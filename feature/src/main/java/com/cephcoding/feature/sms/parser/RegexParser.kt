package com.cephcoding.feature.sms.parser

import android.util.Log
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionType

class RegexParser {
    companion object {
        private val SENT_PATTERN = Regex(
            """^([A-Z0-9]+)\s+Confirmed\.\s+Ksh\s?([\d,]+\.\d{2})\s+(?:sent to|paid to)\s+(.+?)(?:\s+on|\s+at|\s+for|\.|\s|$)""",
            RegexOption.IGNORE_CASE
        )

        private val RECEIVED_PATTERN = Regex(
            """^([A-Z0-9]+)\s+Confirmed\.\s*You\s+have\s+received\s+Ksh\s*([\d,]+\.\d{2})\s+from\s+([^.\n]+?)(?=\s+on)""",
            RegexOption.IGNORE_CASE
        )
    }

    fun parse(smsBody: String): RawTransaction? {
        val cleanBody = smsBody.trim()

        Log.d("SwipeLedgerParser", "Parsing SMS body: $cleanBody")

        SENT_PATTERN.find(cleanBody)?.let { match ->
            val id = match.groupValues[1]
            val amountStr = match.groupValues[2].replace(",", "")
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val party = match.groupValues[3].trim()

            Log.d(
                "SwipeLedgerParser",
                "Match Found (SENT): ID=$id, Amount=$amount, Party=$party"
            )

            return RawTransaction(id, amount, party, TransactionType.EXPENSE, cleanBody)
        }

        RECEIVED_PATTERN.find(cleanBody)?.let { match ->
            val id = match.groupValues[1]
            val amountStr = match.groupValues[2].replace(",", "")
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val party = match.groupValues[3].trim()

            Log.d(
                "SwipeLedgerParser",
                "Match Found (RECEIVED): ID=$id, Amount=$amount, Party=$party"
            )
            return RawTransaction(id, amount, party, TransactionType.INCOME, cleanBody)
        }

        Log.w("SwipeLedgerParser", "No regex match found for SMS body.")

        return null
    }
}
package com.cephcoding.feature.sms.parser

import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionType

class RegexParser {
    companion object {
        private val SENT_PATTERN = Regex(
            """^([A-Z0-9]+)\s+Confirmed\.\s+Ksh([\d,]+\.\d{2})\s+(?:sent to|paid to)\s+([^.]+?)(?:\s+for|\s+on|\.)""",
            RegexOption.IGNORE_CASE
        )

        private val RECEIVED_PATTERN = Regex(
            """^([A-Z0-9]+)\s+Confirmed\.\s+You\s+have\s+received\s+Ksh([\d,]+\.\d{2})\s+from\s+([^.]+?)\.""",
            RegexOption.IGNORE_CASE
        )
    }

    fun parse(smsBody: String): RawTransaction? {
        val cleanBody = smsBody.trim()

        SENT_PATTERN.find(cleanBody)?.let { match ->
            val id = match.groupValues[1]
            val amount = match.groupValues[2].replace(",", "").toDoubleOrNull() ?: 0.0
            val party = match.groupValues[3].trim()

            return RawTransaction(id, amount, party, TransactionType.EXPENSE, cleanBody)
        }

        RECEIVED_PATTERN.find(cleanBody)?.let { match ->
            val id = match.groupValues[1]
            val amount = match.groupValues[2].replace(",", "").toDoubleOrNull() ?: 0.0
            val party = match.groupValues[3].trim()

            return RawTransaction(id, amount, party, TransactionType.INCOME, cleanBody)
        }

        return null
    }
}
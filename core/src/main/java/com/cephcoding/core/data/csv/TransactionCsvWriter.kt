package com.cephcoding.core.data.csv

import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.category
import com.cephcoding.core.domain.model.displayName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val csvDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

fun buildTransactionsCsv(transactions: List<RawTransaction>): String {
    val builder = StringBuilder()
    builder.appendLine("ID,Date,Title,Category,Subcategory,Amount,Type")
    transactions.forEach { txn ->
        val subcategory = txn.subcategory?.displayName ?: "Uncategorized"
        val row = listOf(
            txn.transactionId,
            csvDateFormat.format(Date(txn.timestamp)),
            txn.party,
            txn.category.displayName,
            subcategory,
            "%.2f".format(txn.amount),
            txn.type.name
        ).joinToString(",") { it.toCsvField() }
        builder.appendLine(row)
    }
    return builder.toString()
}

private fun String.toCsvField(): String =
    if (any { it == ',' || it == '"' || it == '\n' || it == '\r' })
        "\"${replace("\"", "\"\"")}\""
    else this

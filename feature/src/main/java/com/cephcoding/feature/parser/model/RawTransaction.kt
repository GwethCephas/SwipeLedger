package com.cephcoding.feature.parser.model

data class RawTransaction(
    val transactionId: String,
    val amount: Double,
    val party: String,
    val type: TransactionType,
    val rawBody: String,
    val category: ExpenseCategory? = null
)

enum class TransactionType {
    INCOME, EXPENSE, UNKNOWN
}
enum class ExpenseCategory {
    INVENTORY,
    TRANSPORT,
    UTILITIES,
    MARKETING,
    SOFTWARE_SAAS,
    UNCATEGORIZED
}
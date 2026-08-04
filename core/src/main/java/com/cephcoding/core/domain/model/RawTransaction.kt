package com.cephcoding.core.domain.model

data class RawTransaction(
    val transactionId: String,
    val amount: Double,
    val party: String,
    val type: TransactionType,
    val rawBody: String,
    val timestamp: Long = System.currentTimeMillis(),
    val subcategory: TransactionSubcategory? = null
)

enum class TransactionType {
    INCOME, EXPENSE, UNKNOWN
}

/** Derived, never independently stored. Defaults to UNCATEGORIZED_EXPENSE pre-classification. */
val RawTransaction.category: TransactionCategory
    get() = subcategory?.parent ?: TransactionCategory.UNCATEGORIZED_EXPENSE

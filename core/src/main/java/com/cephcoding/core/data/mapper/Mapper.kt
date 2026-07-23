package com.cephcoding.core.data.mapper

import com.cephcoding.core.data.database.TransactionEntity
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionType

fun RawTransaction.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        transactionId = this.transactionId,
        amount = this.amount,
        party = this.party,
        type = this.type.name,
        category = this.category?.name ?: ExpenseCategory.UNCATEGORIZED.name,
        rawBody = this.rawBody,
        timestamp = this.timestamp
    )
}

fun TransactionEntity.toRawTransaction(): RawTransaction {
    return RawTransaction(
        transactionId = this.transactionId,
        amount = this.amount,
        party = this.party,
        type = try {
            TransactionType.valueOf(this.type)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            TransactionType.UNKNOWN
        },
        category = try {
            ExpenseCategory.valueOf(this.category)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            ExpenseCategory.UNCATEGORIZED
        },
        rawBody = this.rawBody,
        timestamp = this.timestamp
    )
}
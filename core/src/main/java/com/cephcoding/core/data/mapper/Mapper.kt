package com.cephcoding.core.data.mapper

import com.cephcoding.core.data.database.TransactionEntity
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionCategory
import com.cephcoding.core.domain.model.TransactionSubcategory
import com.cephcoding.core.domain.model.TransactionType

fun RawTransaction.toTransactionEntity(): TransactionEntity {
    val resolvedSubcategory = this.subcategory ?: TransactionSubcategory.UNCATEGORIZED
    return TransactionEntity(
        transactionId = this.transactionId,
        amount = this.amount,
        party = this.party,
        type = this.type.name,
        category = resolvedSubcategory.parent.name,
        subcategory = resolvedSubcategory.name,
        rawBody = this.rawBody,
        timestamp = this.timestamp
    )
}

fun TransactionEntity.toRawTransaction(): RawTransaction {
    val parsedSubcategory = try {
        TransactionSubcategory.valueOf(this.subcategory)
    } catch (e: IllegalArgumentException) {
        null
    }

    // subcategory is the source of truth whenever it parses at all, even if it
    // disagrees with the denormalized `category` column. `category` is only
    // consulted as a fallback hint when `subcategory` itself is corrupt/unknown,
    // to pick a better-than-global-uncategorized representative for that category.
    val finalSubcategory = parsedSubcategory ?: run {
        val fallbackCategory = try {
            TransactionCategory.valueOf(this.category)
        } catch (e: IllegalArgumentException) {
            TransactionCategory.UNCATEGORIZED_EXPENSE
        }
        TransactionSubcategory.entries.first { it.parent == fallbackCategory }
    }

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
        subcategory = finalSubcategory,
        rawBody = this.rawBody,
        timestamp = this.timestamp
    )
}

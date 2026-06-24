package com.cephcoding.core.data.mapper

import com.cephcoding.core.data.database.TransactionEntity
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionType
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TransactionMapperTest {
    @Test
    fun `toTransactionEntity maps domain model to database entity accurately`() {
        val domainModel = RawTransaction(
            transactionId = "TXN123",
            amount = 1500.50,
            party = "Wholesale Supplier",
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.INVENTORY,
            rawBody = "Paid Ksh 1500.50 to Wholesale Supplier"
        )

        val entity = domainModel.toTransactionEntity()

        assertEquals("TXN123", entity.transactionId)
        assertEquals(1500.50, entity.amount, 0.0)
        assertEquals("INVENTORY", entity.category)
        assertEquals("EXPENSE", entity.type)
    }

    @Test
    fun `toRawTransaction maps unknown strings gracefully to fallback enums`() {
        val brokenEntity = TransactionEntity(
            transactionId = "TXN999",
            amount = 50.0,
            party = "Unknown Biz",
            type = "INVALID_TYPE_STRING",
            category = "NEW_UNSUPPORTED_CATEGORY",
            rawBody = "Some text"
        )

        val domainModel = brokenEntity.toRawTransaction()

        assertEquals(TransactionType.UNKNOWN, domainModel.type)
        assertEquals(ExpenseCategory.UNCATEGORIZED, domainModel.category)
    }
}
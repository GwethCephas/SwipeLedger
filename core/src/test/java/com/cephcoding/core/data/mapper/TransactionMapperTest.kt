package com.cephcoding.core.data.mapper

import com.cephcoding.core.data.database.TransactionEntity
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionCategory
import com.cephcoding.core.domain.model.TransactionSubcategory
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.domain.model.category
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
            subcategory = TransactionSubcategory.SHOPPING_AND_ELECTRONICS,
            rawBody = "Paid Ksh 1500.50 to Wholesale Supplier"
        )

        val entity = domainModel.toTransactionEntity()

        assertEquals("TXN123", entity.transactionId)
        assertEquals(1500.50, entity.amount, 0.0)
        assertEquals("PERSONAL_CARE_AND_SHOPPING", entity.category)
        assertEquals("SHOPPING_AND_ELECTRONICS", entity.subcategory)
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
            subcategory = "NEW_UNSUPPORTED_CATEGORY",
            rawBody = "Some text"
        )

        val domainModel = brokenEntity.toRawTransaction()

        assertEquals(TransactionType.UNKNOWN, domainModel.type)
        assertEquals(TransactionSubcategory.UNCATEGORIZED, domainModel.subcategory)
        assertEquals(TransactionCategory.UNCATEGORIZED_EXPENSE, domainModel.category)
    }

    @Test
    fun `toRawTransaction falls back to category's representative subcategory when subcategory is corrupt`() {
        val entity = TransactionEntity(
            transactionId = "TXN1",
            amount = 100.0,
            party = "Some Party",
            type = "EXPENSE",
            category = "TRANSPORTATION",
            subcategory = "BOGUS",
            rawBody = "Some text"
        )

        val domainModel = entity.toRawTransaction()

        assertEquals(TransactionSubcategory.FUEL_AND_GAS_STATIONS, domainModel.subcategory)
        assertEquals(TransactionCategory.TRANSPORTATION, domainModel.category)
    }

    @Test
    fun `toRawTransaction trusts subcategory over a disagreeing category column`() {
        val entity = TransactionEntity(
            transactionId = "TXN2",
            amount = 100.0,
            party = "Some Party",
            type = "EXPENSE",
            category = "INCOME",
            subcategory = "FUEL_AND_GAS_STATIONS",
            rawBody = "Some text"
        )

        val domainModel = entity.toRawTransaction()

        assertEquals(TransactionSubcategory.FUEL_AND_GAS_STATIONS, domainModel.subcategory)
        assertEquals(TransactionCategory.TRANSPORTATION, domainModel.category)
    }
}

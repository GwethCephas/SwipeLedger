package com.cephcoding.core.data.csv

import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionSubcategory
import com.cephcoding.core.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionCsvWriterTest {

    @Test
    fun `empty list produces only the header row`() {
        val csv = buildTransactionsCsv(emptyList())
        assertEquals("ID,Date,Title,Category,Subcategory,Amount,Type\n", csv)
    }

    @Test
    fun `header row matches the required column order`() {
        val csv = buildTransactionsCsv(emptyList())
        val header = csv.lineSequence().first()
        assertEquals("ID,Date,Title,Category,Subcategory,Amount,Type", header)
    }

    @Test
    fun `a row is emitted per transaction with expected field mapping`() {
        val txn = RawTransaction(
            transactionId = "TX123",
            amount = 1500.5,
            party = "Shell Station",
            type = TransactionType.EXPENSE,
            subcategory = TransactionSubcategory.FUEL_AND_GAS_STATIONS,
            rawBody = "raw sms body",
            timestamp = 0L
        )

        val csv = buildTransactionsCsv(listOf(txn))
        val row = csv.lineSequence().drop(1).first()
        val fields = row.split(",")

        assertEquals("TX123", fields[0])
        assertEquals("Shell Station", fields[2])
        assertEquals("Transportation", fields[3])
        assertEquals("Fuel & Gas Stations", fields[4])
        assertEquals("1500.50", fields[5])
        assertEquals("EXPENSE", fields[6])
    }

    @Test
    fun `null subcategory falls back to Uncategorized`() {
        val txn = RawTransaction(
            transactionId = "TX1",
            amount = 100.0,
            party = "Unknown",
            type = TransactionType.EXPENSE,
            subcategory = null,
            rawBody = "raw"
        )

        val csv = buildTransactionsCsv(listOf(txn))
        val row = csv.lineSequence().drop(1).first()

        assertTrue(row.contains(",Uncategorized,"))
    }

    @Test
    fun `party names containing commas or quotes are quoted and escaped`() {
        val txn = RawTransaction(
            transactionId = "TX2",
            amount = 100.0,
            party = "Joe's \"Diner\", Ltd",
            type = TransactionType.EXPENSE,
            subcategory = null,
            rawBody = "raw"
        )

        val csv = buildTransactionsCsv(listOf(txn))
        val row = csv.lineSequence().drop(1).first()

        assertTrue(row.contains("\"Joe's \"\"Diner\"\", Ltd\""))
    }
}

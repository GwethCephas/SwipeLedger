package com.cephcoding.feature.parser

import com.cephcoding.feature.parser.model.TransactionType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test


class RegexParserTest {
    private val regexParser = RegexParser()

    @Test
    fun `parse validExpensePayment returnsCorrectMetadata`() {
        val sms = "XYZ1234567 Confirmed. Ksh1,500.00 paid to Jumia Technologies."

        val result = regexParser.parse(sms)

        assertNotNull(result)

        result?.run {
            assertEquals("XYZ1234567", transactionId)
            assertEquals(1500.00, amount, 0.0)
            assertEquals("Jumia Technologies", party)
            assertEquals(TransactionType.EXPENSE, type)
        }
    }

    @Test
    fun `parse validIncomingPayment returnsCorrectMetadata`() {
        val sms = "DEF4567890 Confirmed. You have received Ksh2,300.50 from JANE SMITH."

        val result = regexParser.parse(sms)

        assertNotNull(result)

        result?.run {
            assertEquals("DEF4567890", transactionId)
            assertEquals(2300.50, amount, 0.0)
            assertEquals("JANE SMITH", party)
            assertEquals(TransactionType.INCOME, type)
        }
    }

    @Test
    fun `non financial sms returns null`() {
        val sms = "Hey, don't forget to buy milk on your way home from the office."
        val result = regexParser.parse(sms)

        assertNull(result)
    }

    @Test
    fun `parse malformedAmount returns null or graceful fallback`() {
        val sms = "XYZ1234567 Confirmed. KshNotANumber paid to Jumia Technologies."

        val result = regexParser.parse(sms)
        assertNull(result)
    }
}
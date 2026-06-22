package com.cephcoding.feature.parser

import android.util.Log
import com.cephcoding.feature.parser.model.RawTransaction
import com.cephcoding.feature.parser.model.TransactionType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class TransactionProcessorTest {

    private val regexParser: RegexParser = mockk()
    private val transactionProcessor = TransactionProcessor(regexParser)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }
    @Test
    fun `processIncomingText withPersonalNumberSender ignoresProcessing`() = runTest {
        val sender = "+254712345678"
        val body = "XYZ1234567 Confirmed. Ksh150.00 paid to Supplier."

        transactionProcessor.processIncomingSms(sender = sender, body = body)

        verify(exactly = 0) { regexParser.parse(any()) }
    }

    @Test
    fun `processIncomingText withValidBusinessSender invokesParser`() = runTest {

        val sender = "MPESA"
        val body = "XYZ1234567 Confirmed. Ksh150.00 paid to Supplier."

        val expectedTransaction = RawTransaction(
            transactionId = "XYZ1234567",
            amount = 150.0,
            party = "Supplier",
            type = TransactionType.EXPENSE,
            rawBody = body
        )

        every { regexParser.parse(body) } returns expectedTransaction

        transactionProcessor.processIncomingSms(sender = sender, body = body)

        verify(exactly = 1) { regexParser.parse(body) }
    }
}
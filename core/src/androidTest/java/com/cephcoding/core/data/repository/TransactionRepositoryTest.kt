package com.cephcoding.core.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cephcoding.core.data.database.TransactionDatabase
import com.cephcoding.core.domain.model.ExpenseCategory
import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionType
import com.cephcoding.core.domain.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class TransactionRepositoryTest {

    private lateinit var database: TransactionDatabase
    private lateinit var repository: TransactionRepository
    private val testPassphrase = "TestSecurePassword123".toByteArray()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        System.loadLibrary("sqlcipher")

        database = Room.inMemoryDatabaseBuilder(
            context,
            TransactionDatabase::class.java
        ).openHelperFactory(SupportOpenHelperFactory(testPassphrase))
            .allowMainThreadQueries()
            .build()

        repository = TransactionRepositoryImpl(database.transactionsDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadTransactionFlow() = runTest {
        val sampleTransaction = RawTransaction(
            transactionId = "MPESA_XYZ_789",
            amount = 4500.0,
            party = "KPLC Tokens",
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.UTILITIES,
            rawBody = "Ksh4500 paid to KPLC."
        )

        repository.insertTransaction(sampleTransaction)

        val allTransactions = repository.getAllTransactions().first()

        assertEquals(1, allTransactions.size)
        val extractedTransaction = allTransactions[0]
        assertEquals("MPESA_XYZ_789", extractedTransaction.transactionId)
        assertEquals(ExpenseCategory.UTILITIES, extractedTransaction.category)
        assertEquals(4500.0, extractedTransaction.amount, 0.0)
    }
}
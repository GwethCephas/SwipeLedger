package com.cephcoding.feature.sms.ml

import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionSubcategory
import com.cephcoding.core.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalClassifierTest {

    private val classifier = LocalClassifier()

    private fun transaction(
        party: String = "Some Party",
        rawBody: String = "",
        type: TransactionType = TransactionType.EXPENSE
    ) = RawTransaction(
        transactionId = "TXN1",
        amount = 100.0,
        party = party,
        type = type,
        rawBody = rawBody
    )

    @Test
    fun `income always classifies as GENERAL_INCOME`() {
        val txn = transaction(type = TransactionType.INCOME, party = "Shell Station", rawBody = "received Ksh100")
        assertEquals(TransactionSubcategory.GENERAL_INCOME, classifier.classify(txn))
    }

    @Test
    fun `income short-circuits before keyword matching even when body contains an expense keyword`() {
        val txn = transaction(type = TransactionType.INCOME, rawBody = "You have received Ksh500 from Shell Station")
        assertEquals(TransactionSubcategory.GENERAL_INCOME, classifier.classify(txn))
    }

    @Test
    fun `kplc keyword classifies as POWER_AND_WATER`() {
        val txn = transaction(party = "KPLC PREPAID")
        assertEquals(TransactionSubcategory.POWER_AND_WATER, classifier.classify(txn))
    }

    @Test
    fun `shell keyword classifies as FUEL_AND_GAS_STATIONS`() {
        val txn = transaction(party = "Shell Station")
        assertEquals(TransactionSubcategory.FUEL_AND_GAS_STATIONS, classifier.classify(txn))
    }

    @Test
    fun `netflix keyword classifies as DIGITAL_MEDIA_AND_ENTERTAINMENT`() {
        val txn = transaction(party = "Netflix")
        assertEquals(TransactionSubcategory.DIGITAL_MEDIA_AND_ENTERTAINMENT, classifier.classify(txn))
    }

    @Test
    fun `naivas keyword classifies as GROCERIES_AND_SUPERMARKET`() {
        val txn = transaction(party = "Naivas Supermarket")
        assertEquals(TransactionSubcategory.GROCERIES_AND_SUPERMARKET, classifier.classify(txn))
    }

    @Test
    fun `fuliza keyword classifies as LOANS_AND_DEBT_REPAYMENTS`() {
        val txn = transaction(rawBody = "Fuliza M-Pesa amount")
        assertEquals(TransactionSubcategory.LOANS_AND_DEBT_REPAYMENTS, classifier.classify(txn))
    }

    @Test
    fun `pharmacy keyword classifies as HEALTHCARE_AND_PHARMACY`() {
        val txn = transaction(party = "Goodlife Pharmacy")
        assertEquals(TransactionSubcategory.HEALTHCARE_AND_PHARMACY, classifier.classify(txn))
    }

    @Test
    fun `school fees keyword classifies as EDUCATION_AND_LEARNING`() {
        val txn = transaction(rawBody = "Paid for school fees")
        assertEquals(TransactionSubcategory.EDUCATION_AND_LEARNING, classifier.classify(txn))
    }

    @Test
    fun `unmatched expense text falls back to UNCATEGORIZED`() {
        val txn = transaction(party = "Some Random Person", rawBody = "sent to Some Random Person")
        assertEquals(TransactionSubcategory.UNCATEGORIZED, classifier.classify(txn))
    }

    @Test
    fun `money market fund does not collide with GROCERIES via bare 'market'`() {
        val txn = transaction(rawBody = "Deposit to money market fund")
        assertEquals(TransactionSubcategory.INVESTMENTS_AND_MMFS, classifier.classify(txn))
    }

    @Test
    fun `barber shop does not collide with ALCOHOL_AND_NIGHTLIFE via bare 'bar'`() {
        val txn = transaction(party = "City Barber Shop")
        assertEquals(TransactionSubcategory.BEAUTY_AND_GROOMING, classifier.classify(txn))
    }

    @Test
    fun `realistic P2P confirmation boilerplate does not fall into BANK_AND_WALLET_FEES`() {
        // Real M-Pesa SMS bodies always contain "Transaction cost, KshX.XX" regardless
        // of what the transaction actually was -- this must not become a catch-all.
        val txn = transaction(
            party = "JOHN DOE",
            rawBody = "ABC1234 Confirmed. Ksh500.00 sent to JOHN DOE 0722000000 on 4/8/26 at 10:00 AM. " +
                "New M-PESA balance is Ksh1,000.00. Transaction cost, Ksh7.00. " +
                "Amount you can transact within a day is 299,000.00."
        )
        assertEquals(TransactionSubcategory.UNCATEGORIZED, classifier.classify(txn))
    }

    @Test
    fun `'rent' does not match inside 'current' balance boilerplate`() {
        val txn = transaction(rawBody = "Your current outstanding Fuliza M-Pesa balance is Ksh500.00")
        assertEquals(TransactionSubcategory.LOANS_AND_DEBT_REPAYMENTS, classifier.classify(txn))
    }

    @Test
    fun `'sha' does not match inside personal names like 'Shah'`() {
        val txn = transaction(party = "Ahmed Shah", rawBody = "sent to Ahmed Shah")
        assertEquals(TransactionSubcategory.UNCATEGORIZED, classifier.classify(txn))
    }

    @Test
    fun `'pub' does not match inside 'Republic'`() {
        val txn = transaction(party = "Republic Bank", rawBody = "paid to Republic Bank")
        assertEquals(TransactionSubcategory.UNCATEGORIZED, classifier.classify(txn))
    }
}

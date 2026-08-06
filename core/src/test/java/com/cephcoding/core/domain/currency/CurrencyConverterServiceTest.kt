package com.cephcoding.core.domain.currency

import com.cephcoding.core.domain.model.Currency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyConverterServiceTest {

    @Test
    fun `KES passes through unchanged`() {
        assertEquals(1000.0, CurrencyConverterService.convert(1000.0, Currency.KES), 0.0001)
    }

    @Test
    fun `converting zero always yields zero`() {
        Currency.entries.forEach { currency ->
            assertEquals(0.0, CurrencyConverterService.convert(0.0, currency), 0.0001)
        }
    }

    @Test
    fun `every currency has a positive conversion rate`() {
        Currency.entries.forEach { currency ->
            assertTrue(
                "Expected a positive converted amount for $currency",
                CurrencyConverterService.convert(1000.0, currency) > 0.0
            )
        }
    }

    @Test
    fun `format embeds the target currency symbol`() {
        Currency.entries.forEach { currency ->
            val formatted = CurrencyConverterService.format(1000.0, currency)
            assertTrue(
                "Expected \"$formatted\" to start with ${currency.symbol}",
                formatted.startsWith(currency.symbol)
            )
        }
    }

    @Test
    fun `format renders thousands separators and two decimal places`() {
        val formatted = CurrencyConverterService.format(1000.0, Currency.KES)
        assertEquals("KSh1,000.00", formatted)
    }
}

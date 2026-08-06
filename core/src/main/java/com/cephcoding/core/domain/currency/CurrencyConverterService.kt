package com.cephcoding.core.domain.currency

import com.cephcoding.core.domain.model.Currency

/**
 * Static, hardcoded, KES-relative rates. SwipeLedger has no backend, so there is no
 * live rate source — every transaction amount is natively stored/entered in KES
 * (M-Pesa SMS confirmations are always KES-denominated), and these rates only drive
 * display-layer formatting. Illustrative fixed values; revisit periodically since
 * real-world rates drift.
 */
object CurrencyConverterService {

    private val ratesFromKes: Map<Currency, Double> = mapOf(
        Currency.KES to 1.0,
        Currency.USD to 0.0078,
        Currency.EUR to 0.0072,
        Currency.GBP to 0.0062,
        Currency.JPY to 1.16,
        Currency.CAD to 0.0106,
        Currency.AUD to 0.0120,
        Currency.INR to 0.66,
        Currency.ZAR to 0.14,
        Currency.AED to 0.0286
    )

    fun convert(amountInKes: Double, to: Currency): Double =
        amountInKes * (ratesFromKes[to] ?: 1.0)

    fun format(amountInKes: Double, currency: Currency): String =
        "${currency.symbol}%,.2f".format(convert(amountInKes, currency))
}

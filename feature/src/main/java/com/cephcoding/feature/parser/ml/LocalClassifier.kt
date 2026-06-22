package com.cephcoding.feature.parser.ml

import com.cephcoding.feature.parser.model.ExpenseCategory
import com.cephcoding.feature.parser.model.RawTransaction
import com.cephcoding.feature.parser.model.TransactionType
import java.util.Locale

class LocalClassifier {
    private val classificationMatrix = mapOf(
        ExpenseCategory.INVENTORY to listOf(
            "wholesale",
            "supply",
            "distributors",
            "stores",
            "stock",
            "kamukunji",
            "biashara"
        ),
        ExpenseCategory.TRANSPORT to listOf(
            "fuel",
            "shell",
            "totalenergy",
            "rubis",
            "ola",
            "courier",
            "speedaf",
            "dhl",
            "bolt",
            "uber",
            "matatu"
        ),
        ExpenseCategory.UTILITIES to listOf(
            "kplc",
            "tokens",
            "water",
            "sewerage",
            "safaricom home",
            "fiber",
            "zuku"
        ),
        ExpenseCategory.MARKETING to listOf(
            "facebook ads",
            "google ads",
            "printing",
            "branding",
            "graphics"
        ),
        ExpenseCategory.SOFTWARE_SAAS to listOf(
            "hosting",
            "cloud",
            "domain",
            "github",
            "aws",
            "shopify"
        )
    )

    fun classify(transaction: RawTransaction): ExpenseCategory {
        if (transaction.type == TransactionType.INCOME) {
            return ExpenseCategory.UNCATEGORIZED
        }

        val normalizedText =
            "${transaction.party.lowercase(Locale.ROOT)} ${transaction.rawBody.lowercase(Locale.ROOT)}"

        for ((category, keywords) in classificationMatrix) {
            if (keywords.any { keyword -> normalizedText.contains(keyword) }) {
                return category
            }
        }
        return ExpenseCategory.UNCATEGORIZED
    }
}
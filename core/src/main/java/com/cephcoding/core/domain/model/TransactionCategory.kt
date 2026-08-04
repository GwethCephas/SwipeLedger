package com.cephcoding.core.domain.model

enum class TransactionCategory {
    HOUSING_AND_UTILITIES,
    PHONE_AND_CONNECTIVITY,
    FOOD_AND_DINING,
    TRANSPORTATION,
    FINANCIALS_AND_FEES,
    PERSONAL_CARE_AND_SHOPPING,
    EDUCATION_AND_FAMILY,
    UNCATEGORIZED_EXPENSE,
    INCOME
}

val TransactionCategory.displayName: String
    get() = when (this) {
        TransactionCategory.HOUSING_AND_UTILITIES -> "Housing & Utilities"
        TransactionCategory.PHONE_AND_CONNECTIVITY -> "Phone & Connectivity"
        TransactionCategory.FOOD_AND_DINING -> "Food & Dining"
        TransactionCategory.TRANSPORTATION -> "Transportation"
        TransactionCategory.FINANCIALS_AND_FEES -> "Financials & Fees"
        TransactionCategory.PERSONAL_CARE_AND_SHOPPING -> "Personal Care & Shopping"
        TransactionCategory.EDUCATION_AND_FAMILY -> "Education & Family"
        TransactionCategory.UNCATEGORIZED_EXPENSE -> "Uncategorized"
        TransactionCategory.INCOME -> "Income"
    }

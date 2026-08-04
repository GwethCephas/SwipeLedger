package com.cephcoding.core.domain.model

/**
 * Order within each category group matters: the first-declared member of a group
 * is used by Mapper.kt as the fallback representative when a stored subcategory
 * string fails to parse but the stored category string still does.
 */
enum class TransactionSubcategory(val parent: TransactionCategory) {
    // HOUSING_AND_UTILITIES
    RENT_AND_HOUSING(TransactionCategory.HOUSING_AND_UTILITIES),
    POWER_AND_WATER(TransactionCategory.HOUSING_AND_UTILITIES),
    HOME_MAINTENANCE_AND_SECURITY(TransactionCategory.HOUSING_AND_UTILITIES),

    // PHONE_AND_CONNECTIVITY
    DATA_AND_AIRTIME(TransactionCategory.PHONE_AND_CONNECTIVITY),
    HOME_INTERNET_AND_TV(TransactionCategory.PHONE_AND_CONNECTIVITY),
    DIGITAL_MEDIA_AND_ENTERTAINMENT(TransactionCategory.PHONE_AND_CONNECTIVITY),
    SOFTWARE_AND_DIGITAL_TOOLS(TransactionCategory.PHONE_AND_CONNECTIVITY),

    // FOOD_AND_DINING
    GROCERIES_AND_SUPERMARKET(TransactionCategory.FOOD_AND_DINING),
    DINING_OUT_AND_CAFES(TransactionCategory.FOOD_AND_DINING),
    FOOD_DELIVERY(TransactionCategory.FOOD_AND_DINING),
    ALCOHOL_AND_NIGHTLIFE(TransactionCategory.FOOD_AND_DINING),

    // TRANSPORTATION
    FUEL_AND_GAS_STATIONS(TransactionCategory.TRANSPORTATION),
    RIDE_HAILING_AND_TAXIS(TransactionCategory.TRANSPORTATION),
    PUBLIC_TRANSIT_AND_TRAINS(TransactionCategory.TRANSPORTATION),
    VEHICLE_UPKEEP(TransactionCategory.TRANSPORTATION),

    // FINANCIALS_AND_FEES
    BANK_AND_WALLET_FEES(TransactionCategory.FINANCIALS_AND_FEES),
    LOANS_AND_DEBT_REPAYMENTS(TransactionCategory.FINANCIALS_AND_FEES),
    P2P_TRANSFERS(TransactionCategory.FINANCIALS_AND_FEES),
    INVESTMENTS_AND_MMFS(TransactionCategory.FINANCIALS_AND_FEES),

    // PERSONAL_CARE_AND_SHOPPING
    HEALTHCARE_AND_PHARMACY(TransactionCategory.PERSONAL_CARE_AND_SHOPPING),
    GYM_AND_WELLNESS(TransactionCategory.PERSONAL_CARE_AND_SHOPPING),
    SHOPPING_AND_ELECTRONICS(TransactionCategory.PERSONAL_CARE_AND_SHOPPING),
    BEAUTY_AND_GROOMING(TransactionCategory.PERSONAL_CARE_AND_SHOPPING),

    // EDUCATION_AND_FAMILY
    EDUCATION_AND_LEARNING(TransactionCategory.EDUCATION_AND_FAMILY),
    GIFTS_TITHES_AND_DONATIONS(TransactionCategory.EDUCATION_AND_FAMILY),

    // UNCATEGORIZED_EXPENSE
    UNCATEGORIZED(TransactionCategory.UNCATEGORIZED_EXPENSE),

    // INCOME (only GENERAL_INCOME is auto-assigned by the classifier today;
    // the rest exist for future manual reclassification)
    GENERAL_INCOME(TransactionCategory.INCOME),
    SALARY(TransactionCategory.INCOME),
    BUSINESS_SALES(TransactionCategory.INCOME),
    REFUNDS_CASHBACKS(TransactionCategory.INCOME)
}

val TransactionSubcategory.displayName: String
    get() = when (this) {
        TransactionSubcategory.RENT_AND_HOUSING -> "Rent & Housing"
        TransactionSubcategory.POWER_AND_WATER -> "Power & Water"
        TransactionSubcategory.HOME_MAINTENANCE_AND_SECURITY -> "Home Maintenance & Security"
        TransactionSubcategory.DATA_AND_AIRTIME -> "Data & Airtime"
        TransactionSubcategory.HOME_INTERNET_AND_TV -> "Home Internet & TV"
        TransactionSubcategory.DIGITAL_MEDIA_AND_ENTERTAINMENT -> "Digital Media & Entertainment"
        TransactionSubcategory.SOFTWARE_AND_DIGITAL_TOOLS -> "Software & Digital Tools"
        TransactionSubcategory.GROCERIES_AND_SUPERMARKET -> "Groceries & Supermarket"
        TransactionSubcategory.DINING_OUT_AND_CAFES -> "Dining Out & Cafes"
        TransactionSubcategory.FOOD_DELIVERY -> "Food Delivery"
        TransactionSubcategory.ALCOHOL_AND_NIGHTLIFE -> "Alcohol & Nightlife"
        TransactionSubcategory.FUEL_AND_GAS_STATIONS -> "Fuel & Gas Stations"
        TransactionSubcategory.RIDE_HAILING_AND_TAXIS -> "Ride-Hailing & Taxis"
        TransactionSubcategory.PUBLIC_TRANSIT_AND_TRAINS -> "Public Transit & Trains"
        TransactionSubcategory.VEHICLE_UPKEEP -> "Vehicle Upkeep"
        TransactionSubcategory.BANK_AND_WALLET_FEES -> "Bank & Wallet Transaction Fees"
        TransactionSubcategory.LOANS_AND_DEBT_REPAYMENTS -> "Loans & Debt Repayments"
        TransactionSubcategory.P2P_TRANSFERS -> "P2P Transfers"
        TransactionSubcategory.INVESTMENTS_AND_MMFS -> "Investments/MMFs"
        TransactionSubcategory.HEALTHCARE_AND_PHARMACY -> "Healthcare/Pharmacy"
        TransactionSubcategory.GYM_AND_WELLNESS -> "Gym & Wellness"
        TransactionSubcategory.SHOPPING_AND_ELECTRONICS -> "Shopping/Electronics"
        TransactionSubcategory.BEAUTY_AND_GROOMING -> "Beauty/Grooming"
        TransactionSubcategory.EDUCATION_AND_LEARNING -> "Education & Learning"
        TransactionSubcategory.GIFTS_TITHES_AND_DONATIONS -> "Gifts/Tithes/Donations"
        TransactionSubcategory.UNCATEGORIZED -> "Uncategorized"
        TransactionSubcategory.GENERAL_INCOME -> "General Income/Received"
        TransactionSubcategory.SALARY -> "Salary"
        TransactionSubcategory.BUSINESS_SALES -> "Business/Sales"
        TransactionSubcategory.REFUNDS_CASHBACKS -> "Refunds/Cashbacks"
    }

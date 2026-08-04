package com.cephcoding.feature.sms.ml

import com.cephcoding.core.domain.model.RawTransaction
import com.cephcoding.core.domain.model.TransactionSubcategory
import com.cephcoding.core.domain.model.TransactionType
import java.util.Locale

class LocalClassifier {

    // Map iteration order is the precedence order on keyword collision -- follows
    // the master taxonomy's category order, subcategory declaration order within
    // each category. Deliberately avoids collision-prone bare substrings (e.g.
    // "bar", "market", "bus", "data", "loan") that would false-positive against
    // unrelated categories or plain SMS prose -- fuller phrases/brand names used
    // instead.
    //
    // Known accepted gaps: P2P_TRANSFERS has no reliable keyword signal from an
    // SMS body alone (distinguishing a personal-name recipient from a merchant
    // needs parser-level signal, out of scope here) and ships with no keywords.
    // Courier/logistics terms (DHL, Speedaf) and generic "insurance" have no
    // clean single-subcategory fit in the master taxonomy and are dropped rather
    // than force-fit -- both fall through to UNCATEGORIZED.
    //
    // BANK_AND_WALLET_FEES also ships with no keywords: every M-Pesa confirmation
    // SMS includes "Transaction cost, KshX.XX" boilerplate regardless of what the
    // transaction actually was, so keying off that phrase would make this
    // subcategory a catch-all for nearly every expense instead of a real signal.
    private val classificationMatrix = mapOf(
        // HOUSING_AND_UTILITIES
        TransactionSubcategory.RENT_AND_HOUSING to listOf(
            "rent", "landlord", "housing", "apartment", "estate agent"
        ),
        TransactionSubcategory.POWER_AND_WATER to listOf(
            "kplc", "tokens", "prepaid token", "water", "sewerage", "nairobi water"
        ),
        TransactionSubcategory.HOME_MAINTENANCE_AND_SECURITY to listOf(
            "plumber", "electrician", "kk security", "wapi security", "cctv", "alarm monitoring"
        ),

        // PHONE_AND_CONNECTIVITY
        TransactionSubcategory.DATA_AND_AIRTIME to listOf(
            "airtime", "data bundle", "bundles", "okoa jahazi", "safaricom data"
        ),
        TransactionSubcategory.HOME_INTERNET_AND_TV to listOf(
            "safaricom home", "fiber", "zuku", "dstv", "gotv", "startimes", "wananchi"
        ),
        TransactionSubcategory.DIGITAL_MEDIA_AND_ENTERTAINMENT to listOf(
            "netflix", "spotify", "showmax", "youtube premium", "amazon prime"
        ),
        TransactionSubcategory.SOFTWARE_AND_DIGITAL_TOOLS to listOf(
            "hosting", "cloud", "domain", "github", "aws", "shopify",
            "google workspace", "microsoft 365", "canva"
        ),

        // FOOD_AND_DINING
        TransactionSubcategory.GROCERIES_AND_SUPERMARKET to listOf(
            "supermarket", "naivas", "carrefour", "quickmart", "chandarana", "greenspoon"
        ),
        TransactionSubcategory.DINING_OUT_AND_CAFES to listOf(
            "restaurant", "cafe", "java house", "artcaffe", "kfc", "hotel"
        ),
        TransactionSubcategory.FOOD_DELIVERY to listOf(
            "glovo", "uber eats", "jumia food", "bolt food"
        ),
        TransactionSubcategory.ALCOHOL_AND_NIGHTLIFE to listOf(
            "wines and spirits", "liquor store", "pub", "nightclub", "lounge"
        ),

        // TRANSPORTATION
        TransactionSubcategory.FUEL_AND_GAS_STATIONS to listOf(
            "fuel", "shell", "totalenergy", "total energies", "rubis", "ola energy", "petrol station"
        ),
        TransactionSubcategory.RIDE_HAILING_AND_TAXIS to listOf(
            "bolt", "uber", "little cab", "taxi"
        ),
        TransactionSubcategory.PUBLIC_TRANSIT_AND_TRAINS to listOf(
            "matatu", "sacco", "sgr", "madaraka express", "ntsa"
        ),
        TransactionSubcategory.VEHICLE_UPKEEP to listOf(
            "mechanic", "spare parts", "car wash", "garage"
        ),

        // FINANCIALS_AND_FEES
        TransactionSubcategory.BANK_AND_WALLET_FEES to emptyList(),
        TransactionSubcategory.LOANS_AND_DEBT_REPAYMENTS to listOf(
            "fuliza", "m-shwari", "kcb m-pesa", "tala", "branch international", "okash", "loan repayment"
        ),
        TransactionSubcategory.P2P_TRANSFERS to emptyList(),
        TransactionSubcategory.INVESTMENTS_AND_MMFS to listOf(
            "money market fund", "mmf", "cytonn", "sanlam unit trust", "nabo capital", "etica"
        ),

        // PERSONAL_CARE_AND_SHOPPING
        TransactionSubcategory.HEALTHCARE_AND_PHARMACY to listOf(
            "pharmacy", "chemist", "hospital", "clinic", "nhif", "sha", "goodlife"
        ),
        TransactionSubcategory.GYM_AND_WELLNESS to listOf(
            "gym", "fitness club", "spa", "yoga studio"
        ),
        TransactionSubcategory.SHOPPING_AND_ELECTRONICS to listOf(
            "wholesale", "supply", "distributors", "stores", "stock", "kamukunji",
            "biashara", "jumia", "electronics"
        ),
        TransactionSubcategory.BEAUTY_AND_GROOMING to listOf(
            "salon", "barber", "cosmetics"
        ),

        // EDUCATION_AND_FAMILY
        TransactionSubcategory.EDUCATION_AND_LEARNING to listOf(
            "school fees", "tuition", "college", "university", "udemy", "coursera"
        ),
        TransactionSubcategory.GIFTS_TITHES_AND_DONATIONS to listOf(
            "church", "tithe", "offering", "donation", "harambee"
        )
    )

    // Word-boundary matching, not raw substring: a bare keyword like "rent" must
    // not match inside "current", "sha" must not match inside "Shah"/"Shariff",
    // "pub" must not match inside "Republic". Substring matching against full,
    // unstructured SMS bodies is too permissive for short keywords.
    private val compiledMatrix = classificationMatrix.mapValues { (_, keywords) ->
        keywords.map { keyword -> Regex("\\b${Regex.escape(keyword)}\\b") }
    }

    fun classify(transaction: RawTransaction): TransactionSubcategory {
        if (transaction.type == TransactionType.INCOME) {
            return TransactionSubcategory.GENERAL_INCOME
        }

        val normalizedText =
            "${transaction.party.lowercase(Locale.ROOT)} ${transaction.rawBody.lowercase(Locale.ROOT)}"

        for ((subcategory, patterns) in compiledMatrix) {
            if (patterns.any { pattern -> pattern.containsMatchIn(normalizedText) }) {
                return subcategory
            }
        }
        return TransactionSubcategory.UNCATEGORIZED
    }
}

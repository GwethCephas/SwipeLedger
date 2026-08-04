package com.cephcoding.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Best-effort remapping of the old flat ExpenseCategory taxonomy (INVENTORY,
 * TRANSPORT, UTILITIES, MARKETING, SOFTWARE_SAAS, UNCATEGORIZED) into the new
 * hierarchical TransactionCategory/TransactionSubcategory taxonomy. This is
 * lossy for historical rows -- e.g. an old TRANSPORT row could originally have
 * been fuel, a ride, or a courier fee, and SQL has no access to the original
 * SMS body at migration time to disambiguate further -- but it is strictly
 * better than the pre-migration state, where every INCOME row was stored as
 * UNCATEGORIZED.
 */
object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE transactions ADD COLUMN subcategory TEXT NOT NULL DEFAULT 'UNCATEGORIZED'"
            )

            db.execSQL(
                """
                UPDATE transactions SET
                    category = CASE category
                        WHEN 'INVENTORY'      THEN 'PERSONAL_CARE_AND_SHOPPING'
                        WHEN 'TRANSPORT'      THEN 'TRANSPORTATION'
                        WHEN 'UTILITIES'      THEN 'HOUSING_AND_UTILITIES'
                        WHEN 'MARKETING'      THEN 'UNCATEGORIZED_EXPENSE'
                        WHEN 'SOFTWARE_SAAS'  THEN 'PHONE_AND_CONNECTIVITY'
                        WHEN 'UNCATEGORIZED'  THEN 'UNCATEGORIZED_EXPENSE'
                        ELSE 'UNCATEGORIZED_EXPENSE'
                    END,
                    subcategory = CASE category
                        WHEN 'INVENTORY'      THEN 'SHOPPING_AND_ELECTRONICS'
                        WHEN 'TRANSPORT'      THEN 'FUEL_AND_GAS_STATIONS'
                        WHEN 'UTILITIES'      THEN 'POWER_AND_WATER'
                        WHEN 'MARKETING'      THEN 'UNCATEGORIZED'
                        WHEN 'SOFTWARE_SAAS'  THEN 'SOFTWARE_AND_DIGITAL_TOOLS'
                        WHEN 'UNCATEGORIZED'  THEN 'UNCATEGORIZED'
                        ELSE 'UNCATEGORIZED'
                    END
                WHERE type != 'INCOME'
                """.trimIndent()
            )

            // Old code always stored UNCATEGORIZED for income; force every
            // INCOME row onto the new INCOME/GENERAL_INCOME pair regardless of
            // whatever it held before -- this retroactively fixes the
            // income-never-categorized bug for existing on-device data.
            db.execSQL(
                "UPDATE transactions SET category = 'INCOME', subcategory = 'GENERAL_INCOME' WHERE type = 'INCOME'"
            )
        }
    }
}

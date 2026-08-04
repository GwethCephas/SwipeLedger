package com.cephcoding.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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

            db.execSQL(
                "UPDATE transactions SET category = 'INCOME', subcategory = 'GENERAL_INCOME' WHERE type = 'INCOME'"
            )
        }
    }
}

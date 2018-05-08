package xyz.arnau.muvicat.cache.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

val Migration_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.run {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS `postal_codes` (
                `code` INTEGER NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                PRIMARY KEY(`code`)
                )"""
            )

            execSQL("CREATE  INDEX `postalCode` ON `postal_codes` (`code`)")
        }
    }
}
package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object DbMigration1to2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.run {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS `cinemas`(
                `id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `address` TEXT NOT NULL,
                `town` TEXT, `region` TEXT,
                `province` TEXT,
                `postalCode` INTEGER,
                PRIMARY KEY(`id`)
                )"""
            )

            execSQL("CREATE  INDEX `cinemaId` ON `cinemas` (`id`)")

            execSQL("DROP INDEX id")
            execSQL("CREATE INDEX `movieId` ON `movies` (`id`)")
        }
    }
}
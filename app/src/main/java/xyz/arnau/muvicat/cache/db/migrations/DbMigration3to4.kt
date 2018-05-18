package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object DbMigration3to4 : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.run {
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS `showings` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `movieId` INTEGER NOT NULL,
                `cinemaId` INTEGER NOT NULL,
                `date` INTEGER NOT NULL,
                `version` TEXT,
                `seasonId` INTEGER,
                FOREIGN KEY(`movieId`) REFERENCES `movies`(`id`) ON UPDATE CASCADE ON DELETE CASCADE ,
                FOREIGN KEY(`cinemaId`) REFERENCES `cinemas`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            )

            execSQL("CREATE  INDEX `showingMovieId` ON `showings` (`movieId`)")
            execSQL("CREATE  INDEX `showingCinemaId` ON `showings` (`cinemaId`)")
            execSQL("CREATE  INDEX `showingDateId` ON `showings` (`date`)")
        }
    }
}
package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object DbMigration4to5 : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.run {
            execSQL("ALTER TABLE movies ADD COLUMN `tmdbId` INTEGER DEFAULT NULL")
            execSQL("ALTER TABLE movies ADD COLUMN `runtime` INTEGER DEFAULT NULL")
            execSQL("ALTER TABLE movies ADD COLUMN `genres` TEXT DEFAULT NULL")
            execSQL("ALTER TABLE movies ADD COLUMN `backdropUrl` TEXT DEFAULT NULL")
            execSQL("ALTER TABLE movies ADD COLUMN `voteAverage` REAL DEFAULT NULL")
            execSQL("ALTER TABLE movies ADD COLUMN `voteCount` INTEGER DEFAULT NULL")

            execSQL("""
                CREATE TABLE IF NOT EXISTS `cast_members` (
                    `id` INTEGER NOT NULL,
                    `order` INTEGER,
                    `name` TEXT NOT NULL,
                    `character` TEXT,
                    `profile_path` TEXT,
                    PRIMARY KEY(`id`)
                )
            """)
            execSQL("CREATE  INDEX `castMemberId` ON `cast_members` (`id`)")

            execSQL("""
                CREATE TABLE IF NOT EXISTS `movie_cast_member_join` (
                    `movieId` INTEGER NOT NULL,
                    `castMemberId` INTEGER NOT NULL,
                    PRIMARY KEY(`movieId`, `castMemberId`),
                    FOREIGN KEY(`movieId`) REFERENCES `movies`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE ,
                    FOREIGN KEY(`castMemberId`) REFERENCES `cast_members`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
                )
                """)
        }
    }
}

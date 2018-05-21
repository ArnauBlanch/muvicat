package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object DbMigration5to6 : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.run {
            execSQL("ALTER TABLE movies ADD COLUMN `vote` REAL DEFAULT NULL")
        }
    }
}

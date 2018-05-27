package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.database.Cursor
import org.junit.Assert

object MigrationShowingUtils {
    fun insertShowings4(db: SupportSQLiteDatabase) {
        db.execSQL(
            """INSERT INTO showings
                VALUES(1, 1, 1, "date1", "version1", NULL)"""
        )
        db.execSQL(
            """INSERT INTO showings
                VALUES(2, 2, 2, "date2", "version2", NULL)"""
        )
    }

    fun checkShowings4to5(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM showings ORDER BY id")
        Assert.assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertShowing(
            cursor, 1, 1, 1, "date1", "version1", null
        )
        cursor.moveToNext()
        assertShowing(
            cursor, 2, 2, 2, "date2", "version2", null
        )
        cursor.close()
    }

    private fun assertShowing(cursor: Cursor, id: Int, movieId: Int, cinemaId: Int, date: String,
                              version: String?, seasonId: Int?) {
        Assert.assertEquals(id.toLong(), cursor.getLong(0))
        Assert.assertEquals(movieId.toLong(), cursor.getLong(1))
        Assert.assertEquals(cinemaId.toLong(), cursor.getLong(2))
        Assert.assertEquals(date, cursor.getString(3))
        Assert.assertEquals(version, cursor.getString(4))
        Assert.assertEquals(seasonId, cursor.getString(5)?.toLong())
    }
}
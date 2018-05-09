/*package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.database.Cursor
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.migrations.getMigration_1_2
import xyz.arnau.muvicat.cache.db.migrations.getMigration_2_3
import xyz.arnau.muvicat.cache.db.MuvicatDatabase

@RunWith(AndroidJUnit4::class)
class MigrationsTest {
    companion object {
        private val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            MuvicatDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
            )

    @Test
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1)

        db.execSQL("""INSERT INTO movies
            VALUES(1, "title1", "originalTitle1", 1, "direction1", "cast1", "plot1",
            1, "posterUrl1", 1, "originalLanguage1", "ageRating1", "trailerUrl1")""")
        db.execSQL("""INSERT INTO movies
            VALUES(${2.toLong()}, "title2", "originalTitle2", 2, "direction2", "cast2", "plot2",
            2, "posterUrl2", 2, "originalLanguage2", "ageRating2", "trailerUrl2")""")

        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, Migration_1_2)

        val cursor = db.query("SELECT * FROM movies")
        assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertMovie(cursor, 1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", 1, "posterUrl1",
            1, "originalLanguage1", "ageRating1", "trailerUrl1")
        cursor.moveToNext()
        assertMovie(cursor, 2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", 2, "posterUrl2",
            2, "originalLanguage2", "ageRating2", "trailerUrl2")
        cursor.close()
    }

    @Test
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2)

        db.execSQL("""INSERT INTO movies
            VALUES(1, "title1", "originalTitle1", 1, "direction1", "cast1", "plot1",
            1, "posterUrl1", 1, "originalLanguage1", "ageRating1", "trailerUrl1")""")
        db.execSQL("""INSERT INTO movies
            VALUES(${2.toLong()}, "title2", "originalTitle2", 2, "direction2", "cast2", "plot2",
            2, "posterUrl2", 2, "originalLanguage2", "ageRating2", "trailerUrl2")""")

        db.execSQL("""INSERT INTO cinemas
            VALUES(1, "name1", "address1", "town1", "region1", "region2", 1)""")
        db.execSQL("""INSERT INTO cinemas
            VALUES(2, "name2", "address2", null, null, null, null)""")

        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, Migration_2_3)

        val cursor = db.query("SELECT * FROM movies")
        assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertMovie(cursor, 1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", 1, "posterUrl1",
            1, "originalLanguage1", "ageRating1", "trailerUrl1")
        cursor.moveToNext()
        assertMovie(cursor, 2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", 2, "posterUrl2",
            2, "originalLanguage2", "ageRating2", "trailerUrl2")
        cursor.close()
    }

    private fun assertMovie(cursor: Cursor, id: Int, title: String, originalTitle: String, year: Int,
                            direction: String, cast: String, plot: String, releaseDate: Int,
                            posterUrl: String, priority: Int, originalLanguage: String,
                            ageRating: String, trailerUrl: String) {
        assertEquals(id.toLong(), cursor.getLong(0))
        assertEquals(title, cursor.getString(1))
        assertEquals(originalTitle, cursor.getString(2))
        assertEquals(year, cursor.getInt(3))
        assertEquals(direction, cursor.getString(4))
        assertEquals(cast, cursor.getString(5))
        assertEquals(plot, cursor.getString(6))
        assertEquals(releaseDate.toLong(), cursor.getLong(7))
        assertEquals(posterUrl, cursor.getString(8))
        assertEquals(priority, cursor.getInt(9))
        assertEquals(originalLanguage, cursor.getString(10))
        assertEquals(ageRating, cursor.getString(11))
        assertEquals(trailerUrl, cursor.getString(12))
    }

    private fun assertCinema(cursor: Cursor, id: Int, name: String, address: String, town: String,
                            region: String, province: String, postalCode: Int) {
        assertEquals(id.toLong(), cursor.getLong(0))
        assertEquals(name, cursor.getString(1))
        assertEquals(address, cursor.getString(2))
        assertEquals(town, cursor.getString(3))
        assertEquals(region, cursor.getString(4))
        assertEquals(province, cursor.getString(5))
        assertEquals(postalCode, cursor.getInt(6))
    }
}*/
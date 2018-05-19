package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.database.Cursor
import org.junit.Assert

object MigrationMovieUtils {
    fun insertMoviesOld(db: SupportSQLiteDatabase) {
        db.execSQL(
            """INSERT INTO movies
                VALUES(1, "title1", "originalTitle1", 1, "direction1", "cast1", "plot1",
                1, "posterUrl1", 1, "originalLanguage1", "ageRating1", "trailerUrl1")"""
        )
        db.execSQL(
            """INSERT INTO movies
                VALUES(${2.toLong()}, "title2", "originalTitle2", 2, "direction2", "cast2", "plot2",
                2, "posterUrl2", 2, "originalLanguage2", "ageRating2", "trailerUrl2")"""
        )
    }

    fun checkMoviesOld(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM movies ORDER BY id")
        Assert.assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertMovieOld(
            cursor, 1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", 1, "posterUrl1",
            1, "originalLanguage1", "ageRating1", "trailerUrl1"
        )
        cursor.moveToNext()
        assertMovieOld(
            cursor, 2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", 2, "posterUrl2",
            2, "originalLanguage2", "ageRating2", "trailerUrl2"
        )
        cursor.close()
    }

    fun checkMovies(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM movies ORDER BY id")
        Assert.assertEquals(2, cursor.count)
        cursor.moveToNext()
        assertMovie(
            cursor, 1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", 1, "posterUrl1",
            1, "originalLanguage1", "ageRating1", "trailerUrl1"
        )
        cursor.moveToNext()
        assertMovie(
            cursor, 2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", 2, "posterUrl2",
            2, "originalLanguage2", "ageRating2", "trailerUrl2"
        )
        cursor.close()
    }

    private fun assertMovieOld(
        cursor: Cursor, id: Int, title: String, originalTitle: String, year: Int,
        direction: String, cast: String, plot: String, releaseDate: Int,
        posterUrl: String, priority: Int, originalLanguage: String,
        ageRating: String, trailerUrl: String
    ) {
        Assert.assertEquals(id.toLong(), cursor.getLong(0))
        Assert.assertEquals(title, cursor.getString(1))
        Assert.assertEquals(originalTitle, cursor.getString(2))
        Assert.assertEquals(year, cursor.getInt(3))
        Assert.assertEquals(direction, cursor.getString(4))
        Assert.assertEquals(cast, cursor.getString(5))
        Assert.assertEquals(plot, cursor.getString(6))
        Assert.assertEquals(releaseDate.toLong(), cursor.getLong(7))
        Assert.assertEquals(posterUrl, cursor.getString(8))
        Assert.assertEquals(priority, cursor.getInt(9))
        Assert.assertEquals(originalLanguage, cursor.getString(10))
        Assert.assertEquals(ageRating, cursor.getString(11))
        Assert.assertEquals(trailerUrl, cursor.getString(12))
    }

    private fun assertMovie(
        cursor: Cursor, id: Int, title: String, originalTitle: String, year: Int,
        direction: String, cast: String, plot: String, releaseDate: Int,
        posterUrl: String, priority: Int, originalLanguage: String,
        ageRating: String, trailerUrl: String, tmdbId: Int? = null,
        runtime: Int? = null, genres: String? = null, backdropUrl: String? = null,
        voteAverage: Double? = null, voteCount: Int? = null
    ) {
        Assert.assertEquals(id.toLong(), cursor.getLong(0))
        Assert.assertEquals(title, cursor.getString(1))
        Assert.assertEquals(originalTitle, cursor.getString(2))
        Assert.assertEquals(year, cursor.getInt(3))
        Assert.assertEquals(direction, cursor.getString(4))
        Assert.assertEquals(cast, cursor.getString(5))
        Assert.assertEquals(plot, cursor.getString(6))
        Assert.assertEquals(releaseDate.toLong(), cursor.getLong(7))
        Assert.assertEquals(posterUrl, cursor.getString(8))
        Assert.assertEquals(priority, cursor.getInt(9))
        Assert.assertEquals(originalLanguage, cursor.getString(10))
        Assert.assertEquals(ageRating, cursor.getString(11))
        Assert.assertEquals(trailerUrl, cursor.getString(12))
        Assert.assertEquals(tmdbId, cursor.getString(13)?.toInt())
        Assert.assertEquals(runtime, cursor.getString(14)?.toInt())
        Assert.assertEquals(genres, cursor.getString(15))
        Assert.assertEquals(backdropUrl, cursor.getString(16))
        Assert.assertEquals(voteAverage, cursor.getString(17)?.toDouble())
        Assert.assertEquals(voteCount, cursor.getString(18)?.toDouble())
    }
}
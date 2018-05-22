@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.cache.db.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.database.Cursor
import org.junit.Assert.assertEquals
import xyz.arnau.muvicat.cache.db.StringListTypeConverter
import xyz.arnau.muvicat.cache.model.MovieEntity
import java.util.*

object MigrationMovieUtils {
    private val movies1 = listOf(
        MovieEntity(
            1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", Date(1, 1, 1),
            "posterUrl1", 1, "originalLanguage1",
            "ageRating1", "trailerUrl1"
        ),
        MovieEntity(
            2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", Date(2, 2, 2),
            "posterUrl2", 2, "originalLanguage2",
            "ageRating2", "trailerUrl2"
        )
    )
    private val movies5 = listOf(
        MovieEntity(
            1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", Date(1, 1, 1),
            "posterUrl1", 1, "originalLanguage1",
            "ageRating1", "trailerUrl1", 1, 1, listOf("genre1", "genre2"),
            "backdropUrl1", 1.1, 1
        ),
        MovieEntity(
            2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", Date(2, 2, 2),
            "posterUrl2", 2, "originalLanguage2",
            "ageRating2", "trailerUrl2", 2, 2, listOf("genre1", "genre2"),
            "backdropUrl2", 2.2, 2
        )
    )
    private val movies6 = listOf(
        MovieEntity(
            1, "title1", "originalTitle1", 1,
            "direction1", "cast1", "plot1", Date(1, 1, 1),
            "posterUrl1", 1, "originalLanguage1",
            "ageRating1", "trailerUrl1", 1, 1, listOf("genre1", "genre2"),
            "backdropUrl1", 1.1, 1, 1.1
        ),
        MovieEntity(
            2, "title2", "originalTitle2", 2,
            "direction2", "cast2", "plot2", Date(2, 2, 2),
            "posterUrl2", 2, "originalLanguage2",
            "ageRating2", "trailerUrl2", 2, 2, listOf("genre1", "genre2"),
            "backdropUrl2", 2.2, 2, 2.2
        )
    )

    fun insertMoviesVersion1(db: SupportSQLiteDatabase) {
        movies1.forEach {
            db.execSQL(
                """
                INSERT INTO movies VALUES(${it.id.toInt()}, "${it.title}", "${it.originalTitle}", ${it.year},
                "${it.direction}", "${it.cast}", "${it.plot}", ${it.releaseDate?.time}, "${it.posterUrl}", ${it.priority},
                "${it.originalLanguage}", "${it.ageRating}", "${it.trailerUrl}")
            """
            )
        }
    }

    fun insertMoviesVersion5(db: SupportSQLiteDatabase) {
        movies5.forEach {
            db.execSQL(
                """
                INSERT INTO movies VALUES(${it.id.toInt()}, "${it.title}", "${it.originalTitle}", ${it.year},
                "${it.direction}", "${it.cast}", "${it.plot}", ${it.releaseDate?.time}, "${it.posterUrl}", ${it.priority},
                "${it.originalLanguage}", "${it.ageRating}", "${it.trailerUrl}", ${it.tmdbId}, ${it.runtime},
                "${StringListTypeConverter().toString(it.genres)}", "${it.backdropUrl}", ${it.voteAverage},
                ${it.voteCount})
            """
            )
        }
    }

    fun checkMoviesVersion1to2(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM movies ORDER BY id")
        assertEquals(movies1.size, cursor.count)
        cursor.moveToNext()
        movies1.forEach {
            assertMovie2(cursor, it)
            cursor.moveToNext()
        }
        cursor.close()
    }

    fun checkMoviesVersion5to6(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM movies ORDER BY id")
        assertEquals(movies5.size, cursor.count)
        cursor.moveToNext()
        movies5.forEach {
            assertMovie6(cursor, it)
            cursor.moveToNext()
        }
        cursor.close()
    }

    fun checkMoviesVersion6to7(db: SupportSQLiteDatabase) {
        val cursor = db.query("SELECT * FROM movies ORDER BY id")
        assertEquals(movies6.size, cursor.count)
        cursor.moveToNext()
        movies6.forEach {
            assertMovie7(cursor, it)
            cursor.moveToNext()
        }
        cursor.close()
    }

    private fun assertMovie2(cursor: Cursor, movie: MovieEntity) {
        assertEquals(movie.id, cursor.getLong(0))
        assertEquals(movie.title, cursor.getString(1))
        assertEquals(movie.originalTitle, cursor.getString(2))
        assertEquals(movie.year, cursor.getInt(3))
        assertEquals(movie.direction, cursor.getString(4))
        assertEquals(movie.cast, cursor.getString(5))
        assertEquals(movie.plot, cursor.getString(6))
        assertEquals(movie.releaseDate?.time, cursor.getLong(7))
        assertEquals(movie.posterUrl, cursor.getString(8))
        assertEquals(movie.priority, cursor.getInt(9))
        assertEquals(movie.originalLanguage, cursor.getString(10))
        assertEquals(movie.ageRating, cursor.getString(11))
        assertEquals(movie.trailerUrl, cursor.getString(12))
    }

    private fun assertMovie6(cursor: Cursor, movie: MovieEntity) {
        assertMovie2(cursor, movie)
        assertEquals(movie.tmdbId, cursor.getString(13)?.toInt())
        assertEquals(movie.runtime, cursor.getString(14)?.toInt())
        assertEquals(movie.genres, StringListTypeConverter().toStringList(cursor.getString(15)))
        assertEquals(movie.backdropUrl, cursor.getString(16))
        assertEquals(movie.voteAverage, cursor.getString(17)?.toDouble())
        assertEquals(movie.voteCount, cursor.getString(18)?.toInt())
    }

    private fun assertMovie7(cursor: Cursor, movie: MovieEntity) {
        assertMovie6(cursor, movie)
        assertEquals(movie.vote, cursor.getString(19)?.toDouble())
    }
}
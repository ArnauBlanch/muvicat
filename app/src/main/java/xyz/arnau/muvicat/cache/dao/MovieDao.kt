package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import xyz.arnau.muvicat.data.model.Movie
import java.util.*

@Dao
abstract class MovieDao {
    @Query("SELECT * FROM movies ORDER BY priority DESC")
    abstract fun getMovies(): LiveData<List<Movie>>

    @Query("DELETE FROM movies")
    abstract fun clearMovies()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertMovies(movies: List<Movie>)

    @Query("SELECT * FROM movies LIMIT 1")
    abstract fun isCached(): Boolean

    @Query("SELECT id FROM movies")
    abstract fun getMovieIds(): List<Long>

    @Query("DELETE FROM movies WHERE id IN(:ids)")
    abstract fun deleteMoviesById(ids: List<Long>)

    @Query(
            "UPDATE movies SET " +
                    "title = COALESCE(:title, title), " +
                    "originalTitle = COALESCE(:originalTitle, originalTitle), " +
                    "year = COALESCE(:year, year), " +
                    "direction = COALESCE(:direction, direction), " +
                    "plot = COALESCE(:plot, plot), " +
                    "castList = COALESCE(:cast, castList), " +
                    "releaseDate = COALESCE(:releaseDate, releaseDate), " +
                    "posterUrl = COALESCE(:posterUrl, posterUrl), " +
                    "priority = COALESCE(:priority, priority), " +
                    "originalLanguage = COALESCE(:originalLanguage, originalLanguage), " +
                    "ageRating = COALESCE(:ageRating, ageRating), " +
                    "trailerUrl = COALESCE(:trailerUrl, trailerUrl) " +
                    "WHERE id = :id"
    )
    abstract fun updateMovie(
            id: Long, title: String?, originalTitle: String?, year: Int?,
            direction: String?, cast: String?, plot: String?, releaseDate: Date?,
            posterUrl: String?, priority: Int?, originalLanguage: String?,
            ageRating: String?, trailerUrl: String?
    )


    @Transaction
    open fun updateMovieDb(movies: List<Movie>) {
        val ids: List<Long> = movies.map { it.id }
        val existingIds = getMovieIds()

        val outdatedIds = existingIds - ids
        deleteMoviesById(outdatedIds)

        val newIds = ids - existingIds
        val newMovies = movies.filter { it.id in newIds }
        insertMovies(newMovies)

        val updatedIds = existingIds - outdatedIds
        val updatedMovies = movies.filter { it.id in updatedIds }
        updatedMovies.forEach {
            updateMovie(
                    it.id, it.title, it.originalTitle, it.year, it.direction, it.cast,
                    it.plot, it.releaseDate, it.posterUrl, it.priority, it.originalLanguage,
                    it.ageRating, it.trailerUrl
            )
        }
    }
}
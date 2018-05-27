package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import org.joda.time.LocalDate
import xyz.arnau.muvicat.cache.db.StringListTypeConverter
import xyz.arnau.muvicat.cache.model.CastMemberEntity
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.repository.model.CastMember
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieWithCast
import java.util.*

@Dao
abstract class MovieDao {

    @Query("SELECT * FROM movies ORDER BY priority DESC")
    abstract fun getMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE vote IS NOT NULL ORDER BY vote DESC")
    abstract fun getVotedMovies(): LiveData<List<Movie>>

    @Query(
        """SELECT *
        FROM movies
        WHERE id IN (SELECT DISTINCT movieId FROM showings WHERE date >= :today)
        ORDER BY priority DESC"""
    )
    abstract fun getCurrentMovies(today: Long = LocalDate.now().toDate().time): LiveData<List<Movie>>

    @Query(
        """SELECT *
        FROM movies
        WHERE id IN (SELECT DISTINCT movieId FROM showings WHERE cinemaId = :cinemaId AND date >= :today)
        ORDER BY priority DESC"""
    )
    abstract fun getCurrentMoviesByCinema(
        cinemaId: Long,
        today: Long = LocalDate.now().toDate().time
    ): LiveData<List<Movie>>

    @Transaction
    @Query("SELECT * FROM movies WHERE id = :movieId")
    abstract fun getMovie(movieId: Long): LiveData<MovieWithCast>

    @Query("DELETE FROM movies")
    abstract fun clearMovies()

    @Insert(onConflict = IGNORE)
    abstract fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT id FROM movies")
    abstract fun getMovieIds(): List<Long>

    @Query("DELETE FROM movies WHERE id IN(:ids) AND vote IS NULL")
    abstract fun deleteMoviesById(ids: List<Long>)

    @Query(
        """UPDATE movies SET
                title = COALESCE(:title, title),
                originalTitle = COALESCE(:originalTitle, originalTitle),
                year = COALESCE(:year, year),
                direction = COALESCE(:direction, direction),
                plot = COALESCE(:plot, plot),
                castList = COALESCE(:cast, castList),
                releaseDate = COALESCE(:releaseDate, releaseDate),
                posterUrl = COALESCE(:posterUrl, posterUrl),
                priority = COALESCE(:priority, priority),
                originalLanguage = COALESCE(:originalLanguage, originalLanguage),
                ageRating = COALESCE(:ageRating, ageRating),
                trailerUrl = COALESCE(:trailerUrl, trailerUrl)
                WHERE id = :id"""
    )
    abstract fun updateMovie(
        id: Long, title: String?, originalTitle: String?, year: Int?,
        direction: String?, cast: String?, plot: String?, releaseDate: Date?,
        posterUrl: String?, priority: Int?, originalLanguage: String?,
        ageRating: String?, trailerUrl: String?
    )


    @Transaction
    open fun updateMovieDb(movies: List<MovieEntity>) {
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

    @Query(
        """
        UPDATE movies SET
            tmdbId = COALESCE(:tmdbId, tmdbId),
            runtime = COALESCE(:runtime, runtime),
            genres = COALESCE(:genres, genres),
            backdropUrl = COALESCE(:backdropUrl, backdropUrl),
            voteAverage = COALESCE(:voteAverage, voteAverage),
            voteCount = COALESCE(:voteCount, voteCount)
        WHERE id = :movieId
    """
    )
    abstract fun updateMovieExtraInfo(
        movieId: Long, tmdbId: Int?, runtime: Int?, genres: String?,
        backdropUrl: String?, voteAverage: Double?, voteCount: Int?
    )

    @Insert(onConflict = IGNORE)
    abstract fun insertCastMembers(castMembers: List<CastMemberEntity>)

    @Query("SELECT tmdbId, `order`, name, character, profile_path, movieId FROM cast_members ORDER BY tmdbId, movieId")
    abstract fun getCastMembers(): List<CastMember>

    @Query("""
        SELECT tmdbId, `order`, name, character, profile_path, movieId
        FROM cast_members
        WHERE movieId = :movieId
        ORDER BY `order`, tmdbId
        """)
    abstract fun getCastMembersByMovie(movieId: Long): List<CastMember>

    @Transaction
    open fun addMovieExtraInfo(movieId: Long, extraInfo: MovieExtraInfo) {
        updateMovieExtraInfo(
            movieId, extraInfo.tmdbId, extraInfo.runtime, StringListTypeConverter().toString(extraInfo.genres),
            extraInfo.backdropUrl, extraInfo.voteAverage, extraInfo.voteCount
        )
        extraInfo.setMovieId(movieId)
        extraInfo.cast?.let { insertCastMembers(it) }
    }

    @Query("UPDATE movies SET vote = :vote WHERE id = :movieId")
    abstract fun voteMovie(movieId: Long, vote: Double)

    @Query("UPDATE movies SET vote = NULL WHERE id = :movieId")
    abstract fun unvoteMovie(movieId: Long)
}
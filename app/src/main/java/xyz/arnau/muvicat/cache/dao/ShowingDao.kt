package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.joda.time.LocalDate
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.Showing

@Dao
abstract class ShowingDao {
    @Query(
        """SELECT
            s.id, s.date, s.version, s.seasonId,
            m.id as movieId, m.title as movieTitle, m.posterUrl as moviePosterUrl,
            c.name as cinemaName, c.town as cinemaTown, c.region as cinemaRegion, c.province as cinemaProvince,
            pc.latitude as cinemaLatitude, pc.longitude as cinemaLongitude
        FROM showings s
            JOIN movies m ON s.movieId = m.id
            JOIN cinemas c ON s.cinemaId = c.id
            LEFT OUTER JOIN postal_codes pc ON c.postalCode = pc.code
        ORDER BY date, m.id, c.name"""
    )
    abstract fun getShowings(): LiveData<List<Showing>>

    @Query(
        """SELECT
            s.id, s.date, s.version, s.seasonId,
            m.id as movieId, m.title as movieTitle, m.posterUrl as moviePosterUrl,
            c.name as cinemaName, c.town as cinemaTown, c.region as cinemaRegion, c.province as cinemaProvince,
            pc.latitude as cinemaLatitude, pc.longitude as cinemaLongitude
        FROM showings s
            JOIN movies m ON s.movieId = m.id
            JOIN cinemas c ON s.cinemaId = c.id
            LEFT OUTER JOIN postal_codes pc ON c.postalCode = pc.code
        WHERE s.date >= :today
        ORDER BY date, m.id, c.name"""
    )
    abstract fun getCurrentShowings(today: Long = LocalDate.now().toDate().time): LiveData<List<Showing>>

    @Query(
        """SELECT
            s.id, s.date, s.version, s.seasonId,
            m.id as movieId, m.title as movieTitle, m.posterUrl as moviePosterUrl,
            c.name as cinemaName, c.town as cinemaTown, c.region as cinemaRegion, c.province as cinemaProvince,
            pc.latitude as cinemaLatitude, pc.longitude as cinemaLongitude
        FROM showings s
            JOIN movies m ON s.movieId = m.id
            JOIN cinemas c ON s.cinemaId = c.id
            LEFT OUTER JOIN postal_codes pc ON c.postalCode = pc.code
        WHERE s.cinemaId = :cinemaId AND s.date >= :today
        ORDER BY date, m.id, c.name"""
    )
    abstract fun getCurrentShowingsByCinema(cinemaId: Long, today: Long = LocalDate.now().toDate().time): LiveData<List<Showing>>

    @Query("DELETE FROM showings")
    abstract fun clearShowings()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertShowings(showings: List<ShowingEntity>)

    @Query("SELECT id FROM cinemas")
    internal abstract fun getCinemaIds(): List<Long>

    @Query("SELECT id FROM movies")
    internal abstract fun getMovieIds(): List<Long>

    @Transaction
    open fun updateShowingDb(showings: List<ShowingEntity>): Boolean {
        clearShowings()
        val movieIds = getMovieIds()
        val cinemaIds = getCinemaIds()
        val list = mutableListOf<ShowingEntity>()
        list.addAll(showings)
        list.removeAll { it.cinemaId !in cinemaIds || it.movieId !in movieIds }
        insertShowings(list)
        return list.size > 0
    }
}
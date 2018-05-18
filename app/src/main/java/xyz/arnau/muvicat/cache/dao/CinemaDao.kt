package xyz.arnau.muvicat.cache.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.joda.time.LocalDate
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.repository.model.Cinema

@Dao
abstract class CinemaDao {
    @Query(
        """
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude, 0 as numShowings, 0 as numMovies
        FROM cinemas c LEFT OUTER JOIN postal_codes pc ON c.postalCode != 0 AND c.postalCode = pc.code"""
    )
    abstract fun getCinemas(): LiveData<List<Cinema>>

    @Query(
        """
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude,
            COUNT(s.id) as numShowings, COUNT(DISTINCT s.movieId) as numMovies
        FROM cinemas c
            LEFT OUTER JOIN postal_codes pc ON c.postalCode != 0 AND c.postalCode = pc.code
            JOIN showings s ON s.cinemaId = c.id
        WHERE s.date >= :today
        GROUP BY c.id"""
    )
    abstract fun getCurrentCinemas(today: Long = LocalDate.now().toDate().time): LiveData<List<Cinema>>

    @Query(
        """
        SELECT c.id, c.name, c.address, c.town, c.region, c.province, pc.latitude, pc.longitude,
            COUNT(s.id) as numShowings, COUNT(DISTINCT s.movieId) as numMovies
        FROM cinemas c
            LEFT OUTER JOIN postal_codes pc ON c.postalCode != 0 AND c.postalCode = pc.code
            JOIN showings s ON s.cinemaId = c.id
        WHERE s.date >= :today AND c.id = :cinemaId
        GROUP BY c.id"""
    )
    abstract fun getCinema(cinemaId: Long, today: Long = LocalDate.now().toDate().time): LiveData<Cinema>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCinemas(cinemas: List<CinemaEntity>)

    @Query("DELETE FROM cinemas")
    abstract fun clearCinemas()

    @Transaction
    open fun updateCinemaDb(cinemas: List<CinemaEntity>) {
        clearCinemas()
        insertCinemas(cinemas)
    }
}
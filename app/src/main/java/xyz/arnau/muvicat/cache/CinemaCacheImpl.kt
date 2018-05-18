package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.repository.model.Cinema
import xyz.arnau.muvicat.repository.data.CinemaCache
import javax.inject.Inject

class CinemaCacheImpl @Inject constructor(private val cinemaDao: CinemaDao) : CinemaCache {
    override fun getCinemas(): LiveData<List<Cinema>> {
        return cinemaDao.getCurrentCinemas()
    }

    override fun getCinema(cinemaId: Long): LiveData<Cinema> {
        return cinemaDao.getCinema(cinemaId)
    }

    override fun updateCinemas(cinemas: List<CinemaEntity>) {
        cinemaDao.updateCinemaDb(cinemas)
    }
}
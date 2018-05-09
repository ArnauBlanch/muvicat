package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.repository.CinemaCache
import javax.inject.Inject

class CinemaCacheImpl @Inject constructor(private val cinemaDao: CinemaDao) : CinemaCache {
    override fun getCinemas(): LiveData<List<CinemaInfo>> {
        return cinemaDao.getCinemas()
    }

    override fun getCinema(cinemaId: Long): LiveData<CinemaInfo> {
        return cinemaDao.getCinema(cinemaId)
    }

    override fun updateCinemas(cinemas: List<Cinema>) {
        cinemaDao.updateCinemaDb(cinemas)
    }
}
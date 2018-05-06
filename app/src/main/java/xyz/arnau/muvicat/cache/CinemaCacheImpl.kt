package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import javax.inject.Inject

class CinemaCacheImpl @Inject constructor(
    private val cinemaDao: CinemaDao,
    private val preferencesHelper: PreferencesHelper
) : CinemaCache {

    companion object {
        const val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong() // $COVERAGE-IGNORE$
    }

    override fun getCinemas(): LiveData<List<CinemaInfo>> {
        return cinemaDao.getCinemas()
    }

    override fun getCinema(cinemaId: Long): LiveData<CinemaInfo> {
        return cinemaDao.getCinema(cinemaId)
    }

    override fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferencesHelper.cinemaslastUpdateTime
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    override fun updateCinemas(cinemas: List<Cinema>) {
        cinemaDao.updateCinemaDb(cinemas)
    }
}
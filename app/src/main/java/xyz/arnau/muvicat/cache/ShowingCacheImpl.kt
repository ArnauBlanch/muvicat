package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.dao.ShowingDao
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.repository.model.CinemaShowing
import xyz.arnau.muvicat.repository.model.MovieShowing
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.repository.data.ShowingCache
import javax.inject.Inject

class ShowingCacheImpl @Inject constructor(private val showingDao: ShowingDao) : ShowingCache {
    override fun getShowings(): LiveData<List<Showing>> {
        return showingDao.getCurrentShowings()
    }

    override fun getShowingsByCinema(cinemaId: Long): LiveData<List<CinemaShowing>> {
        return showingDao.getCurrentShowingsByCinema(cinemaId)
    }

    override fun getShowingsByMovie(movieId: Long): LiveData<List<MovieShowing>> {
        return showingDao.getCurrentShowingsByMovie(movieId)
    }

    override fun updateShowings(showings: List<ShowingEntity>): Boolean {
        return showingDao.updateShowingDb(showings)
    }
}
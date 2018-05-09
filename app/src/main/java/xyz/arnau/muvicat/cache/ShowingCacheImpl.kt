package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.dao.ShowingDao
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.repository.ShowingCache
import javax.inject.Inject

class ShowingCacheImpl @Inject constructor(private val showingDao: ShowingDao) : ShowingCache {
    override fun getShowings(): LiveData<List<Showing>> {
        return showingDao.getShowings()
    }

    override fun updateShowings(showings: List<ShowingEntity>) {
        showingDao.updateShowingDb(showings)
    }
}
package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.utils.NetworkBoundResource
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.utils.AppExecutors
import java.util.concurrent.CountDownLatch

class CinemaRepository(
    private val cinemaCache: CinemaCache,
    private val gencatRemote: GencatRemote,
    private val appExecutors: AppExecutors,
    private val preferencesHelper: RepoPreferencesHelper,
    private val countDownLatch: CountDownLatch
) {
    companion object {
        const val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong()
    }

    private var countDownDone = false

    internal fun hasExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferencesHelper.cinemaslastUpdateTime
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    fun getCinemas(): LiveData<Resource<List<Cinema>>> =
        object : NetworkBoundResource<List<Cinema>, List<CinemaEntity>>(appExecutors) {
            override fun saveResponse(response: Response<List<CinemaEntity>>) {
                if (response.type == SUCCESSFUL) {
                    response.body!!.let {
                        cinemaCache.updateCinemas(it)
                        preferencesHelper.cinemasUpdated()
                        response.callback?.onDataUpdated()
                    }
                }
                if (response.type == NOT_MODIFIED) {
                    preferencesHelper.cinemasUpdated()
                }

                if (!countDownDone) {
                    countDownLatch.countDown()
                    countDownDone = true
                }
            }

            override fun onFetchFailed() {
                if (!countDownDone) {
                    countDownLatch.countDown()
                    countDownDone = true
                }
            }

            override fun createCall(): LiveData<Response<List<CinemaEntity>>> {
                return gencatRemote.getCinemas()
            }

            override fun loadFromDb(): LiveData<List<Cinema>> {
                return cinemaCache.getCinemas()
            }

            override fun shouldFetch(data: List<Cinema>?): Boolean {
                val shouldFetch = data == null || data.isEmpty() || hasExpired()
                if (!shouldFetch && !countDownDone) {
                    countDownLatch.countDown()
                    countDownDone = true
                }
                return shouldFetch
            }

        }.asLiveData()

    fun getCinema(id: Long): LiveData<Resource<Cinema>> {
        return Transformations.switchMap(cinemaCache.getCinema(id), { cinema ->
            val liveData = MutableLiveData<Resource<Cinema>>()
            if (cinema == null) {
                liveData.postValue(Resource.error("Cinema not found", null))
            } else {
                liveData.postValue(Resource.success(cinema))
            }
            liveData
        })
    }
}
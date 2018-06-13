package xyz.arnau.muvicat.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.repository.data.GencatRemote
import xyz.arnau.muvicat.repository.data.ShowingCache
import xyz.arnau.muvicat.repository.model.CinemaShowing
import xyz.arnau.muvicat.repository.model.MovieShowing
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.repository.utils.NetworkBoundResource
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.utils.AfterCountDownLatch
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.utils.BeforeCountDownLatch

class ShowingRepository(
    private val showingCache: ShowingCache,
    private val gencatRemote: GencatRemote,
    private val appExecutors: AppExecutors,
    private val preferencesHelper: RepoPreferencesHelper,
    private val beforeLatch: BeforeCountDownLatch,
    private val afterLatch: AfterCountDownLatch
) {
    companion object {
        const val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong()
    }

    private var countDownDone = false
    private var currentlyFetching = false

    internal fun hasExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferencesHelper.showingslastUpdateTime
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    fun getShowings(): LiveData<Resource<List<Showing>>> =
        object : NetworkBoundResource<List<Showing>, List<ShowingEntity>>(appExecutors) {
            override fun saveResponse(response: Response<List<ShowingEntity>>) {
                if (response.type == SUCCESSFUL) {
                    response.body!!.let { showings ->
                        beforeLatch.await()
                        val hasUpdated = showingCache.updateShowings(showings)
                        if (hasUpdated) {
                            preferencesHelper.showingsUpdated()
                            response.callback?.onDataUpdated()
                        }
                    }
                }
                if (response.type == NOT_MODIFIED) {
                    preferencesHelper.showingsUpdated()
                }
                if (!countDownDone) {
                    afterLatch.countDown()
                    countDownDone = true
                }
                currentlyFetching = false
            }

            override fun onFetchFailed() {
                if (!countDownDone) {
                    afterLatch.countDown()
                    countDownDone = true
                }
                currentlyFetching = false
            }

            override fun createCall(): LiveData<Response<List<ShowingEntity>>> {
                currentlyFetching = true
                return gencatRemote.getShowings()
            }

            override fun loadFromDb(): LiveData<List<Showing>> {
                return showingCache.getShowings()
            }

            override fun shouldFetch(data: List<Showing>?): Boolean {
                val shouldFetch = !currentlyFetching && (data == null || data.isEmpty() || hasExpired())
                if (!shouldFetch && !countDownDone) {
                    afterLatch.countDown()
                    countDownDone = true
                }
                return shouldFetch
            }

        }.asLiveData()

    fun getShowingsByCinema(cinemaId: Long): LiveData<Resource<List<CinemaShowing>>> {
        return Transformations.switchMap(showingCache.getShowingsByCinema(cinemaId), {
            val liveData = MutableLiveData<Resource<List<CinemaShowing>>>()
            if (it == null || it.isEmpty()) {
                liveData.postValue(Resource.error("Showings not found", it))
            } else {
                liveData.postValue(Resource.success(it))
            }
            liveData
        })
    }

    fun getShowingsByMovie(movieId: Long): LiveData<Resource<List<MovieShowing>>> {
        return Transformations.switchMap(showingCache.getShowingsByMovie(movieId), {
            val liveData = MutableLiveData<Resource<List<MovieShowing>>>()
            if (it == null || it.isEmpty()) {
                liveData.postValue(Resource.error("Showings not found", it))
            } else {
                liveData.postValue(Resource.success(it))
            }
            liveData
        })
    }
}
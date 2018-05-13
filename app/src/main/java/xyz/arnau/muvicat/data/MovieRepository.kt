package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.utils.NetworkBoundResource
import xyz.arnau.muvicat.data.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.utils.AfterCountDownLatch
import xyz.arnau.muvicat.utils.BeforeCountDownLatch
import java.util.concurrent.CountDownLatch

class MovieRepository(
    private val movieCache: MovieCache,
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

    internal fun hasExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferencesHelper.movieslastUpdateTime
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    fun getMovies(): LiveData<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieEntity>>(appExecutors) {
            override fun saveResponse(response: Response<List<MovieEntity>>) {
                if (response.type == SUCCESSFUL) {
                    response.body!!.let {
                        movieCache.updateMovies(it)
                        preferencesHelper.moviesUpdated()
                        response.callback?.onDataUpdated()
                    }
                }
                if (response.type == NOT_MODIFIED) {
                    preferencesHelper.moviesUpdated()
                }

                if (!countDownDone) {
                    beforeLatch.countDown()
                    countDownDone = true
                }
                afterLatch.await()
            }

            override fun onFetchFailed() {
                if (!countDownDone) {
                    beforeLatch.countDown()
                    countDownDone = true
                }
                afterLatch.await()
            }

            override fun createCall(): LiveData<Response<List<MovieEntity>>> {
                return gencatRemote.getMovies()
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return movieCache.getMovies()
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                val shouldFetch = data == null || data.isEmpty() || hasExpired()
                if (!shouldFetch && !countDownDone) {
                    beforeLatch.countDown()
                    countDownDone = true
                }
                return shouldFetch
            }

        }.asLiveData()

    fun getMoviesByCinema(cinemaId: Long): LiveData<Resource<List<Movie>>> {
        return Transformations.switchMap(movieCache.getMoviesByCinema(cinemaId), {
            val liveData = MutableLiveData<Resource<List<Movie>>>()
            if (it == null || it.isEmpty()) {
                liveData.postValue(Resource.error("Movies not found", it))
            } else {
                liveData.postValue(Resource.success(it))
            }
            liveData
        })
    }

    fun getMovie(id: Long): LiveData<Resource<Movie>> {
        return Transformations.switchMap(movieCache.getMovie(id), { movie ->
            val liveData = MutableLiveData<Resource<Movie>>()
            if (movie == null) {
                liveData.postValue(Resource.error("Movie not found", null))
            } else {
                liveData.postValue(Resource.success(movie))
            }
            liveData
        })
    }
}
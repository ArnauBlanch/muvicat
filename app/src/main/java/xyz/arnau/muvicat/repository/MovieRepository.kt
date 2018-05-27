package xyz.arnau.muvicat.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import xyz.arnau.muvicat.repository.data.GencatRemote
import xyz.arnau.muvicat.repository.data.MovieCache
import xyz.arnau.muvicat.repository.data.TMDBRemote
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieWithCast
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.utils.NetworkBoundResource
import xyz.arnau.muvicat.repository.utils.RepoPreferencesHelper
import xyz.arnau.muvicat.utils.AfterCountDownLatch
import xyz.arnau.muvicat.utils.AppExecutors
import xyz.arnau.muvicat.utils.BeforeCountDownLatch

class MovieRepository(
    private val movieCache: MovieCache,
    private val gencatRemote: GencatRemote,
    private val tmdbRemote: TMDBRemote,
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

    fun getVotedMovies(): LiveData<Resource<List<Movie>>> {
        return Transformations.switchMap(movieCache.getVotedMovies(), {
            val liveData = MutableLiveData<Resource<List<Movie>>>()
            appExecutors.diskIO().execute {
                beforeLatch.await()
                if (it == null) {
                    liveData.postValue(Resource.error("Voted movies not found", it))
                } else {
                    liveData.postValue(Resource.success(it))
                }
            }
            liveData
        })
    }

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

    fun getMovie(movieId: Long): LiveData<Resource<MovieWithCast>> =
        object : NetworkBoundResource<MovieWithCast, MovieExtraInfo>(appExecutors) {
            lateinit var movieTitle: String
            private var tmdbId: Int? = null

            override fun saveResponse(response: Response<MovieExtraInfo>) {
                if (response.type == SUCCESSFUL) {
                    response.body!!.let {
                        movieCache.updateExtraMovieInfo(movieId, it)
                    }
                }
            }

            override fun loadFromDb(): LiveData<MovieWithCast> {
                return movieCache.getMovie(movieId)
            }

            override fun onFetchFailed() {}

            override fun createCall(): LiveData<Response<MovieExtraInfo>> {
                tmdbId?.let { return tmdbRemote.getMovie(it) }
                return tmdbRemote.getMovie(movieTitle)
            }

            override fun shouldFetch(data: MovieWithCast?): Boolean {
                data?.let {
                    movieTitle = it.movie.originalTitle ?: it.movie.title!!
                    tmdbId = it.movie.tmdbId
                }
                return data != null
            }

        }.asLiveData()

    fun rateMovie(movieId: Long, tmdbId: Int, rating: Double): LiveData<Resource<Boolean>> {
        return Transformations.switchMap(tmdbRemote.rateMovie(tmdbId, rating), {
            val data = MutableLiveData<Resource<Boolean>>()
            if (it.type == SUCCESSFUL) {
                appExecutors.diskIO().execute {
                    try {
                        movieCache.voteMovie(movieId, rating)
                        data.postValue(Resource.success(true))
                    } catch (e: Exception) {
                        data.postValue(Resource.error("db error", null))
                    }
                }
            } else {
                data.postValue(Resource.error(it.errorMessage, null))
            }
            data
        })
    }

    fun unrateMovie(movieId: Long, tmdbId: Int): LiveData<Resource<Boolean>> {
        return Transformations.switchMap(tmdbRemote.unrateMovie(tmdbId), {
            val data = MutableLiveData<Resource<Boolean>>()
            if (it.type == SUCCESSFUL) {
                appExecutors.diskIO().execute {
                    try {
                        movieCache.unvoteMovie(movieId)
                        data.postValue(Resource.success(true))
                    } catch (e: Exception) {
                        data.postValue(Resource.error("db error", null))
                    }
                }
            } else {
                data.postValue(Resource.error(it.errorMessage, null))
            }
            data
        })
    }
}
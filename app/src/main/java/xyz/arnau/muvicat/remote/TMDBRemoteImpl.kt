package xyz.arnau.muvicat.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.remote.mapper.TMDBMovieInfoMapper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.remote.model.tmdb.TMDBRateMovieBody
import xyz.arnau.muvicat.remote.service.TMDBService
import xyz.arnau.muvicat.remote.utils.RemotePreferencesHelper
import xyz.arnau.muvicat.repository.data.TMDBRemote

class TMDBRemoteImpl(
    private val tmdbService: TMDBService,
    private val tmdbMovieInfoMapper: TMDBMovieInfoMapper,
    private val preferencesHelper: RemotePreferencesHelper
) : TMDBRemote {

    override fun getMovie(movieTitle: String): LiveData<Response<MovieExtraInfo>> {
        return Transformations.switchMap(tmdbService.searchMovie(movieTitle), { apiResponse ->
            if (apiResponse.status == ResponseStatus.SUCCESSFUL && apiResponse.body != null
                && apiResponse.body!!.results.isNotEmpty()
            ) {
                val searchedMovie = apiResponse.body!!.results[0]
                Transformations.switchMap(
                    tmdbService.getMovie(
                        searchedMovie.id,
                        append = "credits"
                    ), { apiResponse2 ->
                        val data = MutableLiveData<Response<MovieExtraInfo>>()
                        if (apiResponse2.status == ResponseStatus.SUCCESSFUL && apiResponse2.body != null) {
                            val movieInfo = apiResponse2.body!!
                            val extraMovieInfo =
                                tmdbMovieInfoMapper.mapFromRemote(Pair(searchedMovie, movieInfo))
                            data.postValue(Response.successful(extraMovieInfo))
                            data
                        } else {
                            data.postValue(Response.error(apiResponse2.errorMessage, null))
                            data
                        }
                    })
            } else {
                val errorData = MutableLiveData<Response<MovieExtraInfo>>()
                errorData.postValue(Response.error(apiResponse.errorMessage))
                errorData
            }
        })
    }

    override fun getMovie(tmdbId: Int): LiveData<Response<MovieExtraInfo>> {
        return Transformations.switchMap(tmdbService.getMovie(tmdbId, append = ""), { apiResponse ->
            val data = MutableLiveData<Response<MovieExtraInfo>>()
            if (apiResponse.status == ResponseStatus.SUCCESSFUL && apiResponse.body != null) {
                val movieInfo = apiResponse.body!!
                val extraMovieInfo = tmdbMovieInfoMapper.mapFromRemote(Pair(null, movieInfo))
                data.postValue(Response.successful(extraMovieInfo))
                data
            } else {
                data.postValue(Response.error(apiResponse.errorMessage))
                data
            }
        })
    }

    override fun rateMovie(tmdbId: Int, rating: Double): LiveData<Response<Boolean>> =
            Transformations.switchMap(getGuestSession(), {
                if (it.type == ResponseStatus.SUCCESSFUL) {
                    val guestSessionId = it.body!!
                    Transformations.switchMap(
                        tmdbService.rateMovie(tmdbId, TMDBRateMovieBody(rating), guestSessionId), {
                            val response = MutableLiveData<Response<Boolean>>()
                            if (it.status == ResponseStatus.SUCCESSFUL) {
                                response.postValue(Response.successful(true))
                            } else {
                                response.postValue(Response.error(it.errorMessage))
                            }
                            response
                        })
                } else {
                    val response = MutableLiveData<Response<Boolean>>()
                    response.postValue(Response.error(it.errorMessage))
                    response
                }
            })

    override fun unrateMovie(tmdbId: Int): LiveData<Response<Boolean>> =
        Transformations.switchMap(getGuestSession(), {
            if (it.type == ResponseStatus.SUCCESSFUL) {
                val guestSessionId = it.body!!
                Transformations.switchMap(
                    tmdbService.unrateMovie(tmdbId, guestSessionId), {
                        val response = MutableLiveData<Response<Boolean>>()
                        if (it.status == ResponseStatus.SUCCESSFUL) {
                            response.postValue(Response.successful(true))
                        } else {
                            response.postValue(Response.error(it.errorMessage))
                        }
                        response
                    })
            } else {
                val response = MutableLiveData<Response<Boolean>>()
                response.postValue(Response.error(it.errorMessage))
                response
            }
        })

    internal fun getGuestSession(): LiveData<Response<String>> {
        val liveData = MutableLiveData<Response<String>>()
        preferencesHelper.tmdbGuestSessionId?.let {
            liveData.postValue(Response.successful(it))
            return liveData
        }
        return createGuestSession()
    }

    private fun createGuestSession(): LiveData<Response<String>> =
        Transformations.switchMap(tmdbService.createGuestSession(), { apiResponse ->
            val data = MutableLiveData<Response<String>>()
            if (apiResponse.status == ResponseStatus.SUCCESSFUL && apiResponse.body != null
                && apiResponse.body!!.success &&
                apiResponse.body!!.guest_session_id != null) {
                preferencesHelper.tmdbGuestSessionId = apiResponse.body!!.guest_session_id
                data.postValue(Response.successful(apiResponse.body!!.guest_session_id))
                data
            } else {
                data.postValue(Response.error(apiResponse.errorMessage))
                data
            }
        })
}
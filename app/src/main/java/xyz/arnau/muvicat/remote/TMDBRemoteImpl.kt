package xyz.arnau.muvicat.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.remote.mapper.TMDBMovieInfoMapper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.remote.service.TMDBService
import xyz.arnau.muvicat.repository.data.TMDBRemote

class TMDBRemoteImpl(
    private val tmdbService: TMDBService,
    private val tmdbMovieInfoMapper: TMDBMovieInfoMapper
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
                            data.postValue(Response.error(apiResponse.errorMessage))
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

    override fun getMovie(tmdbId: Int): LiveData<Response<MovieExtraInfo>> =
        Transformations.switchMap(tmdbService.getMovie(tmdbId, append = ""), { apiResponse ->
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
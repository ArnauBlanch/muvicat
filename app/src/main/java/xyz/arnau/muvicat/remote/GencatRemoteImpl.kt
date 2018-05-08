package xyz.arnau.muvicat.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.mapper.GencatCinemaListEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatMovieListEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatShowingListEntityMapper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.service.GencatService

class GencatRemoteImpl(
    private val gencatService: GencatService,
    private val moviesEntityMapper: GencatMovieListEntityMapper,
    private val cinemasEntityMapper: GencatCinemaListEntityMapper,
    private val showingsEntityMapper: GencatShowingListEntityMapper
) : GencatRemote {
    override fun getMovies(eTag: String?): LiveData<Response<List<Movie>>> {
        return Transformations.switchMap(gencatService.getMovies(eTag), { apiResponse ->
            val data = MutableLiveData<Response<List<Movie>>>()
            data.postValue(
                Response(
                    moviesEntityMapper.mapFromRemote(apiResponse.body),
                    apiResponse.errorMessage,
                    apiResponse.status,
                    apiResponse.eTag
                )
            )
            data
        })
    }

    override fun getCinemas(eTag: String?): LiveData<Response<List<Cinema>>> {
        return Transformations.switchMap(gencatService.getCinemas(eTag), { apiResponse ->
            val data = MutableLiveData<Response<List<Cinema>>>()
            data.postValue(
                Response(
                    cinemasEntityMapper.mapFromRemote(apiResponse.body),
                    apiResponse.errorMessage,
                    apiResponse.status,
                    apiResponse.eTag
                )
            )
            data
        })
    }

    override fun getShowings(eTag: String?): LiveData<Response<List<Showing>>> {
        return Transformations.switchMap(gencatService.getShowings(eTag), { apiResponse ->
            val data = MutableLiveData<Response<List<Showing>>>()
            data.postValue(
                Response(
                    showingsEntityMapper.mapFromRemote(apiResponse.body),
                    apiResponse.errorMessage,
                    apiResponse.status,
                    apiResponse.eTag
                )
            )
            data
        })
    }
}
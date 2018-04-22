package xyz.arnau.muvicat.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.mapper.GencatMovieListEntityMapper
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.model.Response

class GencatRemoteImpl(
    private val gencatService: GencatService,
    private val entityMapper: GencatMovieListEntityMapper
) : GencatRemote {
    override fun getMovies(): LiveData<Response<List<Movie>>> {
        return Transformations.switchMap(gencatService.getMovies(null), { apiResponse ->
            val data = MutableLiveData<Response<List<Movie>>>()
            data.postValue(
                    Response(
                            entityMapper.mapFromRemote(apiResponse.body),
                            apiResponse.errorMessage,
                            apiResponse.status
                    )
            )
            data
        })
    }
}
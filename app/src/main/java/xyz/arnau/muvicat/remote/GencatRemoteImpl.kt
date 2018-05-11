package xyz.arnau.muvicat.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.mapper.GencatCinemaListEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatMovieListEntityMapper
import xyz.arnau.muvicat.remote.mapper.GencatShowingListEntityMapper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.utils.RemotePreferencesHelper

class GencatRemoteImpl(
    private val gencatService: GencatService,
    private val preferencesHelper: RemotePreferencesHelper,
    private val moviesEntityMapper: GencatMovieListEntityMapper,
    private val cinemasEntityMapper: GencatCinemaListEntityMapper,
    private val showingsEntityMapper: GencatShowingListEntityMapper
) : GencatRemote {
    override fun getMovies(): LiveData<Response<List<MovieEntity>>> {
        val eTag = preferencesHelper.moviesETag

        return Transformations.switchMap(gencatService.getMovies(eTag), { apiResponse ->
            val data = MutableLiveData<Response<List<MovieEntity>>>()
            data.postValue(
                Response(
                    moviesEntityMapper.mapFromRemote(apiResponse.body),
                    apiResponse.errorMessage,
                    apiResponse.status,
                    object: DataUpdateCallback {
                        override fun onDataUpdated() {
                            if (apiResponse.eTag != null) {
                                preferencesHelper.moviesETag = apiResponse.eTag
                            }
                        }
                    }
                )
            )
            data
        })
    }

    override fun getCinemas(): LiveData<Response<List<CinemaEntity>>> {
        val eTag = preferencesHelper.cinemasETag

        return Transformations.switchMap(gencatService.getCinemas(eTag), { apiResponse ->
            val data = MutableLiveData<Response<List<CinemaEntity>>>()
            data.postValue(
                Response(
                    cinemasEntityMapper.mapFromRemote(apiResponse.body),
                    apiResponse.errorMessage,
                    apiResponse.status,
                    object: DataUpdateCallback {
                        override fun onDataUpdated() {
                            if (apiResponse.eTag != null) {
                                preferencesHelper.cinemasETag = apiResponse.eTag
                            }
                        }
                    }
                )
            )
            data
        })
    }

    override fun getShowings(): LiveData<Response<List<ShowingEntity>>> {
        val eTag = preferencesHelper.showingsETag

        return Transformations.switchMap(gencatService.getShowings(eTag), { apiResponse ->
            val data = MutableLiveData<Response<List<ShowingEntity>>>()
            data.postValue(
                Response(
                    showingsEntityMapper.mapFromRemote(apiResponse.body),
                    apiResponse.errorMessage,
                    apiResponse.status,
                    object: DataUpdateCallback {
                        override fun onDataUpdated() {
                            if (apiResponse.eTag != null) {
                                preferencesHelper.showingsETag = apiResponse.eTag
                            }
                        }
                    }
                )
            )
            data
        })
    }
}
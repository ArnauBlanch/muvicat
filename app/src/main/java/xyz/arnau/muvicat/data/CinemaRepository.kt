package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.CinemaCache
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.utils.NetworkBoundResource
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus.NOT_MODIFIED
import xyz.arnau.muvicat.remote.model.ResponseStatus.SUCCESSFUL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemaRepository @Inject constructor(
    private val cinemaCache: CinemaCache,
    private val gencatRemote: GencatRemote,
    private val appExecutors: AppExecutors,
    private val preferencesHelper: PreferencesHelper
) {

    fun getCinemas(): LiveData<Resource<List<Cinema>>> =
        object : NetworkBoundResource<List<Cinema>>(appExecutors) {
            override fun saveResponse(response: Response<List<Cinema>>) {
                if (response.type == SUCCESSFUL) {
                    response.body?.let { cinemaCache.updateCinemas(it) }
                }
                response.eTag?.let { preferencesHelper.cinemasETag = response.eTag }
                if ((response.type == SUCCESSFUL && response.body != null)
                    || response.type == NOT_MODIFIED
                ) {
                    preferencesHelper.cinemasUpdated()
                }
            }

            override fun createCall(): LiveData<Response<List<Cinema>>> {
                return gencatRemote.getCinemas(preferencesHelper.cinemasETag)
            }

            override fun loadFromDb(): LiveData<List<Cinema>> {
                return cinemaCache.getCinemas()
            }

            override fun shouldFetch(data: List<Cinema>?): Boolean {
                return data == null || data.isEmpty() || cinemaCache.isExpired()
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
package xyz.arnau.muvicat.data.repository

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xyz.arnau.muvicat.data.model.Movie

class MovieRepository constructor(
    private val movieCache: MovieCache,
    private val gencatRemote: GencatRemote
) {

    fun getMovies(): Flowable<List<Movie>> {
        return movieCache.getMovies()
            /*.doOnSuccess { isCached: Boolean ->
                print("hello!")
                if (!isCached || movieCache.isExpired()) {
                    gencatRemote.getMovies()
                        .map { movieCache.saveMovies(it) }
                }
            }*/
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                print("do on next")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { print("do on error") }
    }
}
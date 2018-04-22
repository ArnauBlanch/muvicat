package xyz.arnau.muvicat.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Movie

abstract class MovieCallback: SimpleCallback<LiveData<Resource<Movie>>, Exception>
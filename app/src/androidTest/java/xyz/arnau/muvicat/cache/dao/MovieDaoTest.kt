package xyz.arnau.muvicat.cache.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.utils.getValueBlocking


@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private var muvicatDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            MuvicatDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun closeDb() {
        muvicatDatabase.close()
    }

    @Test
    fun getMoviesRetrievesData() {
        val movies = MovieFactory.makeMovieList(5)

        muvicatDatabase.movieDao().insertMovies(movies)

        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(movies.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }), retrievedMovies)
    }

    @Test
    fun getMoviesRetrievesDataWithNullValues() {
        val movies = MovieFactory.makeMovieListWithNullValues(6)

        muvicatDatabase.movieDao().insertMovies(movies)

        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(movies.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }), retrievedMovies)
    }

    @Test
    fun clearMoviesDeletesAllMovies() {
        val movies = MovieFactory.makeMovieList(5)
        muvicatDatabase.movieDao().insertMovies(movies)
        muvicatDatabase.movieDao().clearMovies()
        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(true, retrievedMovies!!.isEmpty())
    }


    @Test
    fun getMovieRetrievesMovie() {
        val movie = MovieFactory.makeMovie()

        muvicatDatabase.movieDao().insertMovies(listOf(movie))

        val retrievedMovie = muvicatDatabase.movieDao().getMovie(movie.id).getValueBlocking()
        assertEquals(movie, retrievedMovie)
    }

    @Test
    fun getMovieThrowsExceptionIfItDoesntExist() {
        val retrievedMovie = muvicatDatabase.movieDao().getMovie(123.toLong()).getValueBlocking()
        assertEquals(null, retrievedMovie)
    }

    @Test
    fun insertMoviesInDB() {
        val movies = MovieFactory.makeMovieList(3)
        muvicatDatabase.movieDao().insertMovies(movies)

        assertEquals(movies.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }), muvicatDatabase.movieDao().getMovies().getValueBlocking()!!)
    }

    @Test
    fun getMovieIdsReturnAllExistingIds() {
        val movies = MovieFactory.makeMovieList(5)
        val ids = movies.map { it.id }
        muvicatDatabase.movieDao().insertMovies(movies)

        assertEquals(ids.sorted(), muvicatDatabase.movieDao().getMovieIds())
    }

    @Test
    fun deleteMoviesByIdDeletesOutdatedMovies() {
        val oldMovies = MovieFactory.makeMovieList(2)
        val newMovies = MovieFactory.makeMovieList(5)
        muvicatDatabase.movieDao().insertMovies(oldMovies)
        muvicatDatabase.movieDao().insertMovies(newMovies)
        val oldIds = oldMovies.map { it.id }

        muvicatDatabase.movieDao().deleteMoviesById(oldIds)
        assertEquals(newMovies.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }), muvicatDatabase.movieDao().getMovies().getValueBlocking())
    }

    @Test
    fun updateMovieUpdateFields() {
        val movies = MovieFactory.makeMovieList(4)
        muvicatDatabase.movieDao().insertMovies(movies)

        val movieIdToUpdate = movies[1].id
        val updatedMovie = MovieFactory.makeMovie()
        updatedMovie.id = movieIdToUpdate
        muvicatDatabase.movieDao().updateMovie(updatedMovie.id, updatedMovie.title,
                updatedMovie.originalTitle, updatedMovie.year, updatedMovie.direction,
                updatedMovie.cast, updatedMovie.plot, updatedMovie.releaseDate,
                updatedMovie.posterUrl, updatedMovie.priority, updatedMovie.originalLanguage,
                updatedMovie.ageRating, updatedMovie.trailerUrl)

        val movies2 = (movies - movies[1]) + updatedMovie
        assertEquals(movies2.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
                muvicatDatabase.movieDao().getMovies().getValueBlocking())
    }

    @Test
    fun updateMovieDbWhenNothingIsCached() {
        val movies = MovieFactory.makeMovieList(5)
        muvicatDatabase.movieDao().updateMovieDb(movies)

        Assert.assertEquals(movies.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
                muvicatDatabase.movieDao().getMovies().getValueBlocking())
    }

    @Test
    fun updateMovieDbInsertsUpdatesAndDeletesAsExpected() {
        val movies1 = MovieFactory.makeMovieList(5)
        movies1.forEachIndexed { index, item -> item.id = index.toLong() }
        muvicatDatabase.movieDao().insertMovies(movies1)

        val updatedMovies = MovieFactory.makeMovieList(3)
        updatedMovies.forEachIndexed { index, item -> item.id = index.toLong() }

        val movies2 = MovieFactory.makeMovieList(4) + updatedMovies
        updatedMovies.forEachIndexed { index, item -> item.id = (index + 10).toLong() }
        muvicatDatabase.movieDao().updateMovieDb(movies2)

        assertEquals(movies2.sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
                muvicatDatabase.movieDao().getMovies().getValueBlocking())
    }
}
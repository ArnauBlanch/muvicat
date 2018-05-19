package xyz.arnau.muvicat.cache.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.db.StringListTypeConverter
import xyz.arnau.muvicat.cache.model.MovieCastMemberJoin
import xyz.arnau.muvicat.repository.model.CastMember
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.test.*
import xyz.arnau.muvicat.utils.getValueBlocking
import java.util.*


@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
class MovieDaoTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

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
        val movies = MovieEntityFactory.makeMovieEntityList(5)

        muvicatDatabase.movieDao().insertMovies(movies)

        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            retrievedMovies
        )
    }

    @Test
    fun getCurrentMoviesRetrievesMoviesWithCurrentShowings() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(3)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val showings = ShowingEntityFactory.makeShowingEntityList(3)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.cinemaId = cinemas[index].id
            showingEntity.movieId = movies[index].id
        }
        showings[0].date = Date(2018, 5, 13)
        showings[1].date = Date(2018, 6, 13)
        showings[2].date = Date(2018, 5, 12)
        muvicatDatabase.showingDao().insertShowings(showings)

        val currentMovieIds = listOf(showings[0].movieId, showings[1].movieId)

        val today = Date(2018, 5, 13).time
        val retrievedMovies = muvicatDatabase.movieDao().getCurrentMovies(today).getValueBlocking()
        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id })
                .filter { it.id in currentMovieIds },
            retrievedMovies
        )
    }

    @Test
    fun getCurrentMoviesByCinemaRetrievesCinemaMoviesWithCurrentShowings() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(2)
        muvicatDatabase.cinemaDao().insertCinemas(cinemas)

        val showings = ShowingEntityFactory.makeShowingEntityList(3)
        showings.forEachIndexed { index, showingEntity ->
            showingEntity.movieId = movies[index].id
        }
        showings[0].cinemaId = cinemas[0].id
        showings[1].cinemaId = cinemas[1].id
        showings[2].cinemaId = cinemas[0].id

        showings[0].date = Date(2018, 5, 13)
        showings[1].date = Date(2018, 6, 13)
        showings[2].date = Date(2018, 5, 12)
        muvicatDatabase.showingDao().insertShowings(showings)

        val currentMovieIds = listOf(showings[0].movieId)

        val today = Date(2018, 5, 13).time
        val retrievedMovies =
            muvicatDatabase.movieDao().getCurrentMoviesByCinema(cinemas[0].id, today)
                .getValueBlocking()
        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id })
                .filter { it.id in currentMovieIds },
            retrievedMovies
        )
    }

    @Test
    fun getMoviesRetrievesDataWithNullValues() {
        val movies = MovieEntityFactory.makeMovieEntityListWithNullValues(6)

        muvicatDatabase.movieDao().insertMovies(movies)

        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            retrievedMovies
        )
    }

    @Test
    fun clearMoviesDeletesAllMovies() {
        val movies = MovieEntityFactory.makeMovieEntityList(5)
        muvicatDatabase.movieDao().insertMovies(movies)
        muvicatDatabase.movieDao().clearMovies()
        val retrievedMovies = muvicatDatabase.movieDao().getMovies().getValueBlocking()
        assertEquals(true, retrievedMovies!!.isEmpty())
    }


    @Test
    fun getMovieRetrievesMovie() {
        val movie = MovieEntityFactory.makeMovieEntity()

        muvicatDatabase.movieDao().insertMovies(listOf(movie))

        val retrievedMovie = muvicatDatabase.movieDao().getMovie(movie.id).getValueBlocking()
        assertEquals(MovieMapper.mapFromMovieEntity(movie), retrievedMovie)
    }

    @Test
    fun getMovieReturnsNullIfItDoesntExist() {
        val retrievedMovie = muvicatDatabase.movieDao().getMovie(123.toLong()).getValueBlocking()
        assertEquals(null, retrievedMovie)
    }

    @Test
    fun insertMoviesInDB() {
        val movies = MovieEntityFactory.makeMovieEntityList(3)
        muvicatDatabase.movieDao().insertMovies(movies)

        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            muvicatDatabase.movieDao().getMovies().getValueBlocking()!!
        )
    }

    @Test
    fun getMovieIdsReturnAllExistingIds() {
        val movies = MovieEntityFactory.makeMovieEntityList(5)
        val ids = movies.map { it.id }
        muvicatDatabase.movieDao().insertMovies(movies)

        assertEquals(ids.sorted(), muvicatDatabase.movieDao().getMovieIds())
    }

    @Test
    fun deleteMoviesByIdDeletesOutdatedMovies() {
        val oldMovies = MovieEntityFactory.makeMovieEntityList(2)
        val newMovies = MovieEntityFactory.makeMovieEntityList(5)
        muvicatDatabase.movieDao().insertMovies(oldMovies)
        muvicatDatabase.movieDao().insertMovies(newMovies)
        val oldIds = oldMovies.map { it.id }

        muvicatDatabase.movieDao().deleteMoviesById(oldIds)
        assertEquals(
            MovieMapper.mapFromMovieEntityList(newMovies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            muvicatDatabase.movieDao().getMovies().getValueBlocking()
        )
    }

    @Test
    fun updateMovieUpdateFields() {
        val movies = MovieEntityFactory.makeMovieEntityList(4)
        muvicatDatabase.movieDao().insertMovies(movies)

        val movieIdToUpdate = movies[1].id
        val updatedMovie = MovieEntityFactory.makeMovieEntity()
        updatedMovie.id = movieIdToUpdate
        muvicatDatabase.movieDao().updateMovie(
            updatedMovie.id, updatedMovie.title,
            updatedMovie.originalTitle, updatedMovie.year, updatedMovie.direction,
            updatedMovie.cast, updatedMovie.plot, updatedMovie.releaseDate,
            updatedMovie.posterUrl, updatedMovie.priority, updatedMovie.originalLanguage,
            updatedMovie.ageRating, updatedMovie.trailerUrl
        )

        val movies2 = (movies - movies[1]) + updatedMovie
        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies2)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            muvicatDatabase.movieDao().getMovies().getValueBlocking()
        )
    }

    @Test
    fun updateMovieDbWhenNothingIsCached() {
        val movies = MovieEntityFactory.makeMovieEntityList(5)
        muvicatDatabase.movieDao().updateMovieDb(movies)

        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            muvicatDatabase.movieDao().getMovies().getValueBlocking()
        )
    }

    @Test
    fun updateMovieDbInsertsUpdatesAndDeletesAsExpected() {
        val movies1 = MovieEntityFactory.makeMovieEntityList(5)
        movies1.forEachIndexed { index, item -> item.id = index.toLong() }
        muvicatDatabase.movieDao().insertMovies(movies1)

        val castMembers = CastMemberEntityFactory.makeCastMemberEntityList(5)
        muvicatDatabase.movieDao().insertCastMembers(castMembers)

        val joins = castMembers.mapIndexed { index, castMemberEntity ->
            MovieCastMemberJoin(movies1[index].id, castMemberEntity.id)
        }
        muvicatDatabase.movieDao().insertMovieCastJoins(joins)

        val updatedMovies = MovieEntityFactory.makeMovieEntityList(3)
        updatedMovies.forEachIndexed { index, item -> item.id = index.toLong() }

        val notUpdatedMovies = listOf(movies1[3])
        // val deletedMovies = movies1[4]
        val newMovies = MovieEntityFactory.makeMovieEntityList(3)
        newMovies.forEach { it.id += 10 }
        val movies2 = notUpdatedMovies + updatedMovies + newMovies
        muvicatDatabase.movieDao().updateMovieDb(movies2)

        assertEquals(
            MovieMapper.mapFromMovieEntityList(movies2)
                .sortedWith(compareByDescending<Movie> { it.priority }.thenBy { it.id }),
            muvicatDatabase.movieDao().getMovies().getValueBlocking()
        )

        assertEquals(
            castMembers.subList(0, 4).sortedBy { it.id },
            muvicatDatabase.movieDao().getCastMembers()
        )
        assertEquals(
            joins.subList(0, 4).sortedWith(compareBy<MovieCastMemberJoin>({ it.movieId }).thenBy { it.castMemberId }),
            muvicatDatabase.movieDao().getMovieCastMembersJoin()
        )
    }

    @Test
    fun updateMovieExtraInfoUpdatesDetails() {
        val movie = MovieEntityFactory.makeMovieEntity()
        muvicatDatabase.movieDao().insertMovies(listOf(movie))

        val extraInfo = MovieExtraInfoFactory.makeExtraInfo()

        muvicatDatabase.movieDao().updateMovieExtraInfo(movie.id, extraInfo.tmdbId, extraInfo.runtime,
            StringListTypeConverter().toString(extraInfo.genres), extraInfo.backdropUrl,
            extraInfo.voteAverage, extraInfo.voteCount)

        movie.apply {
            tmdbId = extraInfo.tmdbId
            runtime = extraInfo.runtime
            genres = extraInfo.genres
            backdropUrl = extraInfo.backdropUrl
            voteAverage = extraInfo.voteAverage
            voteCount = extraInfo.voteCount
        }

        assertEquals(
            MovieMapper.mapFromMovieEntity(movie),
            muvicatDatabase.movieDao().getMovie(movie.id).getValueBlocking()
        )
    }

    @Test
    fun addMovieExtraInfoUpdatesDetailsAndAddsCastMembers() {
        val movie = MovieEntityFactory.makeMovieEntity()
        muvicatDatabase.movieDao().insertMovies(listOf(movie))

        val extraInfo = MovieExtraInfoFactory.makeExtraInfo()

        muvicatDatabase.movieDao().addMovieExtraInfo(movie.id, extraInfo)

        movie.apply {
            tmdbId = extraInfo.tmdbId
            runtime = extraInfo.runtime
            genres = extraInfo.genres
            backdropUrl = extraInfo.backdropUrl
            voteAverage = extraInfo.voteAverage
            voteCount = extraInfo.voteCount
        }

        assertEquals(
            MovieMapper.mapFromMovieEntity(movie),
            muvicatDatabase.movieDao().getMovie(movie.id).getValueBlocking()
        )
        assertEquals(
            extraInfo.cast.sortedBy { it.id },
            muvicatDatabase.movieDao().getCastMembers()
        )
        assertEquals(
            extraInfo.cast.map { MovieCastMemberJoin(movie.id, it.id) }
                .sortedWith(compareBy<MovieCastMemberJoin>({ it.movieId }).thenBy { it.castMemberId }),
            muvicatDatabase.movieDao().getMovieCastMembersJoin()
        )
    }

    @Test
    fun deleteOutdatedCastMembersDeletesMembersWithoutAnyMovie() {
        val movies = MovieEntityFactory.makeMovieEntityList(2)
        muvicatDatabase.movieDao().insertMovies(movies)

        val castMembers = CastMemberEntityFactory.makeCastMemberEntityList(4)
        muvicatDatabase.movieDao().insertCastMembers(castMembers)

        val joins = castMembers.mapIndexed { index, castMemberEntity ->
            MovieCastMemberJoin(movies[index/2].id, castMemberEntity.id)
        }
        muvicatDatabase.movieDao().insertMovieCastJoins(joins)

        assertEquals(
            castMembers.sortedBy { it.id },
            muvicatDatabase.movieDao().getCastMembers()
        )
        assertEquals(
            joins.sortedWith(compareBy<MovieCastMemberJoin>({ it.movieId }).thenBy { it.castMemberId }),
            muvicatDatabase.movieDao().getMovieCastMembersJoin()
        )

        muvicatDatabase.movieDao().deleteMoviesById(listOf(movies[0].id))

        assertEquals(
            castMembers.sortedBy { it.id },
            muvicatDatabase.movieDao().getCastMembers()
        )
        assertEquals(
            joins.subList(2, 4).sortedWith(compareBy<MovieCastMemberJoin>({ it.movieId }).thenBy { it.castMemberId }),
            muvicatDatabase.movieDao().getMovieCastMembersJoin()
        )

        muvicatDatabase.movieDao().deleteOutdatedCastMembers()

        assertEquals(
            castMembers.subList(2, 4).sortedBy { it.id },
            muvicatDatabase.movieDao().getCastMembers()
        )
    }

    @Test
    fun getCastMembersByMovieReturnsCast() {
        val movie = MovieEntityFactory.makeMovieEntity()
        muvicatDatabase.movieDao().insertMovies(listOf(movie))

        val extraInfo = MovieExtraInfoFactory.makeExtraInfo()
        muvicatDatabase.movieDao().addMovieExtraInfo(movie.id, extraInfo)


        assertEquals(
            CastMemberMapper.mapFromCastMemberEntityList(extraInfo.cast)
                .sortedWith(compareBy<CastMember> { it.order }.thenBy { it.id }),
            muvicatDatabase.movieDao().getCastMembersByMovie(movie.id)
        )
    }
}
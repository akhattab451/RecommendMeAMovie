package com.example.recommendmeamovie.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.map
import androidx.room.withTransaction
import com.example.recommendmeamovie.paging.MoviePagingSource
import com.example.recommendmeamovie.paging.MovieRemoteMediator
import com.example.recommendmeamovie.repository.interfaces.MovieRepository
import com.example.recommendmeamovie.source.local.database.MovieDatabase
import com.example.recommendmeamovie.source.local.database.asDomain
import com.example.recommendmeamovie.source.remote.asEntity
import com.example.recommendmeamovie.source.remote.dto.MoviesContainer
import com.example.recommendmeamovie.source.remote.service.MovieApiService
import com.example.recommendmeamovie.util.networkBoundResource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalPagingApi
@ViewModelScoped
class MovieRepositoryImpl
@Inject constructor(
    private val service: MovieApiService,
    private val database: MovieDatabase
) : MovieRepository {
    private val movieDao = database.movieDao

    companion object {
        private const val POPULAR_FILTER = "popular"
        private const val TOP_RATED_FILTER = "top_rated"
        private const val WATCHLIST_FILTER = "watchlist"
        private const val FAVORITES_FILTER = "favorite"
    }

    override val popularMoviesPaged = gePagedMovies(
        filter = POPULAR_FILTER,
        maxSize = 60,
        request = {
            service.getMovies(filter = POPULAR_FILTER, page = it)
        }
    )

    override val topRatedMoviesPaged = gePagedMovies(
        filter = TOP_RATED_FILTER,
        maxSize = 60,
        request = {
            service.getMovies(filter = TOP_RATED_FILTER, page = it)
        }
    )

    override fun getPagedSearchResults(query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 60,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            MoviePagingSource(service, query)
        }
    ).flow

    override fun getWatchlistPaged(accountId: Long, sessionId: String) = gePagedMovies(
        filter = WATCHLIST_FILTER,
        maxSize = MAX_SIZE_UNBOUNDED,
        request = {
            service.getAccountMovieList(
                accountId = accountId,
                type = WATCHLIST_FILTER,
                sessionId = sessionId,
                page = it
            )
        }
    )

    override fun getFavoritesPaged(accountId: Long, sessionId: String) = gePagedMovies(
        filter = FAVORITES_FILTER,
        maxSize = MAX_SIZE_UNBOUNDED,
        request = {
            service.getAccountMovieList(
                accountId = accountId,
                type = FAVORITES_FILTER,
                sessionId = sessionId,
                page = it
            )
        }
    )

     override fun getWatchlist(accountId: Long, sessionId: String) = networkBoundResource(
         query = {
                 movieDao.getMovies(WATCHLIST_FILTER).map {
                     it.asDomain()
                 }
         },
         fetch = {
             service.getAccountMovieList(accountId = accountId, sessionId = sessionId, type = WATCHLIST_FILTER)
         },
         saveFetchResult = {
             database.withTransaction {
                 movieDao.deleteAll(WATCHLIST_FILTER)
                 movieDao.insertAll(it.asEntity(WATCHLIST_FILTER))
             }
         }
     )

    override fun getFavorites(accountId: Long, sessionId: String) = networkBoundResource(
        query = {
            movieDao.getMovies(FAVORITES_FILTER).map {
                it.asDomain()
            }
        },
        fetch = {
            service.getAccountMovieList(accountId = accountId, sessionId = sessionId, type = FAVORITES_FILTER)
        },
        saveFetchResult = {
            database.withTransaction {
                movieDao.deleteAll(FAVORITES_FILTER)
                movieDao.insertAll(it.asEntity(FAVORITES_FILTER))
            }
        }
    )

    private fun gePagedMovies(
        filter: String,
        maxSize: Int,
        request: suspend (page: Int) -> MoviesContainer,
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = maxSize,
            enablePlaceholders = false,
        ),
        remoteMediator = MovieRemoteMediator(
            filter = filter,
            request = request,
            database = database
        ),
        pagingSourceFactory = {
            movieDao.getMoviesPaged(filter = filter)
        }
    ).flow.map {
        it.map { entity ->
            entity.asDomain()
        }
    }

    override suspend fun clearCachedMovies() {
        movieDao.deleteAll(WATCHLIST_FILTER)
        movieDao.deleteAll(FAVORITES_FILTER)
    }
}

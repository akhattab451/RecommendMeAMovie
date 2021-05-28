package com.example.recommendmeamovie.repository

import com.example.recommendmeamovie.domain.MovieDetails
import com.example.recommendmeamovie.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieDetailsRepository {

    fun getMovieDetails(id: Long): Flow<Resource<MovieDetails>>

}
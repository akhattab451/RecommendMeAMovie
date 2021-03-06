package com.example.recommendmeamovie.di

import com.example.recommendmeamovie.source.remote.service.AccountApiService
import com.example.recommendmeamovie.source.remote.service.MovieApiService
import com.example.recommendmeamovie.util.Constants.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideMoshi() : Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    fun provideRetrofit(moshi: Moshi) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    fun provideMovieService(retrofit : Retrofit) : MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    fun provideAccountService(retrofit : Retrofit) : AccountApiService {
        return retrofit.create(AccountApiService::class.java)
    }

}
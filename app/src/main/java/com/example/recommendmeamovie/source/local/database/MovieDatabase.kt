package com.example.recommendmeamovie.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recommendmeamovie.source.local.MovieEntity
import com.example.recommendmeamovie.source.local.QuestionEntity
import com.example.recommendmeamovie.source.local.database.dao.MovieDao
import com.example.recommendmeamovie.source.local.database.dao.QuestionDao

@Database(entities = [MovieEntity::class, QuestionEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract val movieDao : MovieDao
    abstract val questionDao : QuestionDao

    companion object {
        const val DATABASE_NAME = "movie-db"
    }

}
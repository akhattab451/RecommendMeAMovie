package com.example.recommendmeamovie.work

import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.recommendmeamovie.domain.Movie
import com.example.recommendmeamovie.util.sendNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject



@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
): Worker(context, workerParameters) {



    override fun doWork(): Result {
        val movie = inputData.let {
            Movie(
                it.getLong("movieId", 0),
                it.getString("movieName").toString(),
                it.getString("moviePoster"),
                ""
            )
        }

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.sendNotification(applicationContext, movie)
        return Result.success()
    }
}
package com.example.recommendmeamovie.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recommendmeamovie.R
import com.example.recommendmeamovie.domain.Movie
import com.example.recommendmeamovie.receiver.WatchedReceiver
import com.example.recommendmeamovie.ui.MainActivity
import com.example.recommendmeamovie.work.NotificationWorker
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

fun NotificationManager.sendNotification(context: Context, movie: Movie) {

    val bundle = Bundle().apply {
            putString("movieName", movie.title)
            putLong("movieId", movie.id)
    }

    // PendingIntent Navigates to movie page when notification is clicked
    val contentIntent = NavDeepLinkBuilder(context)
        .setComponentName(MainActivity::class.java)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.movieFragment)
        .setArguments(bundle)
        .createPendingIntent()

    // Add movie to watched list when action is clicked
    val watchedIntent = Intent(context, WatchedReceiver::class.java)
    val watchedPendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.WATCHED_REQUEST_CODE,
            watchedIntent,
            Constants.FLAGS
        )

    // Add movie to watched list when action is clicked
    val dismissIntent = Intent(context, WatchedReceiver::class.java)
    val dismissPendingIntent = PendingIntent.getBroadcast(
        context,
        Constants.DISMISS_REQUEST_CODE,
        dismissIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Movie Poster as Icon
    val largeIcon: Bitmap = Picasso.get().load(Constants.IMAGE_URL + movie.poster).get()

    val builder = NotificationCompat
        .Builder(context, context.getString(R.string.channel_id))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(context.getString(R.string.notification_title, movie.title))
        .setLargeIcon(largeIcon)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(contentIntent)
        .addAction(R.drawable.ic_checkmark, "Yes", watchedPendingIntent)
        .addAction(R.drawable.ic_dismiss, "No, Leave Me Alone", dismissPendingIntent)
        .setAutoCancel(true)

    notify(Constants.NOTIFICATION_ID, builder.build())
}

fun WorkManager.scheduleNotification(data: Data) {
    val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInputData(data)
        .setInitialDelay(3L /*1L*/, TimeUnit.SECONDS/*TimeUnit.DAYS*/)
        .build()

    enqueue(notificationWorkRequest)
}


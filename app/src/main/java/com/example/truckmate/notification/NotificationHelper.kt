package com.example.truckmate.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.truckmate.R

object NotificationHelper {
    const val NEARBY_CHANNEL_ID = "nearby_objects"

    const val HELP_CHANNEL_ID = "sos_help"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val nearbyChannel = NotificationChannel(
            NEARBY_CHANNEL_ID,
            "Nearby Objects",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        nearbyChannel.description = "Notifications for nearby objects"

        val helpChannel = NotificationChannel(
            HELP_CHANNEL_ID,
            "SOS Help Requests",
            NotificationManager.IMPORTANCE_HIGH
        )

        helpChannel.description = "Emergency help notifications"

        helpChannel.enableVibration(true)

        manager.createNotificationChannel(nearbyChannel)
        manager.createNotificationChannel(helpChannel)
    }

    fun showNearbyNotification(context: Context, title: String, message: String) {
        val notification = NotificationCompat.Builder(context, NEARBY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    fun showHelpNotification(context: Context, title: String, message: String) {
        val notification = NotificationCompat.Builder(context,HELP_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(
                System.currentTimeMillis().toInt(),
                notification
            )
    }
}
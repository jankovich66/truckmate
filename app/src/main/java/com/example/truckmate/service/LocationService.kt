package com.example.truckmate.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.truckmate.data.repository.HelpRepository
import com.example.truckmate.data.repository.ObjectRepository
import com.example.truckmate.utils.LocationHelper
import com.example.truckmate.utils.LocationUtils
import com.example.truckmate.notification.NotificationHelper

class LocationService: Service() {
    private lateinit var locationHelper: LocationHelper
    private val repository = ObjectRepository()
    private val helpRepository = HelpRepository()

    private val notifiedObjects = mutableSetOf<String>()
    private val notifiedHelpRequests = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this)
        createNotificationChannel()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationHelper.startLocationUpdates { lat, lon ->
            checkNearby(lat, lon)
        }

        return START_STICKY
    }

    private fun checkNearby(lat: Double, lon: Double) {
        repository.getObjectsRealtime { objects ->
            objects.forEach { obj ->
                val distance = LocationUtils.distanceInMeters(lat, lon, obj.latitude, obj.longitude)

                if(distance < 200 && !notifiedObjects.contains(obj.id)) {
                    notifiedObjects.add(obj.id)
//                    sendNotification(obj.title)
                    NotificationHelper.showNearbyNotification(context = this, title = "Nearby object", message = obj.title)
                }
            }
        }
        helpRepository.listenForHelpRequests { helps ->
            helps.forEach { help ->
                val distance = LocationUtils.distanceInMeters(lat, lon, help.latitude, help.longitude)

                if(distance < 5000 && !notifiedHelpRequests.contains(help.id)) {
                    notifiedHelpRequests.add(help.id)
//                    sendNotification(title = "Driver nearby needs help")
                    NotificationHelper.showHelpNotification(this, "Driver nearby needs help", help.type.name)
                }
            }
        }
    }

    private fun sendNotification(title: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Nearby object")
            .setContentText(title)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .build()

        manager.notify(title.hashCode(), notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("location_channel", "Location notifications",NotificationManager.IMPORTANCE_HIGH)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Tracking location")
            .setContentText("App is tracking your location")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?) = null
}
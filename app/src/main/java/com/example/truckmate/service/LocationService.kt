package com.example.truckmate.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat
import com.example.truckmate.data.model.HelpRequest
import com.example.truckmate.data.model.LocationObject
import com.example.truckmate.data.repository.HelpRepository
import com.example.truckmate.data.repository.ObjectRepository
import com.example.truckmate.utils.LocationHelper
import com.example.truckmate.utils.LocationUtils
import com.example.truckmate.notification.NotificationHelper

class LocationService: Service() {
    private lateinit var locationHelper: LocationHelper
    private val repository = ObjectRepository()
    private val helpRepository = HelpRepository()

    private var currentObjects = listOf<LocationObject>()
    private var currentHelpRequests = listOf<HelpRequest>()

    private val notifiedObjects = mutableSetOf<String>()
    private val notifiedHelpRequests = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this)
        //NotificationHelper.createChannels(this)

        startForegroundService()

        observeObjects()
        observeHelpRequests()
//        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationHelper.startLocationUpdates { lat, lon ->
            checkNearby(lat, lon)
        }

        return START_STICKY
    }

    private fun checkNearby(lat: Double, lon: Double) {
        currentObjects.forEach { obj ->
//                val distance = LocationUtils.distanceInMeters(lat, lon, obj.latitude, obj.longitude)
            val distance = FloatArray(1)

            Location.distanceBetween(
                lat,
                lon,
                obj.latitude,
                obj.longitude,
                distance
            )
            if(distance[0] < 200 && !notifiedObjects.contains(obj.id)) {
                notifiedObjects.add(obj.id)
//                    sendNotification(obj.title)
                NotificationHelper.showNearbyNotification(context = this, title = "Nearby object", message = obj.title)
            }
        }
        currentHelpRequests.forEach { help ->
//            val distance = LocationUtils.distanceInMeters(lat, lon, help.latitude, help.longitude)
            val distance = FloatArray(1)

            Location.distanceBetween(
                lat,
                lon,
                help.latitude,
                help.longitude,
                distance
            )

            if(distance[0] < 5000 && !notifiedHelpRequests.contains(help.id)) {
                notifiedHelpRequests.add(help.id)
//                    sendNotification(title = "Driver nearby needs help")
                NotificationHelper.showHelpNotification(this, "Driver nearby needs help", help.type.name)
            }
        }
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

    private fun observeObjects() {
        repository.getObjectsRealtime { objects ->
            currentObjects = objects
        }
    }

    private fun observeHelpRequests() {
        helpRepository.listenForHelpRequests { helps ->
            currentHelpRequests = helps
        }
    }
}
package com.example.locationupdates

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.locationupdates.room.Location
import com.example.locationupdates.room.LocationDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService : Service() {
    private lateinit var context: Context
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var location: LocationDao

    override fun onCreate() {
        super.onCreate()
        context = this
        initData()
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation = locationResult.lastLocation
            if (currentLocation != null) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude
            }
            startForeground(1, createNotification())
            Log.d(
                "Locationss",
                currentLocation?.latitude.toString() + "," + currentLocation?.longitude
            )
            val locationCoords = Location(
                latitude = currentLocation?.latitude,
                longitude = currentLocation?.longitude
            )
            location.addLocation(locationCoords)
            Log.d(
                "Locationss",
                "locCOORDS - $locationCoords"
            )
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            startLocationUpdates()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "NOTIFICATION_ID"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                notificationChannelId,
                "Location Service Notification",
                NotificationManager.IMPORTANCE_HIGH

            )
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("Location Updates")
            .setContentText("$latitude - $longitude")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        startLocationUpdates()
    }

    private fun initData() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_IN_MILLISECONDS
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(UPDATE_INTERVAL_IN_MILLISECONDS)
            .build()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)
    }

    companion object {
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 60000
    }
}
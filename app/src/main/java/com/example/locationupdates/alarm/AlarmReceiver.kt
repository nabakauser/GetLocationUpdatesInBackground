package com.example.locationupdates.alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AlarmReceiver: BroadcastReceiver () {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val lat = intent.getDoubleExtra("KEY_LAT", 0.0)
        val lng = intent.getDoubleExtra("KEY_LNG", 0.0)
        Log.d("ALARMM", "BR-2: $lat - $lng")

    }
}
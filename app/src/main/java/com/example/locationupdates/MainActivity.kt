package com.example.locationupdates

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.locationupdates.alarm.AlarmReceiver
import com.example.locationupdates.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
            alertPopUp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermissionLauncher
        setUpListeners()

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
//        val permissions = arrayOf(
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            permissions.plus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        }
//        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    private fun setUpListeners() {
        binding.uiBtnGetLocation.setOnClickListener {
            requestPermissions()
            val serviceIntent = Intent(this, LocationService::class.java)
            startService(serviceIntent)
        }
    }

    @SuppressLint("SetTextI18n", "MissingPermission")
    private fun getLocation() {
        requestPermissions()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                latitude = location.latitude
                longitude = location.longitude
                Log.d("ALARMM", " MA: $latitude - $longitude");
                binding.uiTvLatitude.text = "Latitude: ${location.latitude}"
                binding.uiTvLongitude.text = "Longitude: ${location.longitude}"
            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "Failed on getting current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    @SuppressLint("MissingPermission")
    private fun setAlarmManager(time: Long) {
        requestPermissions()
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java).apply {
            putExtra("KEY_LAT", latitude)
            putExtra("KEY_LNG", longitude)
        }

        val intervalMillis = 5 * 1000

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                latitude = location.latitude
                longitude = location.longitude

                intent.putExtra("KEY_LAT", latitude)
                intent.putExtra("KEY_LNG", longitude)
                Log.d("Alarmmm - Main Activity", "lat:$latitude - long:$longitude")
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    time.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val triggerTime = System.currentTimeMillis() + intervalMillis

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    intervalMillis.toLong(),
                    pendingIntent
                )

                Toast.makeText(this, "$time: $latitude , $longitude", Toast.LENGTH_SHORT).show()
                Log.d("ALARMM", "${System.currentTimeMillis()}: $latitude , $longitude")
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed on getting current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun alertPopUp() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("User must provide Background Location Permission for updating")
        builder.setPositiveButton("YES") { dialog, which ->
            Toast.makeText(
                this,
                "YES", Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        builder.setNegativeButton("NO") { dialog, which ->
            dialog.dismiss()
            Toast.makeText(
                this,
                "NO", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }
}







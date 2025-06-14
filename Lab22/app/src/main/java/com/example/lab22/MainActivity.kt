package com.example.lab22

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvDistance: TextView
    private lateinit var btnNewPoint: Button
    private lateinit var btnSettings: Button

    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    private var targetLat: Double? = null
    private var targetLng: Double? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationUpdates()
        } else {
            tvStatus.text = "Нет доступа к геолокации!"
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvStatus = findViewById(R.id.tvStatus)
        tvDistance = findViewById(R.id.tvDistance)
        btnNewPoint = findViewById(R.id.btnNewPoint)
        btnSettings = findViewById(R.id.btnSettings)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnNewPoint.setOnClickListener {
            requestLocationAndGeneratePoint()
        }

        btnSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        if (hasLocationPermission()) {
            startLocationUpdates()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationAndGeneratePoint() {
        if (!hasLocationPermission()) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        val providers = locationManager.getProviders(true)
        var lastLocation: Location? = null
        for (provider in providers) {
            val loc = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            else
                locationManager.getLastKnownLocation(provider)
            if (loc != null && (lastLocation == null || loc.accuracy < lastLocation.accuracy)) {
                lastLocation = loc
            }
        }
        if (lastLocation != null) {
            generateRandomPoint(lastLocation.latitude, lastLocation.longitude)
            updateStatus(false)
        }
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) return

        // Снимаем предыдущего слушателя, если он был
        locationListener?.let { locationManager.removeUpdates(it) }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (targetLat == null || targetLng == null) {
                    generateRandomPoint(location.latitude, location.longitude)
                    updateStatus(false)
                }
                val dist = distance(
                    location.latitude, location.longitude,
                    targetLat ?: 0.0, targetLng ?: 0.0
                )
                tvDistance.text = "Текущее расстояние до точки: ${dist.toInt()} м"

                if (dist <= 100) {
                    updateStatus(true)
                } else {
                    updateStatus(false)
                }
            }
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Можно использовать либо GPS_PROVIDER, либо NETWORK_PROVIDER, или оба для лучшей точности
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L,
                1f,
                locationListener as LocationListener
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                2000L,
                1f,
                locationListener as LocationListener
            )
        } catch (e: SecurityException) {
            // Игнорируем, разрешения нет
        }
    }

    private fun updateStatus(found: Boolean) {
        if (found) {
            tvStatus.text = "Ура, точка найдена!"
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            tvStatus.text = "Точка загадана, ищите!"
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        }
    }

    // Генерация случайной точки в радиусе 2.5 км (~0.02 градуса)
    private fun generateRandomPoint(lat: Double, lng: Double) {
        val radiusInDegrees = 0.02 // 2.5 км примерно
        val u = Math.random()
        val v = Math.random()
        val w = radiusInDegrees * sqrt(u)
        val t = 2 * Math.PI * v
        val deltaLat = w * cos(t)
        val deltaLng = w * sin(t) / cos(Math.toRadians(lat))
        targetLat = lat + deltaLat
        targetLng = lng + deltaLng
    }

    // Формула для вычисления расстояния между двумя точками
    private fun distance(
        latA: Double, lngA: Double, latB: Double, lngB: Double
    ): Double {
        val d = 6371000.0
        val phiA = Math.toRadians(latA)
        val phiB = Math.toRadians(latB)
        val lambdaA = Math.toRadians(lngA)
        val lambdaB = Math.toRadians(lngB)
        return d * acos(
            sin(phiA) * sin(phiB) +
                    cos(phiA) * cos(phiB) * cos(lambdaA - lambdaB)
        )
    }

    override fun onDestroy() {
        locationListener?.let { locationManager.removeUpdates(it) }
        super.onDestroy()
    }
}
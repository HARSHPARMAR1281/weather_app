package com.example.weather_app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            if (hasLocationPermission()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } else {
                continuation.resume(null)
            }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }

    fun isLocationServiceEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            Constants.DEFAULT_LOCATION_UPDATE_INTERVAL
        )
            .setMinUpdateIntervalMillis(Constants.DEFAULT_LOCATION_FASTEST_INTERVAL)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    trySend(location)
                }
            }
        }

        if (!hasLocationPermission() || !isLocationServiceEnabled()) {
            close(IllegalStateException("Location permissions not granted or services not enabled"))
            return@callbackFlow
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e) // Close the flow with the security exception
            return@callbackFlow
        }

        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            if (hasLocationPermission()) {
                if (isLocationServiceEnabled()) {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { location ->
                        continuation.resume(location)
                    }.addOnFailureListener { e ->
                        // Log the failure reason if needed, e.e., Log.e("LocationHelper", "getCurrentLocation failed", e)
                        continuation.resume(null) // Resume with null on failure
                    }
                } else {
                    // Location services are disabled
                    // Optionally, you could launch an intent to open location settings here
                    // val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    // context.startActivity(intent)
                    continuation.resume(null)
                }
            } else {
                // Permission not granted
                continuation.resume(null)
            }
        } catch (e: SecurityException) {
            // Handle SecurityException (e.g., permission revoked during the call)
            continuation.resume(null)
        } catch (e: Exception) {
            // Catch any other unexpected exceptions
            // Log.e("LocationHelper", "Unexpected error in getCurrentLocation", e)
            continuation.resume(null)
        }
    }
} 
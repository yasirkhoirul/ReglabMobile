package com.example.reglab7firebase.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.reglab7firebase.data.CampusConstants
import com.example.reglab7firebase.data.model.CampusLocation
import com.example.reglab7firebase.data.model.LocationRepo
import com.example.reglab7firebase.view.component.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LocationRepoImpl(private val context: Context) : LocationRepo {
    private val firestore = Firebase.firestore
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun watchingLocationRealtime(data: GeoPoint): Flow<com.example.reglab7firebase.data.model.LocationResult> =
        callbackFlow {
            Log.d("titikpoin", "$data")
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close(IllegalStateException("Layanan Lokasi (GPS) tidak aktif."))
                return@callbackFlow
            }
            val campusLocation = Location("CampusCenter").apply {
                latitude = data.latitude
                longitude = data.longitude
            }
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000
            ).build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d("lokasinya", "sukses")
                    Log.d("titikpoin", "$campusLocation")
                    locationResult.lastLocation?.let {
                        trySend(
                            com.example.reglab7firebase.data.model.LocationResult.Success(
                                location = it,
                                distanceToCampus = it.distanceTo(campusLocation)
                            )
                        )
                    }

                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        Log.d("lokasinya", "dimatikan")
                        trySend(com.example.reglab7firebase.data.model.LocationResult.LocationUnavailable)
                    } else {
                        trySend(com.example.reglab7firebase.data.model.LocationResult.Loading)
                        Log.d("lokasinya", "dihidupkan ")
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    override suspend fun upLocation(data: GeoPoint){
        try {
            firestore.collection("Lokasi").document("thisloc").update("CampusLoc",data).await()
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun getLocation(): CampusLocation? {
        try {
            val doc = firestore.collection("Lokasi").document("thisloc").get().await()
            Log.d("lokasi","getlocation ${doc.toObject<CampusLocation>()} ")
            return doc.toObject<CampusLocation>()
        }catch (e: Exception){
            throw e
        }
    }
}
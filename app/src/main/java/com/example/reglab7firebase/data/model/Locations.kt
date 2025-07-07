package com.example.reglab7firebase.data.model

import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow

data class CampusLocation(
    val CampusLoc : GeoPoint = GeoPoint(-7.823601713107916,110.3763627765288)
)

interface LocationRepo {
    fun watchingLocationRealtime(data: GeoPoint): Flow<LocationResult>
    suspend fun upLocation(data: GeoPoint)
    suspend fun getLocation(): CampusLocation?
}
interface WifiRepo {
    fun observeWifiSsid(): Flow<String?>
}


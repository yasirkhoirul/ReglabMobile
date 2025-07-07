package com.example.reglab7firebase.view.dashboardAdmin.titikMap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.CampusLocation
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.LocationRepo
import com.example.reglab7firebase.data.model.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PilihMapViewModel( private val lokasiRepo : LocationRepo): ViewModel() {
    private val finelocation = MutableStateFlow(false)
    val uifinelocation = finelocation.asStateFlow()
    private val coarseLocation = MutableStateFlow(false)
    val uiCoarseLocation = coarseLocation.asStateFlow()
    private val lokasi = MutableStateFlow<LocationResult>(LocationResult.LocationUnavailable)
    val uiLokasi = lokasi.asStateFlow()
    private val setLokasi = MutableStateFlow<CampusLocation>(CampusLocation())
    val uiSetLokasi = setLokasi.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    fun getPermessionFineLocation(result: Boolean){
        finelocation.value = result
    }
    fun getPermessionCoarseLocation(result: Boolean){
        coarseLocation.value = result
    }
    fun setLocation(data: LatLng){
        setLokasi.update{
            it.copy(GeoPoint(data.latitude,data.longitude))
        }
    }
    fun onIdle(){
        status.value = Cek.idle
    }
    fun handlingClikLocation(){
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                Log.d("lokasi di up",setLokasi.value.CampusLoc.toString())
                lokasiRepo.upLocation(setLokasi.value.CampusLoc)
                status.value = Cek.Sukses
            }catch (e: Exception){
                status.value = Cek.Error("Gagal melakukan set lokasi")
            }
        }
    }
}
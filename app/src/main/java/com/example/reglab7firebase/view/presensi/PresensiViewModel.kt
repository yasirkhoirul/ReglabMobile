package com.example.reglab7firebase.view.presensi

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.CampusLocation
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.LocationRepo
import com.example.reglab7firebase.data.model.LocationResult
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.model.WifiRepo
import com.example.reglab7firebase.data.repository.UserRepo
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PresensiViewModel(
    private val timerepo: TimeRepo,
    private val userRepo: UserRepo,
    private val praktikum: PraktikumRepo,
    private val lokasiRepo: LocationRepo,
    private val wifiRepo: WifiRepo,
) : ViewModel() {

    private val cameraPermession = MutableStateFlow(false)
    val uiCameraPermession = cameraPermession.asStateFlow()
    private val finelocation = MutableStateFlow(false)
    val uifinelocation = finelocation.asStateFlow()
    private val coarseLocation = MutableStateFlow(false)
    val uiCoarseLocation = coarseLocation.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    private val lokasi = MutableStateFlow<LocationResult>(LocationResult.LocationUnavailable)
    val uiLokasi = lokasi.asStateFlow()
    private val wifi = MutableStateFlow<String?>(null)
    val uiwifi = wifi.asStateFlow()
    private val waktu = MutableStateFlow<Timestamp?>(null)
    val uiwaktu = waktu.asStateFlow()
    private val result = MutableStateFlow<List<String>>(emptyList())
    val uiresult = result.asStateFlow()
    private val lokasiPoint = MutableStateFlow<CampusLocation?>(CampusLocation())
    val pointLoc = lokasiPoint.asStateFlow()

    init {
        getPointLocation()
    }

    fun getPointLocation() {
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                val data = lokasiRepo.getLocation()
                lokasiPoint.value = data
                status.value = Cek.idle
            } catch (e: Exception) {
                status.value = Cek.Error("Gagal mendapatkan titik lokasi")
            }
        }
    }

    fun getPermessionCam(result: Boolean) {
        cameraPermession.value = result
    }

    fun getPermessionFineLocation(result: Boolean) {
        finelocation.value = result
    }

    fun getPermessionCoarseLocation(result: Boolean) {
        coarseLocation.value = result
    }

    fun observeLocation() {
        viewModelScope.launch {
            lokasiRepo.watchingLocationRealtime(
                lokasiPoint.value?.CampusLoc ?: CampusLocation().CampusLoc
            ).onStart {
            }.catch { e ->
                lokasi.value = LocationResult.Error(message = e.message.toString())
            }.collect {
                lokasi.value = it
            }
        }
    }

    fun observeWifiChanges() {
        viewModelScope.launch {
            wifiRepo.observeWifiSsid().onStart {
                emit("mencari jaringan..")
            }.collect { newSsid ->
                wifi.value = newSsid
            }
        }
    }

    fun validasiPresensi(result: String) {
        if (status.value !is Cek.idle) {
            return
        }
        status.value = Cek.Loading
        if (userRepo.cekUser()) {
            if (result.isEmpty()) {
                status.value = Cek.Error(message = "Maaf gagal melakukan Scan")
            } else {
                viewModelScope.launch {
                    val cek = praktikum.cekMahasiswa(userRepo.cekCurent())
                    if (cek.isEmpty()) {
                        status.value = Cek.Error("Anda bukan Praktikan Ini")
                        return@launch
                    }
                    val serverTimestamp = timerepo.fetchServerTimeViaDummyDoc(userRepo.cekCurent())
                    val timeNow = serverTimestamp.toDate().time
                    val parts = result.split("/")
                    if (parts.size<2){
                        status.value = Cek.Error("Ini bukan QR Code presensi")
                        return@launch
                    }
                    val batas = parts[0].toLong()
                    val idPrak = parts[1]
                    val idPertemuan = parts.last()
                    if (timeNow <= batas) {
                        try {
                            praktikum.addPresensiUser(
                                idPrak = idPrak,
                                idPertemuan = idPertemuan,
                                idMahasiswa = userRepo.cekCurent()
                            )
                            status.value = Cek.Sukses
                        } catch (e: Exception) {
                            status.value = Cek.Error(message = e.message.toString())
                        }
                    } else {
                        status.value = Cek.Error(message = "Maaf Qr code sudah tidak valid")
                    }
                }
            }
        } else {
            status.value = Cek.Error(message = "Anda belum login")
        }
    }
    fun getIle() {
        status.value = Cek.idle
    }

}
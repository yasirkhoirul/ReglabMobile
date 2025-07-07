package com.example.reglab7firebase.view.generateQrCode

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.QrCodeGenerator
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QRCodeViewModel(
    private val userRepo: UserRepo,
    private val praktikumrepo: PraktikumRepo,
    private val timerepo: TimeRepo,
    private val qrCodeGenerator: QrCodeGenerator,
) : ViewModel() {

    private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap = _qrCodeBitmap.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    private val waktu = MutableStateFlow<Timestamp?>(null)
    val uiwaktu = waktu.asStateFlow()
    private val stateLogin = MutableStateFlow<User?>(User())
    var uiloginState: StateFlow<User?> = stateLogin.asStateFlow()
    private val detailPertemuanState = MutableStateFlow<Pair<String, String>?>(null)

    init {
        cekRole()
    }

    fun cekRole() {
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                val data = userRepo.getUser(userRepo.cekCurent())
                stateLogin.value = data
                status.value = Cek.idle
            } catch (e: Exception) {
                status.value = Cek.idle
            }
        }
    }

    val uidetailPertemuan = detailPertemuanState.filterNotNull().flatMapLatest {
        praktikumrepo.getOnePertemuanPrak(it.first, it.second)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun generateCurrentUserQrCode(data: String) {
        viewModelScope.launch {
            if (!data.isEmpty()) {
                _qrCodeBitmap.value = qrCodeGenerator.generate(data)
            }
        }
    }

    fun getDetailPertemuan(idPrak: String, idPertemuan: String) {
        detailPertemuanState.value = Pair(idPrak, idPertemuan)
    }

    fun onGetServerTimeClicked(
        idPrak: String,
        idPertemuan: String,
        waktuprak: Timestamp?,
        isAdmin: Boolean,
    ) {
        if (waktuprak == null) {
            status.value = Cek.Error("Gagal")
            return
        }
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                val serverTimestamp = timerepo.fetchServerTimeViaDummyDoc(userRepo.cekCurent())
                val serverTimeMillis = serverTimestamp
                waktu.value = serverTimeMillis
                if (!isAdmin) {
                    if (serverTimeMillis.toDate().time !in (waktuprak.toDate().time..waktuprak.toDate().time.plus(
                            86400000
                        ))
                    ) {
                        status.value =
                            Cek.Error("Maaf sudah tidak bisa presensi atau belum waktunya")
                        return@launch
                    }
                }

                generateCurrentUserQrCode(
                    serverTimeMillis.toDate().time.plus(900000).toString() + "/$idPrak/$idPertemuan"
                )
                // Atur status sukses jika perlu
                status.value = Cek.Sukses
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.toString())
            }
        }
    }

    fun getIdle() {
        status.value = Cek.idle
    }

}
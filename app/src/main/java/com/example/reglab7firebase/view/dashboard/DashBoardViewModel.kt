package com.example.reglab7firebase.view.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.InfoRepo
import com.example.reglab7firebase.data.model.Isi
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashBoardViewModel(
    private val informasi: InfoRepo,
    private val getuser: UserRepo,
    private val praktikum: PraktikumRepo,
) : ViewModel() {


    private val profilestate = MutableStateFlow<User?>(User())
    val uiprofie = profilestate.asStateFlow()
    private val navigatelogin = MutableSharedFlow<Unit>()
    val uinavigatelogin = navigatelogin.asSharedFlow()
    private val informasistate = MutableStateFlow<List<Isi>>(listOf(Isi()))
    val uisinformasistate = informasistate.asStateFlow()
    private val jumlahPraktikum = MutableStateFlow<List<Praktikum>>(emptyList())
    val uiJumlahPraktikum = jumlahPraktikum.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uiStatus = status.asStateFlow()

    fun Logout() {
        getuser.Logout()
    }

    init {
        getUser()
        getInformation()
        getJumlahPrak()
    }

    fun getInformation() {
        viewModelScope.launch {
            informasi.getInformation().collect {
                Log.d("lihatisiInfo", it.toString())
                informasistate.value = it
            }
        }
    }

    fun getJumlahPrak() {
        viewModelScope.launch {
            status.value = Cek.Loading
            if (getuser.cekUser()) {
                try {
                    val prak = praktikum.getAllPrakForUser(getuser.cekCurent())
                    jumlahPraktikum.value = prak
                    status.value = Cek.Sukses
                } catch (e: Exception) {
                    status.value = Cek.Error(message = e.message.toString())
                    throw e
                }
            } else {
                status.value = Cek.Error(message = "anda belum login")
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            if (getuser.cekUser()) {
                Log.d("profiilenya", "ada usernya")
                try {
                    val profile = getuser.getUser(getuser.cekCurent())
                    Log.d("profiilenya", profile.toString())
                    profilestate.value = profile
                } catch (e: Exception) {
                    throw e
                }
            } else {
                Log.d("profiilenya", "ada usernya")
                navigatelogin.emit(Unit)
            }

        }
    }

}
package com.example.reglab7firebase.view.praktikumAsprak

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PraktikumAsprakViewModel(
    private val userrepo: UserRepo,
    private val praktikumRepo: PraktikumRepo,
) : ViewModel() {

    private val praktikum = MutableStateFlow<List<Praktikum>>(emptyList())
    val uipraktikum = praktikum.asStateFlow()
    private val praktikumAdmin = MutableStateFlow<List<Praktikum>>(emptyList())
    val uipraktikumAdmin = praktikumAdmin.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()

    init {
        if (userrepo.cekUser()) {
            getPrakAsprak(uid = userrepo.cekCurent())
        }
    }

    fun statusLoading() {
        status.value = Cek.Loading
    }

    fun getPrakAsprak(uid: String) {
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                praktikum.value = praktikumRepo.getAllPrakForAsprak(uid)
                status.value = Cek.Sukses
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.message.toString())
            }
        }
    }

    fun getPrakAdmin() {
        Log.d("apakah admin23", "dijalankan")
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                praktikumRepo.getAllPrakAdminCall().collect {
                    praktikumAdmin.value = it
                    Log.d("apakah admin23", "adalah hasil $it")
                    status.value = Cek.Sukses
                }
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.message.toString())
            }
        }
    }
}
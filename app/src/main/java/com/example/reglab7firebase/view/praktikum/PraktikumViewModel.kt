package com.example.reglab7firebase.view.praktikum

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

class PraktikumViewModel(
    private val prakRepo: PraktikumRepo,
    private val user: UserRepo,
): ViewModel() {

    private val cek = MutableStateFlow<Cek>(Cek.idle)
    val uicek = cek.asStateFlow()
    private val praktikumUserState = MutableStateFlow<List<Praktikum>>(emptyList())
    val uiPraktikumUserState = praktikumUserState.asStateFlow()
    init {
        getAllPrakForUser()
    }
    fun getAllPrakForUser(){
        viewModelScope.launch {
            cek.value = Cek.Loading
            if (user.cekUser()){
                try {
                    val uid = user.cekCurent()
                    val hasil = prakRepo.getAllPrakForUser(uid)
                    praktikumUserState.value = hasil
                    Log.d("hasilprak",hasil.toString())
                    cek.value = Cek.Sukses
                }catch (e: Exception){
                    cek.value = Cek.Error(message = e.message.toString())
                    throw e
                }
            }else{
                cek.value = Cek.Error(message = "anda belum login")
            }

        }
    }
}
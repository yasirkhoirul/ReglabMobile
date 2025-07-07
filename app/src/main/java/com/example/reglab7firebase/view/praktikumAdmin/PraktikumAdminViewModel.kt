package com.example.reglab7firebase.view.praktikumAdmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PraktikumAdminViewModel(private val praktikumRepo : PraktikumRepo): ViewModel() {

    private val listStatePraktikum = MutableStateFlow<List<Praktikum>>(emptyList())
    val uilistPraktikum = listStatePraktikum.asStateFlow()
    private val statusCek = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = statusCek.asStateFlow()
    fun Down(){
        statusCek.value = Cek.idle
    }
    init {
        getPrak()
    }
    fun getPrak(){
        viewModelScope.launch {
            statusCek.value = Cek.Loading
            try {
                val cek = praktikumRepo.getAllPrakAdminCall()
                cek.catch {
                    statusCek.value = Cek.Error(message = it.message.toString())
                }.collect {
                    listStatePraktikum.value = it
                    statusCek.value = Cek.Sukses

                }

            }catch (
                e: Exception
            ){
                statusCek.value = Cek.Error(message = "Terjadi kesalahan coba lagi")
                throw e
            }
        }
    }
    fun getDeletePrak(uid: String){
        if (uid.isEmpty()){
            statusCek.value = Cek.Error(message = "Terjadi Kesalahan Coba lagi")
        }else{
            viewModelScope.launch {
                statusCek.value = Cek.Loading
                try {
                    praktikumRepo.deletePrak(uid)
                    statusCek.value = Cek.Sukses
                }catch (e: Exception){
                    statusCek.value = Cek.Error(message = "gagal melakukan penghapusan praktikum")
                }
            }
        }
    }
}
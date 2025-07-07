package com.example.reglab7firebase.view.dashboardAdmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.InfoRepo
import com.example.reglab7firebase.data.model.Isi
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashBoardAdminViewModel(private val user : UserRepo,private val informasi : InfoRepo): ViewModel() {

    private val userState = MutableStateFlow<User?>(User())
    val uiUser = userState.asStateFlow()
    private val triggerOut = MutableSharedFlow<Unit>()
    val  uiTriggerOut = triggerOut.asSharedFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    private val informasistate = MutableStateFlow<List<Isi>>(emptyList())
    val uiinformasi = informasistate.asStateFlow()
    private val open = MutableStateFlow<Boolean>(false)
    val uiopen = open.asStateFlow()
    private val dataUpdates = MutableStateFlow<Isi>(Isi())
    val uiUpdate = dataUpdates.asStateFlow()
    init {
        getUser()
        getInformation()
    }
    fun judulUpdate(judul: String){
        dataUpdates.update {
            it.copy(judul = judul)
        }
    }
    fun isilUpdate(isi: String){
        dataUpdates.update {
            it.copy(isi = isi)
        }
    }
    fun openDialog() {
        open.value = true
    }
    fun closDialog() {
        open.value = false
    }
    fun getUser(){
        status.value = Cek.Loading
        viewModelScope.launch {
            if (user.cekUser()){
                try {
                    val hasil = user.getUser(user.cekCurent())
                    userState.value = hasil
                    status.value = Cek.Sukses
                }catch (e: Exception){
                    status.value = Cek.Error(message = "Gagal menadapatkan informasi user")
                    throw e
                }
            }else{
                triggerOut.emit(Unit)
            }
        }
    }
    fun getInformation(){
        viewModelScope.launch {
            informasi.getInformation().collect {
                Log.d("lihatisiInfo",it.toString())
                informasistate.value = it
            }
        }
    }
    fun tambahinformasi(){
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                informasi.addInformation(uiUpdate.value)
                status.value = Cek.Sukses
            }catch (e: Exception){
                status.value = Cek.Error(message = "Gagal Upload")
            }
        }
    }
    fun hapusInfor(id: String){
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                informasi.deleteInformation(id)
                status.value = Cek.Sukses
            }catch (e: Exception){
                status.value = Cek.Error(message = "Gagal Hapus")
            }
        }
    }

}
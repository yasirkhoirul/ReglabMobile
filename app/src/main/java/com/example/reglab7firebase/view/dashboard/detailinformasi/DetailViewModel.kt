package com.example.reglab7firebase.view.dashboard.detailinformasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.InfoRepo
import com.example.reglab7firebase.data.model.Isi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(val info : InfoRepo): ViewModel() {

    private val status = MutableStateFlow<Cek>(Cek.Loading)
    val uistatus = status.asStateFlow()
    private val uidState = MutableStateFlow<Isi?>(Isi())
    val uIuidstate = uidState.asStateFlow()

    fun Getdetail(uid: String){
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                val hasil = info.getDetailInformation(uid)
                uidState.value = hasil
                status.value = Cek.Sukses
            }catch (e: Exception){
                throw e
            }
        }
    }
}
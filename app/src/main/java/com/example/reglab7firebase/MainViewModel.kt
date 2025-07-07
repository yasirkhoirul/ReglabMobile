package com.example.reglab7firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val user : UserRepo): ViewModel() {
    private val startdestination = MutableStateFlow<String?>(null)
    val uistartDest: StateFlow<String?> = startdestination.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.Loading)
    val uistatus = status.asStateFlow()
    init {
        if (user.cekUser()){
            getUser()
        }else{
            startdestination.value = null
            status.value = Cek.Sukses
        }
    }
        fun getUser(){
            status.value = Cek.Loading
            viewModelScope.launch {
                try {
                    val data = user.getUser(user.cekCurent())
                    if (data?.role =="admin"){
                        startdestination.value = "admin"
                    }else{
                        startdestination.value = "mahasiswa"
                    }
                    status.value = Cek.Sukses
                }catch (e: Exception){
                    throw e
                }
            }
        }
}

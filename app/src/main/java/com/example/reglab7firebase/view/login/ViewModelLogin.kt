package com.example.reglab7firebase.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.EmailValidator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelLogin(private val userRepo : UserRepo, private val emailValidator: EmailValidator,private val dispatcher: CoroutineDispatcher = Dispatchers.Main): ViewModel() {
    private val stateLogin = MutableStateFlow<User>(User())
    var uiState: StateFlow<User> = stateLogin.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus: StateFlow<Cek> = status.asStateFlow()
    private val isAdmin = MutableStateFlow(false)
    val uiIsAdmin = isAdmin.asStateFlow()

    fun UpdateName(nama: String){
        stateLogin.update {
            it.copy(email = nama)
        }
    }
    fun UpdatePassword(password: String){
        stateLogin.update {
            it.copy(password = password)
        }
    }
    fun onclickHandling(){
        status.value = Cek.Loading
        if (stateLogin.value.email.isEmpty()||stateLogin.value.password.isEmpty()) {
            status.value = Cek.Error(message = "password atau email tidak boleh kososng")
            return
        }
        if (!emailValidator.isValid(stateLogin.value.email)) {
            status.value = Cek.Error(message = "email tidak valid")
            return
        }
        viewModelScope.launch {
            try {
                val hasil = userRepo.login(stateLogin.value.email,stateLogin.value.password)
                val hasilRole = userRepo.getUser(hasil.uid)
                if (hasilRole?.role =="admin"){
                    isAdmin.value = true
                }
                status.value = Cek.Sukses
            }
            catch(e: Exception){
                status.value = Cek.Error(message = "Gagal melakukan login cek kembali email dan password")
            }
        }
    }
    fun Down(){
        status.value = Cek.idle
    }
}
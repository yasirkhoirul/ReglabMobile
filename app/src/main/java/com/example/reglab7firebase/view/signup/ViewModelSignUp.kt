package com.example.reglab7firebase.view.signup

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.SignUp
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.EmailValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelSignUp (private val auth : UserRepo, private val emailValidator: EmailValidator): ViewModel() {
    private val signupstate = MutableStateFlow(SignUp())
    val signupstateui: StateFlow<SignUp> = signupstate.asStateFlow()
    private val ceksignup = MutableStateFlow<Cek>(Cek.idle)
    val stateceksignup: StateFlow<Cek> = ceksignup.asStateFlow()

    fun Update(email: String) {
        signupstate.update {
            it.copy(email = email)
        }
    }

    fun UpdatePas(password: String) {
        signupstate.update {
            it.copy(password = password)
        }
    }

    fun UpdaterePas(repassword: String) {
        signupstate.update {
            it.copy(repassword = repassword)
        }
    }

    fun getNim(nim: String) {
        signupstate.update {
            it.copy(nim = nim)
        }
    }

    fun onClickHandling() {
        val cek = signupstate.value
        ceksignup.value = Cek.idle
        if (cek.email.isBlank() || cek.password.isBlank() || cek.repassword.isBlank()) {
            ceksignup.value = Cek.Error(message = "kolom email atau password tidak boleh kosong")
        } else if (!emailValidator.isValid(cek.email)) {
            ceksignup.value = Cek.Error(message = "Format email tidak benar")
        } else if (cek.nim.isEmpty()) {
            ceksignup.value = Cek.Error(message = "Nim tidak boleh kosong")
        } else if (cek.password.length < 8 && cek.repassword.length < 8) {
            ceksignup.value = Cek.Error(message = "Password tidak boleh kurang dari 8")
        } else if (cek.password != cek.repassword) {
            ceksignup.value = Cek.Error(message = "password harus sama")
        } else {
            viewModelScope.launch {
                ceksignup.value = Cek.Loading
                try {
                    val cekNIm = auth.cekNimUser(signupstate.value.nim)
                    if (!cekNIm.isNullOrEmpty()){
                        ceksignup.value = Cek.Error(message = "NIM sudah ada" )
                        return@launch
                    }
                    val result = auth.Register(
                        email = signupstate.value.email,
                        password = signupstate.value.password,
                        nim = signupstate.value.nim
                    )
                    ceksignup.value = Cek.Sukses

                } catch (e: Exception) {
                    ceksignup.value = Cek.Error(message = e.message.toString())
                }

            }

        }
    }

    fun onSnackDown() {
        ceksignup.value = Cek.idle
    }

}
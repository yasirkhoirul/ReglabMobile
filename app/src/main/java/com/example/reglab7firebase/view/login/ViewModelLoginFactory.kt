package com.example.reglab7firebase.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.EmailValidator

class LoginViewModelFactory(
    private val userRepo: UserRepo,
    private val emailValidator: EmailValidator
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelLogin::class.java)) {
            return ViewModelLogin(userRepo, emailValidator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.reglab7firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reglab7firebase.data.repository.UserRepo

class MainViewModelFactory(
    private val userRepo: UserRepo,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(userRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
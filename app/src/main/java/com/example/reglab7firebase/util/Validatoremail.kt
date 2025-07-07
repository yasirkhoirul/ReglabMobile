package com.example.reglab7firebase.util

import android.util.Patterns

interface EmailValidator {
    fun isValid(email: String): Boolean
}

class AndroidEmailValidator : EmailValidator {
    override fun isValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
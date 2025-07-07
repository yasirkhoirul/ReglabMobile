package com.example.reglab7firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.reglab7firebase.Appnavigasi.RootNav
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        auth.currentUser
        enableEdgeToEdge()
        setContent {
            Reglab7firebaseTheme {
                RootNav()
            }
        }
    }
}

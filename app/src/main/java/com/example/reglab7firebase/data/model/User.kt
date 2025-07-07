package com.example.reglab7firebase.data.model

import android.location.Location
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

data class User(
    val uid : String ="",
    val email: String = "",
    val password: String="",
    val role: String="mahasiswa",
    val nim: String=""
)

data class SignUp(
    val email: String = "",
    val password: String ="",
    val repassword: String = "",
    val nim: String=""
)

data class UpdatePraktikan(
    val idPrak: String = "",
    val idPertemuan: String = "",
    val idMahasiswa: String = "",
    val data: DetailPertemuan = DetailPertemuan()
)

sealed class Cek{
    object idle: Cek()
    data class Error(val message: String): Cek()
    object Sukses: Cek()
    object Loading: Cek()
}

sealed class LocationResult {
    data class Success(val location: Location, val distanceToCampus: Float) :LocationResult()
    object LocationUnavailable : LocationResult()
    object Loading:LocationResult()
    data class Error(val message: String) : LocationResult()
}





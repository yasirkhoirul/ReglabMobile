package com.example.reglab7firebase.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reglab7firebase.data.model.InfoRepo
import com.example.reglab7firebase.data.model.LocationRepo
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.data.model.WifiRepo
import com.example.reglab7firebase.util.EmailValidator
import com.example.reglab7firebase.util.QrCodeGenerator
import com.example.reglab7firebase.util.TimeProvider
import com.example.reglab7firebase.view.dashboard.DashBoardViewModel
import com.example.reglab7firebase.view.dashboard.detailinformasi.DetailViewModel
import com.example.reglab7firebase.view.dashboardAdmin.DashBoardAdminViewModel
import com.example.reglab7firebase.view.dashboardAdmin.koorPraktikum.KoorPraktikumViewModel
import com.example.reglab7firebase.view.dashboardAdmin.titikMap.PilihMapViewModel
import com.example.reglab7firebase.view.generateQrCode.QRCodeViewModel
import com.example.reglab7firebase.view.login.ViewModelLogin
import com.example.reglab7firebase.view.praktikum.PraktikumViewModel
import com.example.reglab7firebase.view.praktikum.detail_praktikum.DetailPraktikumViewModel
import com.example.reglab7firebase.view.praktikum.praktikumTerdekat.PraktikumTerdekatViewModel
import com.example.reglab7firebase.view.praktikumAdmin.PraktikumAdminViewModel
import com.example.reglab7firebase.view.praktikumAdmin.detailPraktikumAdmin.DetailPraktikumAdminViewModel
import com.example.reglab7firebase.view.praktikumAsprak.PraktikumAsprakViewModel
import com.example.reglab7firebase.view.praktikumAsprak.detailPraktikumAsprak.DetailAsprakViewModel
import com.example.reglab7firebase.view.presensi.PresensiViewModel
import com.example.reglab7firebase.view.signup.ViewModelSignUp
import com.example.reglab7firebase.view.tambahDataPraktikum.TambahPraktikumViewModel
import com.example.reglab7firebase.view.tambahOrangPraktikum.TambahMahasiswaViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AppViewModelFactory(
    private val application: Application? = null,
    // Jadikan semua repo bisa null agar fleksibel
    private val userRepo: UserRepo? = null,
    private val praktikumRepo: PraktikumRepo? = null,
    private val infoRepo: InfoRepo? = null,
    private val locationRepo: LocationRepo? = null,
    private val timeRepo: TimeRepo? = null,
    private val wifiRepo: WifiRepo? = null,
    private val emailValidator: EmailValidator? = null ,
    private val dispatcher: CoroutineDispatcher? = null,
    private val timeProvider: TimeProvider? = null,
    private val qrCodeGenerator: QrCodeGenerator? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // AUTHENTICATION
            modelClass.isAssignableFrom(ViewModelLogin::class.java) -> ViewModelLogin(userRepo!!, emailValidator!!,dispatcher ?: Dispatchers.Main) as T
            modelClass.isAssignableFrom(ViewModelSignUp::class.java) -> ViewModelSignUp(userRepo!!, emailValidator!!) as T

            // DASHBOARD & MAIN FEATURES
            modelClass.isAssignableFrom(DashBoardViewModel::class.java) -> DashBoardViewModel(infoRepo!!, userRepo!!, praktikumRepo!!) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(infoRepo!!) as T
            modelClass.isAssignableFrom(PresensiViewModel::class.java) -> PresensiViewModel( timeRepo!!, userRepo!!, praktikumRepo!!, locationRepo!!, wifiRepo!!) as T
            modelClass.isAssignableFrom(QRCodeViewModel::class.java) -> QRCodeViewModel(userRepo!!, praktikumRepo!!, timeRepo!!,qrCodeGenerator!!) as T

            // PRAKTIKUM (MAHASISWA)
            modelClass.isAssignableFrom(PraktikumViewModel::class.java) -> PraktikumViewModel(praktikumRepo!!, userRepo!!) as T
            modelClass.isAssignableFrom(DetailPraktikumViewModel::class.java) -> DetailPraktikumViewModel(praktikumRepo!!, userRepo!!) as T
            modelClass.isAssignableFrom(PraktikumTerdekatViewModel::class.java) -> PraktikumTerdekatViewModel(praktikumRepo!!, userRepo!!) as T

            // PRAKTIKUM (ASPRAK)
            modelClass.isAssignableFrom(PraktikumAsprakViewModel::class.java) -> PraktikumAsprakViewModel(userRepo!!, praktikumRepo!!) as T
            modelClass.isAssignableFrom(DetailAsprakViewModel::class.java) -> DetailAsprakViewModel(timeRepo!!, praktikumRepo!!, userRepo!!) as T

            // ADMIN
            modelClass.isAssignableFrom(DashBoardAdminViewModel::class.java) -> DashBoardAdminViewModel(userRepo!!, infoRepo!!) as T
            modelClass.isAssignableFrom(PraktikumAdminViewModel::class.java) -> PraktikumAdminViewModel(praktikumRepo!!) as T
            modelClass.isAssignableFrom(DetailPraktikumAdminViewModel::class.java) -> DetailPraktikumAdminViewModel(userRepo!!, praktikumRepo!!) as T
            modelClass.isAssignableFrom(TambahPraktikumViewModel::class.java) -> TambahPraktikumViewModel(
                praktikumRepo!!,
                timeProvider!!
            ) as T
            modelClass.isAssignableFrom(TambahMahasiswaViewModel::class.java) -> TambahMahasiswaViewModel(praktikumRepo!!, userRepo!!) as T
            modelClass.isAssignableFrom(KoorPraktikumViewModel::class.java) -> KoorPraktikumViewModel(praktikumRepo!!, userRepo!!) as T
            modelClass.isAssignableFrom(PilihMapViewModel::class.java) -> {
                PilihMapViewModel(locationRepo!!) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
package com.example.reglab7firebase.Appnavigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reglab7firebase.MainViewModel
import com.example.reglab7firebase.view.dashboard.Dashboard
import com.example.reglab7firebase.view.dashboard.detailinformasi.Detalindormasi
import com.example.reglab7firebase.view.generateQrCode.PrevQr
import com.example.reglab7firebase.view.login.Login
import com.example.reglab7firebase.view.login.ViewModelLogin
import com.example.reglab7firebase.view.praktikum.Praktikum
import com.example.reglab7firebase.view.praktikum.detail_praktikum.DetailPraktikum
import com.example.reglab7firebase.view.praktikum.praktikumTerdekat.PraktikumTerdekat
import com.example.reglab7firebase.view.praktikumAdmin.PraktikumAdmin
import com.example.reglab7firebase.view.praktikumAdmin.detailPraktikumAdmin.DetailPrakAdmin
import com.example.reglab7firebase.view.praktikumAsprak.PraktikumAsprak
import com.example.reglab7firebase.view.praktikumAsprak.detailPraktikumAsprak.DetailPraktikumAsprak
import com.example.reglab7firebase.view.presensi.Presensi
import com.example.reglab7firebase.view.signup.SignUP
import com.example.reglab7firebase.view.tambahOrangPraktikum.TambahMahasiswaScreen
import com.example.reglab7firebase.view.tambahDataPraktikum.Tambahpraktikum
import kotlin.math.log

object AppDestinations {
    const val DASHBOARD_ROUTE = "maindash"
    const val PRESENSI_ROUTE = "presensi"
}

@Composable
fun AppNAv(modifier: Modifier = Modifier, navController: NavHostController, logout: () -> Unit) {

    NavHost(navController = navController, startDestination = "main") {
        composable(route = "signup") {
            SignUP(onBack = { navController.popBackStack() })
        }
        composable(route = "main") {
            Dashboard(
                navigateDetail = { navController.navigate(route = "detail/$it") },
                navigatePrak = { navController.navigate(route = "praktikumUser") },
                navigateTambahPrak = {
                    navController.navigate(route = "praktikumAdmin")
                },
                onPrakTerdekat = {
                    navController.navigate(route = "prakTerdekat")
                },
                klikasisten = {
                    navController.navigate(route = "Asisten")
                },
                navigateLogin = logout,
                mainctontroller = navController
            )

        }
        composable(route = "Asisten"){
            PraktikumAsprak(
                clickDetailAsprak = { navController.navigate("detailAsprak/$it") },
                isAdmin = false
            )
        }
        composable(route = "detailAsprak/{uidItem}", arguments = listOf(navArgument("uidItem") {
            type = NavType.StringType
        })) {
            DetailPraktikumAsprak(uid = it.arguments?.getString("uidItem").toString(), generateqr = { idPrak,idPertemuan->navController.navigate(route = "qrcode/$idPrak?idPertemuan=$idPertemuan") })
        }

        composable(route = "prakTerdekat") {
            PraktikumTerdekat(onDetail = { navController.navigate("detailPrak/$it") })
        }
        composable(route = "detail/{uidItem}", arguments = listOf(navArgument("uidItem") {
            type = NavType.StringType
        })) {
            Detalindormasi(uid = it.arguments?.getString("uidItem").toString())
        }
        composable(route = "praktikumUser") {
            Praktikum(clickDetail = {
                navController.navigate("detailPrak/$it")
            })
        }
        composable(
            route = "detailPrak/{idPertemuan}",
            arguments = listOf(navArgument("idPertemuan") {
                type =
                    NavType.StringType
            })
        ) {
            DetailPraktikum(uid = it.arguments?.getString("idPertemuan").toString())
        }

        composable(route = "praktikumAdmin") {
            PraktikumAdmin(
                navigateTambah = { navController.navigate(route = "tambahPraktikum") },
                navigateDetailPrakAdmin = { navController.navigate(route = "detailPrakAdmin/$it") }
            )
        }

        composable(route = "tambahPraktikum") {
            Tambahpraktikum()
        }

        composable(
            route = "detailPrakAdmin/{idPraktium}",
            arguments = listOf(navArgument("idPraktium") {
                type =
                    NavType.StringType
            })
        ) {
            DetailPrakAdmin(
                id_prak = it.arguments?.getString("idPraktium").toString(),
                navigaTeSarching = { idprak, isasprak -> navController.navigate(route = "searching/$idprak?isAsprak=$isasprak") })
        }
        composable(
            route = "searching/{idPrak}?isAsprak={isAsprak}",
            arguments = listOf(
                navArgument("idPrak") { type = NavType.StringType },
                navArgument("isAsprak") {
                    type = NavType.BoolType
                    defaultValue = false // Memberi nilai default adalah praktik yang sangat baik
                })
        ) {
            TambahMahasiswaScreen(
                idPrak = it.arguments?.getString("idPrak").toString(),
                isAsprak = it.arguments?.getBoolean("isAsprak") == true
            )
        }

        composable(
            route = "qrcode/{idPrak}?idPertemuan={idPertemuan}",
            arguments = listOf(
                navArgument("idPrak") { type = NavType.StringType },
                navArgument("idPertemuan") { type = NavType.StringType })
        ) {
            PrevQr(
                idPrak = it.arguments?.getString("idPrak").toString(),
                idPertemuan = it.arguments?.getString("idPertemuan").toString()
            )
        }
    }

}
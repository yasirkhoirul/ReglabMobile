package com.example.reglab7firebase.Appnavigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.reglab7firebase.view.dashboard.detailinformasi.Detalindormasi
import com.example.reglab7firebase.view.dashboardAdmin.DashboardAdmin
import com.example.reglab7firebase.view.dashboardAdmin.koorPraktikum.KoorPraktikum
import com.example.reglab7firebase.view.dashboardAdmin.titikMap.PilihMap
import com.example.reglab7firebase.view.generateQrCode.PrevQr
import com.example.reglab7firebase.view.praktikumAdmin.PraktikumAdmin
import com.example.reglab7firebase.view.praktikumAdmin.detailPraktikumAdmin.DetailPrakAdmin
import com.example.reglab7firebase.view.praktikumAsprak.PraktikumAsprak
import com.example.reglab7firebase.view.praktikumAsprak.detailPraktikumAsprak.DetailPraktikumAsprak
import com.example.reglab7firebase.view.tambahDataPraktikum.Tambahpraktikum
import com.example.reglab7firebase.view.tambahOrangPraktikum.TambahMahasiswaScreen

@Composable
fun AdminNav(navController: NavHostController,logout:()-> Unit) {
    NavHost(navController = navController, startDestination = "dashboardAdmin") {
        composable (route = "dashboardAdmin") {
            DashboardAdmin(
                signOut = logout,
                navigateDetail = { navController.navigate(route = "detail/$it") },
                klikTambah = { navController.navigate(route = "praktikumAdmin") },
                klikPrak = { navController.navigate(route = "Asisten") },
                klikKoor = { navController.navigate(route = "Koor") },
                kliktitik = { navController.navigate(route = "pilihmap") }
            )
        }
        composable(route = "pilihmap") {
            PilihMap()
        }
        composable (route = "Koor") {
            KoorPraktikum()
        }
        composable(route = "Asisten"){
            PraktikumAsprak(
                clickDetailAsprak = { navController.navigate("detailAsprak/$it") },
                isAdmin = true
            )
        }
        composable(route = "detailAsprak/{uidItem}", arguments = listOf(navArgument("uidItem") {
            type = NavType.StringType
        })) {
            DetailPraktikumAsprak(uid = it.arguments?.getString("uidItem").toString(), generateqr = { idPrak,idPertemuan->navController.navigate(route = "qrcode/$idPrak?idPertemuan=$idPertemuan") })
        }
        composable(route = "detail/{uidItem}", arguments = listOf(navArgument("uidItem") {
            type = NavType.StringType
        })) {
            Detalindormasi(uid = it.arguments?.getString("uidItem").toString())
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
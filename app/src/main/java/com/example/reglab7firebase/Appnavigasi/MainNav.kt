package com.example.reglab7firebase.Appnavigasi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reglab7firebase.data.model.Isi
import com.example.reglab7firebase.view.component.IsiDash
import com.example.reglab7firebase.view.component.IsiDashAdmin
import com.example.reglab7firebase.view.dashboard.Dashboard
import com.example.reglab7firebase.view.presensi.Presensi

@Composable
fun MainNavs(
    klikPrak: () -> Unit,
    jumlahPrak: String,
    onPrakTerdekat: () -> Unit,
    isiinformasi: List<Isi>,
    klikIsi: (String) -> Unit,
    modifier: Modifier,
    scafoldController: NavHostController,
    klikasisten:()-> Unit

) {
    NavHost(navController = scafoldController, startDestination = "maindash") {
        composable(route = "maindash") {
            IsiDash(
                modifier = modifier,
                jumlahPrak = jumlahPrak,
                onPrakTerdekat = onPrakTerdekat,
                isiinformasi = isiinformasi,
                klikIsi = klikIsi,
                klikPrak = klikPrak,
                klikasisten = klikasisten
            )
        }
        composable(route = "presensi") {
            Presensi(modifier = modifier)
        }
    }
}
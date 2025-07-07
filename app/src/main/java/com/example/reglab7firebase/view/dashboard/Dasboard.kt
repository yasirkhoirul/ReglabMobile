package com.example.reglab7firebase.view.dashboard

import android.util.Log
import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.R
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.Appnavigasi.AppDestinations
import com.example.reglab7firebase.Appnavigasi.AppNAv
import com.example.reglab7firebase.data.model.BottomNavItem
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.InfoRepoImpl
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DrawableNavigation
import com.example.reglab7firebase.view.component.TopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    mainctontroller: NavHostController,
    viewModel: DashBoardViewModel = viewModel(
        factory = AppViewModelFactory(
            infoRepo = InfoRepoImpl(),
            userRepo = ImplementasiUserRepo(),
            praktikumRepo = PraktikumRepoImpl()
        )
    ),
    navigateLogin: () -> Unit,
    navigateDetail: (String) -> Unit,
    navigatePrak: () -> Unit,
    navigateTambahPrak: () -> Unit,
    onPrakTerdekat: (String) -> Unit,
    klikasisten: () -> Unit,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.backback)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = false
    )
    val jumlahPrak by viewModel.uiJumlahPraktikum.collectAsState()
    val profile by viewModel.uiprofie.collectAsState()
    val informasi by viewModel.uisinformasistate.collectAsState()
    LaunchedEffect(Unit) {
        Log.d("DEBUGLAUNCH", "dijalankan")
        viewModel.uinavigatelogin.collect {
            navigateLogin()
        }
    }
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0284C7),
                        Color(0xFFFFFFFF),
                        Color(0xFFFFFFFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            // `ContentScale.Crop` akan memastikan animasi mengisi seluruh ruang
            // tanpa merusak rasio aspek (mirip background-size: cover di web)
            contentScale = ContentScale.Crop
        )
        DrawableNavigation(
            signOut = {
                viewModel.Logout()
                viewModel.getUser()
            },
            nama = profile?.email ?: "sedang memuat...",
            nim = profile?.role ?: "sedang memuat...",
            isiinformasi = informasi,
            klikIsi = navigateDetail,
            klikPrak = navigatePrak,
            jumlahPrak = jumlahPrak.size.toString(),
            toTambahPrak = navigateTambahPrak,
            onPrakTerdekat = {
                onPrakTerdekat(
                    profile?.uid ?: ""
                )
            },
            mainctontroller = mainctontroller,
            klikasisten = klikasisten
        )
    }

}

@Preview()
@Composable
private fun DashboardPrevfun() {
    Reglab7firebaseTheme {
//        Dashboard()
    }
}
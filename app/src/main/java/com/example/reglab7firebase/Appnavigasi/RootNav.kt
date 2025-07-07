package com.example.reglab7firebase.Appnavigasi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reglab7firebase.MainViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.example.reglab7firebase.MainViewModelFactory
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.util.AndroidEmailValidator
import com.example.reglab7firebase.view.component.Loading
import com.example.reglab7firebase.view.login.LoginViewModelFactory
import com.example.reglab7firebase.view.login.ViewModelLogin
import com.google.android.play.integrity.internal.v

@Composable
fun RootNav(
    modifier: Modifier = Modifier,
    start: MainViewModel = viewModel(
        factory = MainViewModelFactory (
            ImplementasiUserRepo()
        )
    ),
) {
    val navController = rememberNavController()
//    val startDestination = if (start.startdes) "mainNav" else "auth"
    val star by start.uistartDest.collectAsStateWithLifecycle()
    val status by start.uistatus.collectAsStateWithLifecycle()
    if (status is Cek.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
    } else {
        var startDestination = "auth"
        if (!star.isNullOrEmpty()) {
            if (star == "admin") {
                startDestination = "AdminNav"
            } else if (star == "mahasiswa") {
                startDestination = "mainNav"
            }
        }
        NavHost(
            navController = navController,
            startDestination = startDestination,
            route = "root"
        ) {
            composable(route = "auth") {
                AuthNav(
                    navDas = {
                        navController.navigate(route = "mainNav") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateAdmin = {
                        navController.navigate(route = "AdminNav") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(route = "mainNav") {
                val mainNavController = rememberNavController()
                AppNAv(navController = mainNavController, logout = {
                    navController.navigate(route = "auth") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                })
            }
            composable(route = "AdminNav") {
                val mainNavController = rememberNavController()
                AdminNav(navController = mainNavController, logout = {
                    navController.navigate(route = "auth") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                })
            }
        }
    }

}
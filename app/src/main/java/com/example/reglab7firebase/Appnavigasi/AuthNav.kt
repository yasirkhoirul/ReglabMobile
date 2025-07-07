package com.example.reglab7firebase.Appnavigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reglab7firebase.view.login.Login
import com.example.reglab7firebase.view.login.ViewModelLogin
import com.example.reglab7firebase.view.signup.SignUP

@Composable
fun AuthNav(modifier: Modifier = Modifier, navDas:()-> Unit,onNavigateAdmin:()-> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login"){
        composable(route = "login") {
            Login(
                onNavigatetoSignup = { navController.navigate(route = "signup") },
                onNavigateDash = navDas,
                onNavigateAdmin = onNavigateAdmin
            )
        }
        composable(route = "signup") {
            SignUP(onBack = { navController.popBackStack() })
        }
    }
}
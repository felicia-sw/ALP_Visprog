package com.example.alp_visprog.ui.route

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alp_visprog.views.LoginView
import com.example.alp_visprog.views.RegisterView

@Composable
fun AppRouting() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginView(navController)
        }
        composable("register") {
            RegisterView(navController)
        }
    }
}

package com.innovagab.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.innovagab.app.data.auth.UserRole
import com.innovagab.app.features.auth.AuthUiState
import com.innovagab.app.features.auth.AuthViewModel
import com.innovagab.app.features.auth.LoginScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val currentProfile = (authState as? AuthUiState.Authenticated)?.profile

    fun signOut() {
        authViewModel.signOut()
        navController.navigate(AppRoutes.Login.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Login.route,
    ) {
        composable(AppRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onAuthenticated = { role ->
                    val destination = when (role) {
                        UserRole.OPERADOR -> AppRoutes.OperadorGraph.route
                        UserRole.GESTOR -> AppRoutes.GestorGraph.route
                        UserRole.LIDERANCA -> AppRoutes.LiderancaGraph.route
                    }
                    navController.navigate(destination) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppRoutes.OperadorGraph.route) {
            OperadorNav(
                userName = currentProfile?.name ?: "Operador",
                onSignOut = ::signOut,
            )
        }

        composable(AppRoutes.GestorGraph.route) {
            GestorNav(onSignOut = ::signOut)
        }

        composable(AppRoutes.LiderancaGraph.route) {
            LiderancaNav(onSignOut = ::signOut)
        }
    }
}

package com.biocube.navigation.presentation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.biocube.login.presentation.LoginScreen
import com.biocube.profile.presentation.ProfileScreen
import com.biocube.services.presentation.ServicesScreen
import com.biocube.splash.presentation.SplashScreen
import com.biocube.usertrainings.presentation.UserTrainingsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Profile : Screen("profile")
    object UserTrainings : Screen("user_trainings")
    object Services : Screen("services")
}

@Composable
fun BiocubeNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToUserTrainings = {
                    navController.navigate(Screen.UserTrainings.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isFirstLogin ->
                    if (isFirstLogin) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.UserTrainings.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onContinue = {
                    navController.navigate(Screen.UserTrainings.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.UserTrainings.route) {
            UserTrainingsScreen(
                onNavigateToServices = {
                    navController.navigate(Screen.Services.route)
                }
            )
        }

        composable(Screen.Services.route) {
            ServicesScreen(
                onNavigateToUserTrainings = {
                    navController.popBackStack()
                }
            )
        }
    }
}

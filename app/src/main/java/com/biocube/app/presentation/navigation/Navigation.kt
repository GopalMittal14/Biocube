package com.biocube.app.presentation.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.biocube.app.domain.repository.IUserRepository
import com.biocube.app.presentation.login.LoginScreen
import com.biocube.app.presentation.profile.ProfileScreen
import com.biocube.app.presentation.services.ServicesScreen
import com.biocube.app.presentation.splash.SplashScreen
import com.biocube.app.presentation.usertrainings.UserTrainingsScreen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onExit = {
                    // Exit app
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

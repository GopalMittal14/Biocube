package com.biocube.app.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.biocube.app.R
import com.biocube.app.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToUserTrainings: () -> Unit,
    userRepository: IUserRepository = hiltViewModel<SplashViewModel>().userRepository
) {
    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        
        val currentUser = userRepository.getCurrentUser().firstOrNull()
        if (currentUser != null) {
            onNavigateToUserTrainings()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BIOCUBE",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Biometric Authentication System",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    val userRepository: IUserRepository
) : androidx.lifecycle.ViewModel()

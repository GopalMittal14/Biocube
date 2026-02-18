package com.biocube.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Intent
import com.biocube.auth.face.FaceAuthActivity

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToUserTrainings: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasDecided by remember { mutableStateOf(false) }

    val faceAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (hasDecided) return@rememberLauncherForActivityResult
        hasDecided = true

        if (result.resultCode == Activity.RESULT_OK) {
            onNavigateToUserTrainings()
        } else {
            scope.launch {
                viewModel.logout()
                onNavigateToLogin()
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds

        val currentUser = viewModel.getCurrentUserSync()
        if (currentUser == null) {
            hasDecided = true
            onNavigateToLogin()
            return@LaunchedEffect
        }

        val requireFaceAuth = viewModel.hasEnrolledFace(currentUser.id)
        if (requireFaceAuth) {
            faceAuthLauncher.launch(Intent(context, FaceAuthActivity::class.java))
        } else {
            hasDecided = true
            onNavigateToUserTrainings()
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

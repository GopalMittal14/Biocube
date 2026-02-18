package com.biocube.usertrainings
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class UserTrainingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserTrainingsScreen(onNavigateToServices = {})
        }
    }
}

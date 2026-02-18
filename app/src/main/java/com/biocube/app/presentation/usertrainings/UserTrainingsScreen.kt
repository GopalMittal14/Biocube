package com.biocube.app.presentation.usertrainings

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.biocube.auth.eye.eyescan.EyeScanActivity
import com.biocube.auth.face.facescan.FaceScanActivity
import com.biocube.auth.fingerprint.fingerprintscan.FingerprintScanActivity
import com.biocube.auth.palm.palmscan.PalmScanActivity
import com.biocube.auth.voice.voicescan.VoiceScanActivity
import com.biocube.core.domain.model.BiometricScan
import com.biocube.core.domain.model.ScanType
import kotlinx.coroutines.launch
import com.biocube.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTrainingsScreen(
    viewModel: UserTrainingsViewModel = hiltViewModel(),
    onNavigateToServices: () -> Unit
) {
    val scansState by viewModel.scansState.collectAsState()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val faceScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful face scan
        }
    }

    val eyeScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful eye scan
        }
    }

    val voiceScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful voice scan
        }
    }

    val palmScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful palm scan
        }
    }

    val fingerprintScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful fingerprint scan
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { /* TODO: Handle Profile click */ }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Services") },
                        label = { Text("Services") },
                        selected = false,
                        onClick = onNavigateToServices
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = false,
                        onClick = { /* TODO: Handle Home click */ }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                        label = { Text("Logout") },
                        selected = false,
                        onClick = { /* TODO: Handle Logout click */ }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit") },
                        label = { Text("Exit") },
                        selected = false,
                        onClick = { (context as? Activity)?.finish() }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Biometric Trainings") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.biocube_banner),
                        contentDescription = "Biocube Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                when (val state = scansState) {
                    is ScansState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(vertical = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is ScansState.Success -> {
                        items(state.scans) { scan ->
                            TrainingItem(scan = scan, onScanClick = {
                                viewModel.onScanClick(it)
                                when (it.type) {
                                    ScanType.FACE_SCAN -> {
                                        val intent = Intent(context, FaceScanActivity::class.java)
                                        faceScanLauncher.launch(intent)
                                    }
                                    ScanType.EYE_SCAN -> {
                                        val intent = Intent(context, EyeScanActivity::class.java)
                                        eyeScanLauncher.launch(intent)
                                    }
                                    ScanType.VOICE_SCAN -> {
                                        val intent = Intent(context, VoiceScanActivity::class.java)
                                        voiceScanLauncher.launch(intent)
                                    }
                                    ScanType.PALM_SCAN -> {
                                        val intent = Intent(context, PalmScanActivity::class.java)
                                        palmScanLauncher.launch(intent)
                                    }
                                    ScanType.FINGER_PRINT -> {
                                        val intent = Intent(context, FingerprintScanActivity::class.java)
                                        fingerprintScanLauncher.launch(intent)
                                    }
                                }
                            })
                        }
                    }
                    is ScansState.Error -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(vertical = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onNavigateToServices,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Services")
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingItem(
    scan: BiometricScan,
    onScanClick: (BiometricScan) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onScanClick(scan) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = scan.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            if (scan.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Incomplete",
                    tint = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Start Training",
                tint = Color.Gray
            )
        }
    }
}
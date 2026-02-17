package com.biocube.app.presentation.usertrainings

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.biocube.app.domain.model.BiometricScan
import com.biocube.app.domain.model.ScanType
import com.biocube.app.presentation.facescan.FaceScanActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTrainingsScreen(
    onNavigateToServices: () -> Unit,
    onLogout: () -> Unit,
    onExit: () -> Unit,
    viewModel: UserTrainingsViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scansState by viewModel.scansState.collectAsState()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onProfileClick = { /* Navigate to profile */ },
                onServicesClick = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToServices()
                    }
                },
                onHomeClick = {
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    scope.launch {
                        drawerState.close()
                        onLogout()
                    }
                },
                onExitClick = {
                    (context as? Activity)?.finish()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("User Trainings") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Biocube Banner
                BiocubeBanner()

                Spacer(modifier = Modifier.height(16.dp))

                // Scans List
                when (val state = scansState) {
                    is ScansState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is ScansState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.scans) { scan ->
                                ScanCard(
                                    scan = scan,
                                    onClick = {
                                        if (scan.type == ScanType.FACE_SCAN) {
                                            context.startActivity(Intent(context, FaceScanActivity::class.java))
                                        } else {
                                            viewModel.onScanClick(scan)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    is ScansState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Services Button Footer
                Button(
                    onClick = onNavigateToServices,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .height(56.dp)
                ) {
                    Text("Services", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Go to Services")
                }
            }
        }
    }
}

@Composable
fun BiocubeBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BIOCUBE",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Biometric Training System",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ScanCard(scan: BiometricScan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getScanIcon(scan.type),
                contentDescription = scan.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scan.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (scan.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun NavigationDrawerContent(
    onProfileClick: () -> Unit,
    onServicesClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onExitClick: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // User Profile Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "User Name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "user@biocube.com",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items
            NavigationDrawerItem(
                label = { Text("Profile") },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                selected = false,
                onClick = onProfileClick
            )

            NavigationDrawerItem(
                label = { Text("Services") },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Services") },
                selected = false,
                onClick = onServicesClick
            )

            NavigationDrawerItem(
                label = { Text("Home") },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                selected = true,
                onClick = onHomeClick
            )

            Spacer(modifier = Modifier.weight(1f))

            NavigationDrawerItem(
                label = { Text("Logout") },
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                selected = false,
                onClick = onLogoutClick
            )

            NavigationDrawerItem(
                label = { Text("Exit") },
                icon = { Icon(Icons.Default.Close, contentDescription = "Exit") },
                selected = false,
                onClick = onExitClick
            )
        }
    }
}

fun getScanIcon(scanType: ScanType): ImageVector {
    return when (scanType) {
        ScanType.FACE_SCAN -> Icons.Default.Face
        ScanType.EYE_SCAN -> Icons.Default.RemoveRedEye
        ScanType.VOICE_SCAN -> Icons.Default.Mic
        ScanType.PALM_SCAN -> Icons.Default.PanTool
        ScanType.FINGER_PRINT -> Icons.Default.Fingerprint
    }
}

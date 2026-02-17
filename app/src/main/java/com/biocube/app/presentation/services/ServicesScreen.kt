package com.biocube.app.presentation.services

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.biocube.app.domain.model.Service
import com.biocube.app.domain.model.ServiceType
import com.biocube.app.presentation.faceauth.FaceAuthActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onNavigateToUserTrainings: () -> Unit,
    viewModel: ServicesViewModel = hiltViewModel()
) {
    val servicesState by viewModel.servicesState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var pendingAction by remember { mutableStateOf<ServiceType?>(null) }

    val faceAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val action = pendingAction
        pendingAction = null
        if (result.resultCode == Activity.RESULT_OK && action != null) {
            val message = when (action) {
                ServiceType.CHECK_IN -> "Check-in successful"
                ServiceType.CHECK_OUT -> "Check-out successful"
                else -> return@rememberLauncherForActivityResult
            }
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Services") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToUserTrainings) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            when (val state = servicesState) {
                is ServicesState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ServicesState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.services) { service ->
                            ServiceCard(
                                service = service,
                                onClick = {
                                    when (service.type) {
                                        ServiceType.CHECK_IN, ServiceType.CHECK_OUT -> {
                                            pendingAction = service.type
                                            faceAuthLauncher.launch(
                                                Intent(context, FaceAuthActivity::class.java)
                                            )
                                        }
                                        else -> viewModel.onServiceClick(service)
                                    }
                                }
                            )
                        }
                    }
                }
                is ServicesState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // User Trainings Button Footer
            Button(
                onClick = onNavigateToUserTrainings,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                Spacer(modifier = Modifier.width(8.dp))
                Text("User Trainings", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ServiceCard(service: Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getServiceIcon(service.type),
                contentDescription = service.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun getServiceIcon(serviceType: ServiceType): ImageVector {
    return when (serviceType) {
        ServiceType.E_VISA -> Icons.Default.CardTravel
        ServiceType.ATTENDANCE -> Icons.Default.CalendarToday
        ServiceType.CHECK_IN -> Icons.Default.CheckCircle
        ServiceType.CHECK_OUT -> Icons.Default.ExitToApp
        ServiceType.INSURANCE -> Icons.Default.Security
        ServiceType.BANKING -> Icons.Default.AccountBalance
        ServiceType.OTHER -> Icons.Default.MoreHoriz
    }
}

package com.biocube.auth.face.facescan

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.biocube.core.ui.theme.BiocubeTheme
import coil.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FaceScanActivity : ComponentActivity() {

    private val faceScanViewModel: FaceScanViewModel by viewModels()

    private var photoUri: Uri? = null
    private var outputFile: File? = null

    private var capturedImageUri by mutableStateOf<Uri?>(null)

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> launchCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                showPermissionPermanentlyDeniedDialog()
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null && outputFile != null) {
            capturedImageUri = photoUri
            faceScanViewModel.saveFaceScan(outputFile!!.absolutePath)
        } else if (!success) {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiocubeTheme {
                val saveState by faceScanViewModel.saveState.collectAsState()

                LaunchedEffect(saveState) {
                    when (val state = saveState) {
                        is FaceScanSaveState.Success -> {
                            Toast.makeText(this@FaceScanActivity, "Face scan saved successfully", Toast.LENGTH_SHORT).show()
                            faceScanViewModel.resetSaveState()
                            finish()
                        }
                        is FaceScanSaveState.Error -> {
                            Toast.makeText(this@FaceScanActivity, state.message, Toast.LENGTH_LONG).show()
                            faceScanViewModel.resetSaveState()
                        }
                        else -> {}
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FaceScanContent(
                        onCameraClick = { checkPermissionAndLaunchCamera() },
                        isSaving = saveState is FaceScanSaveState.Loading,
                        capturedImageUri = capturedImageUri
                    )
                }
            }
        }
    }

    private fun checkPermissionAndLaunchCamera() {
        when {
            !hasCameraHardware() -> {
                Toast.makeText(this, "No camera available on this device", Toast.LENGTH_LONG).show()
                finish()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun hasCameraHardware(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("Camera permission is required to capture your face image for biometric authentication. The image will be saved securely in the app's local database.")
            .setPositiveButton("Grant") { _, _ ->
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Camera permission is required to capture face scan", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionPermanentlyDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Denied")
            .setMessage("Camera permission was denied. To capture your face scan, please grant camera permission in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Camera permission is required to capture face scan", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun launchCamera() {
        lifecycleScope.launch {
            val user = faceScanViewModel.getCurrentUserSync()
            if (user == null) {
                Toast.makeText(this@FaceScanActivity, "No user logged in. Please log in first.", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            launchCameraInternal(user.id)
        }
    }

    private fun launchCameraInternal(userId: String) {
        val faceScansDir = File(filesDir, "face_scans").apply { mkdirs() }
        outputFile = File(faceScansDir, "face_${userId}_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            outputFile!!
        )
        takePictureLauncher.launch(photoUri!!)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceScanContent(
    onCameraClick: () -> Unit,
    isSaving: Boolean,
    capturedImageUri: Uri?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Face Scan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Capture your face for biometric authentication",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))
            FilledIconButton(
                onClick = onCameraClick,
                enabled = !isSaving,
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture face",
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isSaving) "Saving..." else "Tap to capture",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (capturedImageUri != null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Last captured image preview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(capturedImageUri),
                    contentDescription = "Captured face",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

package com.biocube.auth.face

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.biocube.core.ui.theme.BiocubeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FaceAuthActivity : ComponentActivity() {

    private val viewModel: FaceAuthViewModel by viewModels()

    private var photoUri: Uri? = null
    private var outputFile: File? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> launchCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showPermissionRationaleDialog()
            else -> showPermissionPermanentlyDeniedDialog()
        }
    }

    private val captureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && outputFile != null) {
            viewModel.verifyFace(outputFile!!.absolutePath)
        } else if (result.resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BiocubeTheme {
                val state by viewModel.state.collectAsState()

                LaunchedEffect(state) {
                    when (val s = state) {
                        is FaceAuthState.Success -> {
                            setResult(RESULT_OK)
                            finish()
                        }
                        is FaceAuthState.Error -> {
                            Toast.makeText(this@FaceAuthActivity, s.message, Toast.LENGTH_LONG).show()
                            viewModel.reset()
                        }
                        else -> {}
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FaceAuthContent(
                        onCameraClick = { checkPermissionAndLaunchCamera() },
                        isWorking = state is FaceAuthState.Loading
                    )
                }
            }
        }
    }

    private fun checkPermissionAndLaunchCamera() {
        when {
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

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("Camera permission is required to authenticate using face recognition.")
            .setPositiveButton("Grant") { _, _ -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                setResult(RESULT_CANCELED)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionPermanentlyDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Denied")
            .setMessage("To authenticate with your face, please grant camera permission in app settings.")
            .setPositiveButton("Open Settings") { _, _ -> openAppSettings() }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                setResult(RESULT_CANCELED)
                finish()
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
        val dir = File(filesDir, "face_auth_attempts").apply { mkdirs() }
        outputFile = File(dir, "face_auth_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            outputFile!!
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            // Best-effort hints to use the front camera (not guaranteed on all devices)
            putExtra("android.intent.extras.CAMERA_FACING", 1)
            putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        }
        captureLauncher.launch(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceAuthContent(
    onCameraClick: () -> Unit,
    isWorking: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Face Authentication") },
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
                text = "Confirm your identity with a face photo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))
            FilledIconButton(
                onClick = onCameraClick,
                enabled = !isWorking,
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Authenticate face",
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isWorking) "Verifying..." else "Tap to capture",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

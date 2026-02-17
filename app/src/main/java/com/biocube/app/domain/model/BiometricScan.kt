package com.biocube.app.domain.model

data class BiometricScan(
    val id: String,
    val type: ScanType,
    val name: String,
    val description: String,
    val iconRes: Int? = null,
    val isCompleted: Boolean = false
)

enum class ScanType {
    FACE_SCAN,
    EYE_SCAN,
    VOICE_SCAN,
    PALM_SCAN,
    FINGER_PRINT
}

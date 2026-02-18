package com.biocube.core.data.faceauth

object FaceModelConfig {
    const val assetPath: String = "face_auth.tflite"

    //For cosine similarity: thresholds Start with 0.55..0.70
    const val defaultCosineThreshold: Float = 0.60f
}

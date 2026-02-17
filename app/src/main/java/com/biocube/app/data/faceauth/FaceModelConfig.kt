package com.biocube.app.data.faceauth

/**
 * Configuration for the on-device face embedding model.
 *
 * You must place the TFLite model file at:
 * `app/src/main/assets/${assetPath}`
 *
 * This project expects a model that takes an RGB image tensor and outputs a single
 * embedding vector (e.g., MobileFaceNet / FaceNet style).
 */
object FaceModelConfig {
    const val assetPath: String = "face_auth.tflite"

    /**
     * For cosine similarity: typical thresholds vary by model and preprocessing.
     * Start with 0.55..0.70 and tune using your own data.
     */
    const val defaultCosineThreshold: Float = 0.60f
}


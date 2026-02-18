package com.biocube.app.data.faceauth
import kotlin.math.sqrt

object FaceMath {
    fun l2Normalize(values: FloatArray, epsilon: Float = 1e-10f): FloatArray {
        var sumSq = 0f
        for (v in values) sumSq += v * v
        val norm = sqrt(sumSq.coerceAtLeast(epsilon))
        val out = FloatArray(values.size)
        for (i in values.indices) out[i] = values[i] / norm
        return out
    }

    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size) { "Embedding size mismatch: ${a.size} vs ${b.size}" }
        var dot = 0f
        for (i in a.indices) dot += a[i] * b[i]
        return dot
    }
}


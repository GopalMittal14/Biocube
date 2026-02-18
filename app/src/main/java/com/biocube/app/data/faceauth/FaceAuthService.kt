package com.biocube.app.data.faceauth
import com.biocube.app.data.local.dao.FaceScanDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceAuthService @Inject constructor(
    private val faceScanDao: FaceScanDao,
    private val embeddingExtractor: FaceEmbeddingExtractor
) {
    suspend fun hasEnrollment(userId: String): Boolean {
        val scan = faceScanDao.getFaceScanByUserId(userId)
        return scan?.embedding != null && scan.embedding.isNotEmpty()
    }

    suspend fun enrollFromImage(userId: String, imagePath: String): FloatArray {
        val embedding = embeddingExtractor.extractEmbeddingFromFile(imagePath)
        return embedding
    }

    suspend fun verifyFromImage(
        userId: String,
        imagePath: String,
        cosineThreshold: Float = FaceModelConfig.defaultCosineThreshold
    ): FaceVerificationResult {
        val enrolled = faceScanDao.getFaceScanByUserId(userId)
            ?: return FaceVerificationResult.NotEnrolled

        val enrolledBytes = enrolled.embedding
        if (enrolledBytes == null || enrolledBytes.isEmpty()) return FaceVerificationResult.NotEnrolled

        val enrolledEmbedding = FaceMath.l2Normalize(FloatArrayCodec.fromByteArray(enrolledBytes))
        val probeEmbedding = embeddingExtractor.extractEmbeddingFromFile(imagePath)

        val similarity = embeddingExtractor.cosineSimilarity(enrolledEmbedding, probeEmbedding)
        return if (similarity >= cosineThreshold) {
            FaceVerificationResult.Match(similarity = similarity, threshold = cosineThreshold)
        } else {
            FaceVerificationResult.NoMatch(similarity = similarity, threshold = cosineThreshold)
        }
    }
}

sealed class FaceVerificationResult {
    data object NotEnrolled : FaceVerificationResult()
    data class Match(val similarity: Float, val threshold: Float) : FaceVerificationResult()
    data class NoMatch(val similarity: Float, val threshold: Float) : FaceVerificationResult()
}


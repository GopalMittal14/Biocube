package com.biocube.core.data.faceauth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceEmbeddingExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .enableTracking()
            .build()
        FaceDetection.getClient(options)
    }

    private val interpreter: Interpreter by lazy {
        try {
            val options = Interpreter.Options().apply { setNumThreads(4) }
            Interpreter(loadModelFile(FaceModelConfig.assetPath), options)
        } catch (e: Exception) {
            throw FaceModelUnavailableException(e)
        }
    }

    private fun loadModelFile(assetPath: String): MappedByteBuffer {
        context.assets.openFd(assetPath).use { fd ->
            FileInputStream(fd.fileDescriptor).use { input ->
                val channel = input.channel
                return channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
            }
        }
    }

    suspend fun extractEmbeddingFromFile(imagePath: String): FloatArray {
        Log.d(TAG, "Starting embedding extraction from file: $imagePath")
        val bitmap = BitmapFactory.decodeFile(imagePath)
            ?: throw IllegalArgumentException("Unable to decode image: $imagePath")
        Log.d(TAG, "Decoded bitmap from file: ${bitmap.width}x${bitmap.height}")
        val rotated = rotateUsingExif(bitmap, imagePath)
        return extractEmbeddingFromBitmap(rotated)
    }

    suspend fun extractEmbeddingFromBitmap(bitmap: Bitmap): FloatArray {
        Log.d(TAG, "Extracting embedding from bitmap: ${bitmap.width}x${bitmap.height}")
        val face = detectPrimaryFace(bitmap)
        if (face == null) {
            Log.e(TAG, "No face detected in the provided bitmap.")
            throw NoFaceDetectedException()
        }
        Log.d(TAG, "Face detected with bounding box: ${face.boundingBox}")


        val cropped = cropFace(bitmap, face.boundingBox, marginFraction = 0.25f)
        Log.d(TAG, "Cropped face bitmap to: ${cropped.width}x${cropped.height}")

        val (inputW, inputH, inputC) = inputImageShape()
        require(inputC == 3) { "Model expects $inputC channels (expected 3)." }
        Log.d(TAG, "Model input shape: $inputW x $inputH x $inputC")


        val scaled = Bitmap.createScaledBitmap(cropped, inputW, inputH, true)
        Log.d(TAG, "Scaled bitmap to model input size: ${scaled.width}x${scaled.height}")


        val embedding = runModel(scaled)
        Log.d(TAG, "Successfully ran model and got embedding.")
        return FaceMath.l2Normalize(embedding)
    }

    fun cosineSimilarity(enrolled: FloatArray, probe: FloatArray): Float {
        return FaceMath.cosineSimilarity(enrolled, probe)
    }

    fun close() {
        try {
            faceDetector.close()
        } catch (_: Throwable) {}
        try {
            interpreter.close()
        } catch (_: Throwable) {}
    }

    private suspend fun detectPrimaryFace(bitmap: Bitmap): Face? {
        Log.d(TAG, "Detecting face in bitmap: ${bitmap.width}x${bitmap.height}")
        val image = InputImage.fromBitmap(bitmap, /* rotationDegrees = */ 0)
        val faces = faceDetector.process(image).await()
        Log.d(TAG, "ML Kit found ${faces.size} faces.")
        return faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
    }

    private fun cropFace(source: Bitmap, faceRect: Rect, marginFraction: Float): Bitmap {
        val marginX = (faceRect.width() * marginFraction).toInt()
        val marginY = (faceRect.height() * marginFraction).toInt()

        val left = (faceRect.left - marginX).coerceAtLeast(0)
        val top = (faceRect.top - marginY).coerceAtLeast(0)
        val right = (faceRect.right + marginX).coerceAtMost(source.width)
        val bottom = (faceRect.bottom + marginY).coerceAtMost(source.height)

        val w = (right - left).coerceAtLeast(1)
        val h = (bottom - top).coerceAtLeast(1)
        Log.d(TAG, "Cropping rect: [l=$left, t=$top, r=$right, b=$bottom], final size: ${w}x${h}")
        return Bitmap.createBitmap(source, left, top, w, h)
    }

    private fun inputImageShape(): Triple<Int, Int, Int> {
        val shape = interpreter.getInputTensor(0).shape()
        Log.d(TAG, "Model input tensor shape: ${shape.contentToString()}")
        // Common shapes: [1, H, W, 3] or [1, W, H, 3] (rare).
        require(shape.size == 4) { "Unexpected input tensor rank: ${shape.contentToString()}" }
        val h = shape[1]
        val w = shape[2]
        val c = shape[3]
        return Triple(w, h, c)
    }

    private fun embeddingSize(): Int {
        val shape = interpreter.getOutputTensor(0).shape()
        require(shape.size == 2 && shape[0] == 1) { "Unexpected output tensor shape: ${shape.contentToString()}" }
        return shape[1]
    }

    private fun runModel(inputBitmap: Bitmap): FloatArray {
        val inputType = interpreter.getInputTensor(0).dataType()
        val (w, h, c) = inputImageShape()
        val inputBuffer = when (inputType) {
            DataType.FLOAT32 -> toFloatInputBuffer(inputBitmap, w, h, c)
            DataType.UINT8 -> toUInt8InputBuffer(inputBitmap, w, h, c)
            else -> throw IllegalStateException("Unsupported model input type: $inputType")
        }

        val out = Array(1) { FloatArray(embeddingSize()) }
        interpreter.run(inputBuffer, out)
        return out[0]
    }

    private fun toFloatInputBuffer(bitmap: Bitmap, w: Int, h: Int, c: Int): ByteBuffer {
        val buffer = ByteBuffer
            .allocateDirect(w * h * c * 4)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        // Common FaceNet-style normalization: (x - 127.5) / 128.0 -> approx [-1, 1]
        val mean = 127.5f
        val std = 128.0f

        for (p in pixels) {
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            buffer.putFloat((r - mean) / std)
            buffer.putFloat((g - mean) / std)
            buffer.putFloat((b - mean) / std)
        }
        buffer.rewind()
        return buffer
    }

    private fun toUInt8InputBuffer(bitmap: Bitmap, w: Int, h: Int, c: Int): ByteBuffer {
        val buffer = ByteBuffer
            .allocateDirect(w * h * c)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        for (p in pixels) {
            buffer.put(((p shr 16) and 0xFF).toByte())
            buffer.put(((p shr 8) and 0xFF).toByte())
            buffer.put((p and 0xFF).toByte())
        }
        buffer.rewind()
        return buffer
    }

    private fun rotateUsingExif(bitmap: Bitmap, imagePath: String): Bitmap {
        val rotation = try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            Log.d(TAG, "EXIF Orientation: $orientation")
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Could not read EXIF data from $imagePath", e)
            0
        }

        if (rotation == 0) {
            Log.d(TAG, "No EXIF rotation needed.")
            return bitmap
        }


        Log.d(TAG, "Applying EXIF rotation of $rotation degrees.")
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        Log.d(TAG, "Rotated bitmap size: ${newBitmap.width}x${newBitmap.height}")
        return newBitmap
    }

    companion object {
        private const val TAG = "FaceEmbeddingExtractor"
    }
}

class NoFaceDetectedException : RuntimeException("No face detected in image.")

class FaceModelUnavailableException(
    cause: Throwable
) : RuntimeException(
    "Face authentication model is not available. Please ensure face_auth.tflite exists in assets and is a valid TensorFlow Lite model.",
    cause
)

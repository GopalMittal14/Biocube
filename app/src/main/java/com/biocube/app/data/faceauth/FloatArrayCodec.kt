package com.biocube.app.data.faceauth
import java.nio.ByteBuffer
import java.nio.ByteOrder
object FloatArrayCodec {
    fun toByteArray(values: FloatArray): ByteArray {
        val buffer = ByteBuffer
            .allocate(values.size * 4)
            .order(ByteOrder.LITTLE_ENDIAN)
        for (v in values) buffer.putFloat(v)
        return buffer.array()
    }

    fun fromByteArray(bytes: ByteArray): FloatArray {
        require(bytes.size % 4 == 0) { "Invalid float byte array length: ${bytes.size}" }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val out = FloatArray(bytes.size / 4)
        for (i in out.indices) out[i] = buffer.getFloat()
        return out
    }
}


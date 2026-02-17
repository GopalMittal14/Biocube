package com.biocube.app.data.service

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    suspend fun authenticate(activity: FragmentActivity): BiometricResult {
        return suspendCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)

            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        continuation.resume(BiometricResult.Error(errString.toString()))
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        continuation.resume(BiometricResult.Success)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        continuation.resume(BiometricResult.Failed)
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate using your biometric credential")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }
}

sealed class BiometricResult {
    object Success : BiometricResult()
    object Failed : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}

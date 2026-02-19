package com.biocube.core.data.worker
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.biocube.core.data.repository.BiometricRepository
import com.biocube.core.util.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: BiometricRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            when (val resource = repository.getBiometricScans().first()) {
                is Resource.Success -> {
                    val scans = resource.data
                    if (!scans.isNullOrEmpty()) {
                        println("Syncing ${scans.size} scans to the server.")
                    }
                    Result.success()
                }
                is Resource.Error -> {
                    Result.retry()
                }
                is Resource.Loading -> {
                    Result.retry()
                }
            }
        } catch (e: IOException) {
            Result.retry()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}

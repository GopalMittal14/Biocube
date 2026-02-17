package com.biocube.app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.biocube.app.domain.repository.ILocationRepository
import com.biocube.app.domain.repository.IUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: ILocationRepository,
    private val userRepository: IUserRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val user = userRepository.getCurrentUserSync()
            
            if (user != null) {
                val result = locationRepository.syncLocations(user.id)
                
                if (result.isSuccess) {
                    Result.success()
                } else {
                    Result.retry()
                }
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "BiocubeSyncWork"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                30, TimeUnit.MINUTES,
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

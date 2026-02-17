package com.biocube.app.di

import android.content.Context
import androidx.room.Room
import com.biocube.app.data.local.BiocubeDatabase
import com.biocube.app.data.local.dao.LocationDao
import com.biocube.app.data.local.dao.UserDao
import com.biocube.app.data.remote.api.BiocubeApi
import com.biocube.app.data.remote.api.MockApiService
import com.biocube.app.data.repository.*
import com.biocube.app.domain.repository.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.biocube.com/") // Placeholder URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideBiocubeApi(
        @ApplicationContext context: Context,
        gson: Gson
    ): BiocubeApi {
        // Using MockApiService instead of Retrofit
        return MockApiService(context, gson)
    }

    @Provides
    @Singleton
    fun provideBiocubeDatabase(@ApplicationContext context: Context): BiocubeDatabase {
        return Room.databaseBuilder(
            context,
            BiocubeDatabase::class.java,
            "biocube_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: BiocubeDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: BiocubeDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        api: BiocubeApi,
        userDao: UserDao
    ): IUserRepository {
        return UserRepository(api, userDao)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        api: BiocubeApi,
        locationDao: LocationDao
    ): ILocationRepository {
        return LocationRepository(api, locationDao)
    }

    @Provides
    @Singleton
    fun provideBiometricRepository(
        api: BiocubeApi
    ): IBiometricRepository {
        return BiometricRepository(api)
    }

    @Provides
    @Singleton
    fun provideServiceRepository(
        api: BiocubeApi
    ): IServiceRepository {
        return ServiceRepository(api)
    }
}

package com.biocube.app.data.local.dao
import androidx.room.*
import com.biocube.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE username = :username AND email = :email LIMIT 1")
    suspend fun getUserByCredentials(username: String, email: String): UserEntity?
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUserSync(): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("UPDATE users SET isFirstLogin = :isFirstLogin WHERE id = :userId")
    suspend fun updateFirstLoginStatus(userId: String, isFirstLogin: Boolean)
    
    @Query("UPDATE users SET lastSyncedAt = :timestamp WHERE id = :userId")
    suspend fun updateLastSyncTime(userId: String, timestamp: Long)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

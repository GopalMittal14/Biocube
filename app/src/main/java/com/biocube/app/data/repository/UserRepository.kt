package com.biocube.app.data.repository

import com.biocube.app.data.local.dao.UserDao
import com.biocube.app.data.local.entity.toDomain
import com.biocube.app.data.local.entity.toEntity
import com.biocube.app.data.remote.api.BiocubeApi
import com.biocube.app.data.remote.dto.LoginRequest
import com.biocube.app.domain.model.User
import com.biocube.app.domain.repository.IUserRepository
import com.biocube.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: BiocubeApi,
    private val userDao: UserDao
) : IUserRepository {

    override suspend fun login(username: String, password: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            
            val response = api.login(LoginRequest(username, password))
            
            if (response.success && response.user != null) {
                val user = User(
                    id = response.user.id,
                    username = response.user.username,
                    email = response.user.email,
                    fullName = response.user.fullName,
                    phoneNumber = response.user.phoneNumber,
                    profileImageUrl = response.user.profileImageUrl,
                    isFirstLogin = response.user.isFirstLogin
                )
                
                userDao.insertUser(user.toEntity())
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toDomain() }
    }

    override suspend fun getCurrentUserSync(): User? {
        return userDao.getCurrentUserSync()?.toDomain()
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun updateFirstLoginStatus(userId: String, isFirstLogin: Boolean) {
        userDao.updateFirstLoginStatus(userId, isFirstLogin)
    }

    override suspend fun logout() {
        userDao.deleteAllUsers()
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
    }
}

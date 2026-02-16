package com.example.weather_app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository internal constructor(private val dao: UserDao) {

    suspend fun register(username: String, password: String): Long = withContext(Dispatchers.IO) {
        dao.insert(User(username = username, password = password))
    }

    suspend fun login(username: String, password: String): User? = withContext(Dispatchers.IO) {
        dao.login(username, password)
    }

    suspend fun getByUsername(username: String): User? = withContext(Dispatchers.IO) {
        dao.getByUsername(username)
    }

    suspend fun ensureDefaultAdmin(defaultUsername: String = "admin", defaultPassword: String = "password") = withContext(Dispatchers.IO) {
        val existing = dao.getByUsername(defaultUsername)
        if(existing == null) {
            dao.insert(User(username = defaultUsername, password = defaultPassword, isAdmin = true))
        } else if (!existing.isAdmin) {
            dao.promoteToAdmin(defaultUsername)
        }
    }

    private suspend fun authenticateAdmin(adminUsername: String, adminPassword: String): User? = withContext(Dispatchers.IO) {
        val admin = dao.login(adminUsername, adminPassword)
        if (admin != null && admin.isAdmin) {
            admin
        } else {
            null
        }
    }

    suspend fun addUserAsAdmin(
        adminUsername: String,
        adminPassword: String,
        newUsername: String,
        newPassword: String,
        isAdmin: Boolean = false
    ): Long? = withContext(Dispatchers.IO) {
        val admin = authenticateAdmin(adminUsername, adminPassword) ?: return@withContext null
        dao.insert(User(username = newUsername, password = newPassword, isAdmin = isAdmin))

    }

    suspend fun deleteUserByUsernameAsAdmin(
        adminUsername: String,
        adminPassword: String,
        targetUsername: String
    ): Int? = withContext(Dispatchers.IO) {
        val admin = authenticateAdmin(adminUsername, adminPassword) ?: return@withContext null
        dao.deleteByUsername(targetUsername)
    }

    suspend fun deleteUserByIdAsAdmin(
        adminUsername: String,
        adminPassword: String,
        targetId: Long
    ): Int? = withContext(Dispatchers.IO) {
        val admin = authenticateAdmin(adminUsername, adminPassword) ?: return@withContext null
        dao.deleteById(targetId)
    }

    suspend fun promoteUserToAdminAsAdmin(
    adminUsername: String,
    adminPassword: String,
    targetUsername: String
    ): Int? = withContext(Dispatchers.IO)
    {
        val admin = authenticateAdmin(adminUsername, adminPassword) ?: return@withContext null
        dao.promoteToAdmin(targetUsername)
    }



    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(
                    UserAppDatabase.getInstance(context).userDao()
                ).also { INSTANCE = it }
            }
    }
}
package com.example.weather_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    @Query("SELECT * FROM users WHERE isAdmin = 1")
    suspend fun getAdmins(): List<User>

    @Query("UPDATE users SET isAdmin = 1 WHERE username = :username")
    suspend fun promoteToAdmin(username: String): Int

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteByUsername(username: String): Int

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long): Int



}
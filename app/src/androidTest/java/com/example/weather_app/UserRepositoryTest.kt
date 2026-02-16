package com.example.weather_app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class UserRepositoryTest {

    private lateinit var db: UserAppDatabase
    private lateinit var dao: UserDao
    private lateinit var repo: UserRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            UserAppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.userDao()
        repo = UserRepository(dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun register_and_login_success() = runBlocking {
        val id = repo.register("alice", "inChains")
        Assert.assertTrue(id > 0)

        val user = repo.login("alice", "inChains")
        Assert.assertNotNull(user)
        Assert.assertEquals("alice", user?.username)
    }

    @Test
    fun duplicate_username_returns_minus_one() = runBlocking {
        repo.register("AverageJoe", "password1")
        val second = repo.register("AverageJoe", "password2")

        Assert.assertEquals(-1L, second)
    }
}

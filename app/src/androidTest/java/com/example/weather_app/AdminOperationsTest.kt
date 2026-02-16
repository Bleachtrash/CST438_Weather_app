package com.example.weather_app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminOperationsTest {

    private lateinit var db: UserAppDatabase
    private lateinit var repo: UserRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            UserAppDatabase::class.java
        ).allowMainThreadQueries().build()

        repo = UserRepository(db.userDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun ensureDefaultAdmin_creates_admin_if_missing() = runBlocking {
        repo.ensureDefaultAdmin("admin", "password")

        val admin = repo.getByUsername("admin")
        Assert.assertNotNull(admin)
        Assert.assertTrue(admin?.isAdmin == true)
    }

    @Test
    fun promoteUserToAdmin_as_admin_succeeds() = runBlocking {
        repo.register("admin", "admin123")
        repo.promoteUserToAdminAsAdmin("admin", "admin123", "admin")

        val user = repo.getByUsername("admin")
        Assert.assertTrue(user?.isAdmin == true)
    }
}

package com.example.weather_app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_app.ui.auth.PasswordHasher
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInFlowTest {

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
    fun signIn_with_hashed_password_succeeds() = runBlocking {
        val hashed = PasswordHasher.sha256("password123")
        repo.register("user1", hashed)

        val user = repo.login("user1", hashed)
        Assert.assertNotNull(user)
    }

    @Test
    fun signIn_wrong_password_fails() = runBlocking {
        val hashed = PasswordHasher.sha256("password123")
        repo.register("user1", hashed)

        val wrongHash = PasswordHasher.sha256("wrong")
        val user = repo.login("user1", wrongHash)

        Assert.assertNull(user)
    }
}

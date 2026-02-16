package com.example.weather_app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: UserAppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, UserAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert_and_getByUsername_and_login() = runBlocking {
        val id = dao.insert(User(username = "NotHackerman", password = "password1"))
        assertTrue(id > 0)

        val byName = dao.getByUsername("NotHackerman")
        assertNotNull(byName)
        assertEquals("NotHackerman", byName?.username)

        val loginUser = dao.login("NotHackerman", "password1")
        assertNotNull(loginUser)
        assertEquals("NotHackerman", loginUser?.username)
    }

    @Test
    fun insert_conflict_ignored_and_existing_is_preserved() = runBlocking {
        val first = User(username = "AverageJoe", password = "RegularJoe")
        val id1 = dao.insert(first)
        assertTrue(id1 > 0)

        val second = User(username = "AverageJoe", password = "NormalJoe")
        val id2 = dao.insert(second)
        val fromDb = dao.getByUsername("AverageJoe")
        assertNotNull(fromDb)
        assertEquals("RegularJoe", fromDb?.password)
    }

    @Test
    fun promoteToAdmin_and_getAdmins_and_delete() = runBlocking {
        dao.insert(User(username = "username1", password = "aPassword"))
        dao.insert(User(username = "username2", password = "anotherPassword"))
        val promoted = dao.promoteToAdmin("username1")
        assertTrue(promoted >= 0)

        val admins = dao.getAdmins()
        assertNotNull(admins)
        val deleted = dao.deleteByUsername("username2")
        assertTrue(deleted >= 0)
    }
}

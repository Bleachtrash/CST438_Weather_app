package com.example.weather_app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather_app.data.local.AppDatabase
import com.example.weather_app.data.local.FavoriteCountyEntity
import com.example.weather_app.data.local.FavoriteDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteDaoInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.favoriteDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun upsert_and_observeFavorites() = runTest {
        dao.upsert(FavoriteCountyEntity("Monterey County, CA", "Monterey County, CA"))
        val list = dao.observeFavorites().first()
        assertEquals(1, list.size)
    }

    @Test
    fun deleteById_removesFavorite() = runTest {
        dao.upsert(FavoriteCountyEntity("Monterey County, CA", "Monterey County, CA"))
        dao.deleteById("Monterey County, CA")
        val list = dao.observeFavorites().first()
        assertEquals(0, list.size)
    }
}

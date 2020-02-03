package co.kyald.coronavirustracking

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.kyald.coronavirustracking.data.database.AppDatabase
import co.kyald.coronavirustracking.data.database.dao.CoronaDao
import co.kyald.coronavirustracking.utils.DataDummy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CoronaRepositoryTest {

    private lateinit var userDao: CoronaDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        userDao = db.coronaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {

        GlobalScope.launch {

            val coronaDummyEntity = DataDummy.generateCoronaEntity()

            val addedID = userDao.save(coronaDummyEntity)

            assertEquals(addedID, 1)

        }
    }
}
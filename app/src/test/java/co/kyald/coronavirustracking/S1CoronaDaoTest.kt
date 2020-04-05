package co.kyald.coronavirustracking

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.kyald.coronavirustracking.data.database.AppDatabase
import co.kyald.coronavirustracking.data.database.dao.chnasia.S1CoronaDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class S1CoronaDaoTest {

    private lateinit var userDaoS1: S1CoronaDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        userDaoS1 = db.coronaDao1()
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

//            val coronaDummyEntity = DataDummy.generateCoronaEntity()
//            val addedID = userDaoS1.save(coronaDummyEntity)
//            assertEquals(addedID, 1)

        }
    }
}
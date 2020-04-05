package co.kyald.coronavirustracking.utils

import android.util.Log
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import java.io.InputStream


object DataDummy {
    private var gson = Gson()

    fun generateCoronaEntity(): S1CoronaEntity =
        gson.fromJson(loadJSON("corona.json"), S1CoronaEntity::class.java)

    private fun loadJSON(fileSource: String): String? {
        var json: String? = null
        try {

            val input: InputStream = this.javaClass.classLoader!!.getResourceAsStream(fileSource) //context.assets.open(fileSource)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            json = String(buffer, charset("UTF-8"))

        } catch (ex: IOException) {
            Timber.e(ex.localizedMessage)
        }

        return json
    }

}
package co.kyald.coronavirustracking.utils

import android.util.Log
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.IOException
import java.io.InputStream


object DataDummy {
    private var gson = Gson()

    fun generateCoronaEntity(): List<CoronaEntity> =
        gson.fromJson(loadJSON("corona.json"), object : TypeToken<List<CoronaEntity>>() {}.type)

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
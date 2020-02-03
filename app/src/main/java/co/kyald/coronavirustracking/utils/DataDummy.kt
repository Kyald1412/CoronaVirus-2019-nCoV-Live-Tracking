package co.kyald.coronavirustracking.utils

import android.util.Log
import co.kyald.coronavirustracking.data.model.CoronaEntity
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream


object DataDummy {
    private var gson = Gson()

    fun generateCoronaEntity() =
        gson.fromJson(loadJSON("corona.json"), CoronaEntity::class.java)

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
            Log.e("Dummy", ex.localizedMessage)
        }

        return json
    }

}
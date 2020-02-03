package co.kyald.coronavirustracking.utils

import android.os.AsyncTask
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class InternetChecker(private val mConsumer: Consumer) : AsyncTask<Void, Void, Boolean>() {
    interface Consumer {
        fun accept(internet: Boolean)
    }

    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            val sock = Socket()
            sock.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    override fun onPostExecute(result: Boolean?) {
        if (result != null) {
            mConsumer.accept(result)
        }
    }
}

package co.kyald.coronavirustracking.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class NotifyWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val coronaRepository: CoronaRepository by inject()
    val mContext = context

    lateinit var coronaOldEntry: List<CoronaEntity.Entry>
    lateinit var coronaNewEntry: List<CoronaEntity.Entry>

    var oldDataCountCase = 0
    var newDataCountCase = 0

    override suspend fun doWork(): Result {

        fetchcoronaData()

        return Result.success()
    }


    fun fetchcoronaData(coroutineContext: CoroutineContext = Dispatchers.IO) {
        
        CoroutineScope(coroutineContext).launch {

            coronaOldEntry = Gson().fromJson(
                coronaRepository.getJsonEntry(),
                Array<CoronaEntity.Entry>::class.java
            ).toList()

            coronaRepository.fetchAll()?.let {
                coronaNewEntry = it.feed.entry

            }

            coronaOldEntry.map {
                oldDataCountCase += it.gsxconfirmedcases.t.toInt()
            }

            coronaNewEntry.map {
                newDataCountCase += it.gsxconfirmedcases.t.toInt()
            }


            if(oldDataCountCase != newDataCountCase)
                sendNotification()

        }
    }

    fun sendNotification() {
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "WorkManager_01"

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WorkManager",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(mContext, channelId)
            .setContentTitle("CoronaVirus (2019-nCoV) Update!!")
            .setContentText("New cases has been confirmed")
            .setSmallIcon(R.mipmap.ic_launcher)

        notificationManager.notify(2, notification.build())
    }
}
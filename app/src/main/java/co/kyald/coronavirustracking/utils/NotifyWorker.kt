package co.kyald.coronavirustracking.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaS1Repository
import co.kyald.coronavirustracking.data.repository.CoronaS2Repository
import co.kyald.coronavirustracking.data.repository.CoronaS3Repository
import co.kyald.coronavirustracking.data.repository.CoronaS4Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext


class NotifyWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val coronaS1Repository: CoronaS1Repository by inject()
    private val coronaS2Repository: CoronaS2Repository by inject()
    private val coronaS3Repository: CoronaS3Repository by inject()
    private val coronaS4Repository: CoronaS4Repository by inject()
    private val preferences: SharedPreferences by inject()

    private val mContext = context

    private var oldDataCountCase = 0
    private var newDataCountCase = 0

    override suspend fun doWork(): Result {

        fetchcoronaData()

        return Result.success()
    }


    private fun fetchcoronaData(coroutineContext: CoroutineContext = Dispatchers.IO) {

        CoroutineScope(coroutineContext).launch {

            when (preferences.getString(
                Constants.PREF_DATA_SOURCE,
                Constants.DATA_SOURCE.DATA_S4.value
            )) {
                Constants.DATA_SOURCE.DATA_S1.value -> {
//                    val s1CoronaOldEntry = Gson().fromJson(
//                        coronaS1Repository.getJsonEntryS1(),
//                        Array<S1CoronaEntity.Entry>::class.java
//                    ).toList()
//
//                    var s1CoronaNewEntry: List<S1CoronaEntity.Entry> = listOf()
//
//                    coronaS1Repository.callNetwork()?.let {
//                        s1CoronaNewEntry = it.feed.entry
//                    }
//
//                    s1CoronaOldEntry.map {
//                        oldDataCountCase += it.gsxconfirmedcases.parsedT().toInt()
//                    }
//
//                    s1CoronaNewEntry.map {
//                        newDataCountCase += it.gsxconfirmedcases.parsedT().toInt()
//                    }
                }

                Constants.DATA_SOURCE.DATA_S2.value -> {


                    val s2CoronaOldEntry: List<S2CoronaEntity> =
                        coronaS2Repository.getCoronaDataS2()
                    var s2CoronaNewEntry: List<S2CoronaEntity> = listOf()

                    coronaS2Repository.callCoronaDataS2().let {
                        if(it.isSuccessful){
                            s2CoronaNewEntry = it.body()!!
                        }
                    }

                    s2CoronaOldEntry.forEach { value ->

                        oldDataCountCase += try {
                            value.stats.confirmed!!.toInt()
                        } catch (nfe: NumberFormatException) {
                            1
                        }

                    }

                    s2CoronaNewEntry.forEach { value ->

                        newDataCountCase += try {
                            value.stats.confirmed!!.toInt()
                        } catch (nfe: NumberFormatException) {
                            1
                        }

                    }
                }

                Constants.DATA_SOURCE.DATA_S3.value -> {


                    val s3CoronaOldEntry: List<S3CoronaEntity> =
                        coronaS3Repository.getCoronaDataS3()
                    var s3CoronaNewEntry: List<S3CoronaEntity> = listOf()

                    coronaS3Repository.callCoronaS3Data().let {
                        if(it.isSuccessful){
                            s3CoronaNewEntry = it.body()!!.features
                        }
                    }

                    s3CoronaOldEntry.forEach { value ->

                        oldDataCountCase += try {
                            value.attributes.attr_confirmed!!.toInt()
                        } catch (nfe: NumberFormatException) {
                            1
                        }

                    }

                    s3CoronaNewEntry.forEach { value ->

                        newDataCountCase += try {
                            value.attributes.attr_confirmed!!.toInt()
                        } catch (nfe: NumberFormatException) {
                            1
                        }

                    }
                }

                Constants.DATA_SOURCE.DATA_S4.value -> {


                    val s4CoronaOldEntry: List<S4CoronaEntity> =
                        coronaS4Repository.getCoronaDataS4()
                    var s4CoronaNewEntry: List<S4CoronaEntity> = listOf()

                    coronaS4Repository.callCoronaS4Data().let {
                        if(it.isSuccessful){
                            s4CoronaNewEntry = it.body()!!
                        }
                    }

                    s4CoronaOldEntry.forEach { value ->

                        oldDataCountCase += try {
                            value.cases!!.toInt()
                        } catch (nfe: NumberFormatException) {
                            1
                        }

                    }

                    s4CoronaNewEntry.forEach { value ->

                        newDataCountCase += try {
                            value.cases!!.toInt()
                        } catch (nfe: NumberFormatException) {
                            1
                        }

                    }
                }

            }



            if (oldDataCountCase != newDataCountCase)
                sendNotification(newDataCountCase - oldDataCountCase)

        }
    }

    private fun sendNotification(total: Int) {
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
            .setContentTitle("Covid19 Update!")
            .setContentText(
                "$total new cases has been confirmed"
            )
            .setSmallIcon(R.mipmap.ic_launcher)

        notificationManager.notify(2, notification.build())
    }
}
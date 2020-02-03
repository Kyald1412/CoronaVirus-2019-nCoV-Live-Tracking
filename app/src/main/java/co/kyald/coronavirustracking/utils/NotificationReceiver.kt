package co.kyald.coronavirustracking.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.ui.feature.mainscreen.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NotificationReceiver(private var context: Context?) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        getReleaseToday(context)
    }

    private fun getReminderTime(type: String): Calendar {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 8
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        return calendar
    }

    private fun getReminderIntent(type: String): Intent {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(EXTRA_TYPE, type)
        return intent
    }

    fun setReleaseTodayReminder() {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_RELEASE_TODAY,
            getReminderIntent(TYPE_RELEASE),
            0
        )
        val alarmManager =
            context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getReminderTime(TYPE_RELEASE).timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun getReleaseToday(context: Context) {
//        val dateFormat =
//            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val date = Date()
//        val now = dateFormat.format(date)
//        val apiService: ApiEndpoints = ApiClient.getClient().create(ApiEndpoints::class.java)
//        val call: Call<MovieResponse> = apiService.getReleasedMovies(now, now)
//        call.enqueue(object : Callback<MovieResponse> {
//            override fun onResponse(
//                call: Call<MovieResponse>,
//                response: Response<MovieResponse>
//            ) {
//                if (response.isSuccessful()) {
//                    val movies: ArrayList<Movie> = response.body().getResults()
//                    var id = 2
//                    for (movie in movies) {
//                        val title: String = movie.getTitle()
//                        val desc =
//                            title + " " + context.getString(R.string.release_reminder_message)
//                        showReleaseToday(context, title, desc, id)
//                        id++
//                    }
//                }
//            }
//
//            override fun onFailure(
//                call: Call<MovieResponse>,
//                t: Throwable
//            ) {
//            }
//        })
    }

    private fun showReleaseToday(
        context: Context,
        title: String,
        desc: String,
        id: Int
    ) {
        val CHANNEL_ID = "Channel_2"
        val CHANNEL_NAME = "Today release channel"
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val uriRingtone =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(desc)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uriRingtone)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification: Notification = mBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.notify(id, notification)
    }

    private fun cancelReminder(context: Context, type: String) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val requestCode = ID_RELEASE_TODAY
        val pendingIntent =
            PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }


    fun cancelReleaseToday(context: Context) {
        cancelReminder(context, TYPE_RELEASE)
    }

    companion object {
        private const val EXTRA_TYPE = "type"
        private const val TYPE_RELEASE = "release"
        private const val ID_RELEASE_TODAY = 1001
    }
}
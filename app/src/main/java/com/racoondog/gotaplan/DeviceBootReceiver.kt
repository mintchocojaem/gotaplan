package com.racoondog.gotaplan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import java.text.SimpleDateFormat
import java.util.*


class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val realm = Realm.getDefaultInstance()
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {


            val subjectData = realm.where<SubjectData>(SubjectData::class.java).findAll()
            val data = subjectData.sort("id", Sort.ASCENDING)
            if(data != null){

                for(i in data.indices){
                    val sharedPreferences =
                        context.getSharedPreferences("alarm", MODE_PRIVATE)
                    val millis = sharedPreferences.getLong(
                        "${data[i]!!.id}",
                        Calendar.getInstance().timeInMillis
                    )
                    val current_calendar = Calendar.getInstance()
                    val nextNotifyTime: Calendar = GregorianCalendar()
                    nextNotifyTime.timeInMillis = sharedPreferences.getLong("${data[i]!!.id}", millis)
                    if (current_calendar.after(nextNotifyTime)) {
                        nextNotifyTime.add(Calendar.DATE, 7)
                    }
                    // on device boot complete, reset the alarm
                    val alarmIntent = Intent(context, AlarmReceiver::class.java)
                    val pendingIntent =
                        PendingIntent.getBroadcast(context, data[i]!!.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    val manager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    //
                    manager.setRepeating(
                        AlarmManager.RTC_WAKEUP, nextNotifyTime.timeInMillis,
                        AlarmManager.INTERVAL_DAY, pendingIntent
                    )
                }

            }

        }
    }
}

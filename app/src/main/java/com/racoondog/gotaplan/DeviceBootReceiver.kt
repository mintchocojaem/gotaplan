package com.racoondog.gotaplan

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.widget.RemoteViews
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

            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val sharedPreferences =
                context.getSharedPreferences("alarm", MODE_PRIVATE)
            val weekInterval = (7 * 24 * 60 * 60 * 1000).toLong()
            if(data != null){


                for(i in data.indices){

                    val id = data[i]!!.id
                    val alarmIntent = Intent(context, AlarmReceiver::class.java)
                    alarmIntent.putExtra("id",id)
                    alarmIntent.putExtra("title",data[i]!!.title)
                    val pendingIntent =
                        PendingIntent.getBroadcast(context, id, alarmIntent, FLAG_UPDATE_CURRENT)

                    if(data[i]!!.notification == -1){
                        val editor =
                            context.getSharedPreferences("alarm", MODE_PRIVATE)
                                .edit()
                        val am =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        am.cancel(pendingIntent)
                        editor.remove("$id")
                        editor.apply()
                    }else{
                        val calendar = Calendar.getInstance()

                        calendar.timeInMillis = sharedPreferences.getLong("$id", 0L)

                        if (calendar.before(Calendar.getInstance())) {
                            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정

                            calendar.add(Calendar.DATE, 7)

                            alarmMgr.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis, weekInterval, pendingIntent)

                        }else{
                            alarmMgr.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis, weekInterval, pendingIntent)
                        }

                        val editor = context.getSharedPreferences("alarm", MODE_PRIVATE).edit()
                        editor.putLong("$id", calendar.timeInMillis)
                        editor.apply()
                    }
                }

            }

        }

        val cal = Calendar.getInstance()
        var date = 0
        val dayFlag = cal.get(Calendar.DAY_OF_WEEK)
        when(dayFlag){
            1 -> date = 7
            2 -> date = 1
            3 -> date = 2
            4 -> date = 3
            5 -> date = 4
            6 -> date = 5
            7 -> date = 6
        }

        var subjectData = realm.where(ScheduleData::class.java).equalTo("id",AppStorage(context).getWidgetScheduleID())
            .findFirst()?.subjectData?.where()
            ?.equalTo("dayFlag", date)
            ?.findAll()
        if ( subjectData == null){
            val initScheduleData = realm.where(ScheduleData::class.java).findFirst()!!
            val initSubjectData = initScheduleData.subjectData.where()
                .equalTo("dayFlag", date)
                .findAll()

            if (initSubjectData != null){
                subjectData = initSubjectData
                AppStorage(context).setWidgetScheduleID(initScheduleData.id)
                val sortedDate = subjectData.sort("startHour", Sort.ASCENDING).sort("endHour", Sort.ASCENDING)

                //Toast.makeText(context,"$date",Toast.LENGTH_LONG).show()
                AppStorage(context).setWidgetSubjectList(sortedDate)
            }

        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val smallAppWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                SmallScheduleWidget::class.java
            )
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(
            smallAppWidgetIds,
            R.id.small_schedule_widget_listview
        )

        val largeAppWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                LargeScheduleWidget::class.java
            )
        )

        appWidgetManager.notifyAppWidgetViewDataChanged(
            largeAppWidgetIds,
            R.id.large_schedule_widget_listview
        )

        val data = realm.where(ScheduleData::class.java)
            .equalTo("id",AppStorage(context).getWidgetScheduleID()).findFirst()
            ?: realm.where(ScheduleData::class.java).findFirst()!!

        AppStorage(context).setWidgetScheduleList(data)

        val largeWidget = RemoteViews(context.packageName, R.layout.large_schedule_widget)
        val smallWidget = RemoteViews(context.packageName, R.layout.small_schedule_widget)

        val largeIntent = Intent(context, LargeScheduleWidget::class.java)
        val smallIntent = Intent(context, SmallScheduleWidget::class.java)

        largeIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        smallIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.

        //You need to specify a proper flag for the intent. Or else the intent will become deleted.
        context.sendBroadcast(largeIntent)
        context.sendBroadcast(smallIntent)

        largeWidget.setTextViewText(R.id.large_schedule_widget_title, data.title)
        largeWidget.setTextViewText(R.id.small_schedule_widget_title,  data.title)

        appWidgetManager.updateAppWidget(largeAppWidgetIds, largeWidget)
        appWidgetManager.updateAppWidget(smallAppWidgetIds, smallWidget)


    }

}

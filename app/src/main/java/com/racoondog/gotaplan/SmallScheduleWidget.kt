package com.racoondog.gotaplan

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.realm.Realm
import io.realm.Sort
import java.text.SimpleDateFormat
import java.util.*


class SmallScheduleWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // There may be multiple widgets active, so update all of them

        // RemoteViewsService 실행 등록시키는 함수

        val currentTime: Date = Calendar.getInstance().getTime()
        val n: String = Locale.getDefault().displayLanguage

        val date = if (n.compareTo("한국어") == 0){
            SimpleDateFormat("MM월 dd일 EE요일", Locale.getDefault()).format(currentTime)

        } else {
            SimpleDateFormat("MM.dd EE", Locale.getDefault()).format(currentTime)
        }
        val serviceIntent = Intent(context, SmallScheduleWidgetRemoteViewsService::class.java)
        val widget = RemoteViews(context.packageName, R.layout.small_schedule_widget)
        widget.setRemoteAdapter(R.id.small_schedule_widget_listview, serviceIntent)
        widget.setTextViewText(R.id.small_schedule_widget_date, date)
        widget.setTextViewText(R.id.small_schedule_widget_title, AppStorage(context).getWidgetScheduleList()!![0]?.title?:"title")


        updateAppWidget(context, appWidgetManager, appWidgetIds)


        // 나중에 오늘은 일정이 없네요! 할때 활용하면 될듯
        /*
        val data = context!!.getSharedPreferences("app_storage", RemoteViewsService.MODE_PRIVATE)
            .getString("data", "")
        val makeGson = GsonBuilder().create()
        val listType : TypeToken<MutableList<WidgetItem?>> = object : TypeToken<MutableList<WidgetItem?>>() {}
        val count:MutableList<WidgetItem> = makeGson.fromJson(data,listType.type)
        if (count.size == 0) widget.setTextViewText(R.id.widget_title,"gg")
        else widget.setTextViewText(R.id.widget_title,"ok")

         */

        appWidgetManager.updateAppWidget(appWidgetIds, widget)
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

}

private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: IntArray) {

    //여기부분 다 사용할 일 없어져서 주석처리함!
    // Construct the RemoteViews object

    val views = RemoteViews(context.packageName, R.layout.small_schedule_widget)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, SmallScheduleWidget::class.java))

    /*
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)

     */
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
    val realm = Realm.getDefaultInstance()
    val initScheduleData = realm.where(ScheduleData::class.java).findFirst()!!
    var subjectData = realm.where(ScheduleData::class.java)?.equalTo("id",AppStorage(context).getWidgetScheduleID())
        ?.findFirst()?.subjectData?.where()
        ?.equalTo("dayFlag", date)
        ?.findAll()
    if ( subjectData == null){
        val initSubjectData = initScheduleData.subjectData.where()
            ?.equalTo("dayFlag", date)
            ?.findAll()
        if(initSubjectData != null){
            subjectData = initSubjectData
        }

    }
    if (subjectData != null){
        AppStorage(context).setWidgetScheduleID(initScheduleData.id)
        val sortedDate = subjectData.sort("startHour", Sort.ASCENDING).sort("endHour", Sort.ASCENDING)
        //Toast.makeText(context,"$date",Toast.LENGTH_LONG).show()
        AppStorage(context).setWidgetSubjectList(sortedDate)
        views.setTextViewText(R.id.small_schedule_widget_title, AppStorage(context).getWidgetScheduleList()!![0]?.title?:"title")
    }


    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.small_schedule_widget_listview)
    appWidgetManager.updateAppWidget(appWidgetId, views)


}

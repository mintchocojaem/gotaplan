package com.racoondog.gotaplan

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    /**
     * 위젯이 바탕화면에 설치될 때마다 호출되는 함수
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        // RemoteViewsService 실행 등록시키는 함수
        val serviceIntent = Intent(context, MyRemoteViewsService::class.java)
        val widget = RemoteViews(context.getPackageName(), R.layout.new_app_widget)
        widget.setRemoteAdapter(R.id.widget_listview, serviceIntent)
        //widget.setTextViewText(R.id.widget_title,"ok")
        //        클릭이벤트 인텐트 유보.
        //보내기
        appWidgetManager.updateAppWidget(appWidgetIds, widget)
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        /**
         * 위젯의 크기 및 옵션이 변경될 때마다 호출되는 함수
         * @param context
         * @param appWidgetManager
         * @param appWidgetId
         */
        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            //여기부분 다 사용할 일 없어져서 주석처리함!
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
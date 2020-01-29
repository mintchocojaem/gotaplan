package com.racoondog.mystudent

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.widget.RemoteViews


/**
 * Implementation of App Widget functionality.
 */
class ScheduleWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

var bitmap:Int = 0

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int){

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.schedule_widget)
    views.setImageViewResource(R.id.widget_view, bitmap)
    val intent = Intent(context, MainActivity::class.java)
    val pe = PendingIntent.getActivity(context, 0, intent, 0)
    //views.setOnClickPendingIntent(R.id.open_app_btn, pe)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

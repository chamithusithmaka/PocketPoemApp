package com.example.act3

import PoemDataHelper
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

class PoemWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Fetch the poem topic from SharedPreferences
            val sharedPrefHelper = PoemDataHelper(context)
            val poemTopic = sharedPrefHelper.getLatestPoemTopic() ?: "New Poem Challenge"

            // Set the poem topic in the widget
            views.setTextViewText(R.id.widget_poem_topic, poemTopic)

            // Set up PendingIntent to open the app when the widget is clicked
            val intent = Intent(context, poemchallange::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_submit_button, pendingIntent)

            // Finally, update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == "UPDATE_WIDGET") {
            val appWidgetManager = AppWidgetManager.getInstance(context!!)
            val componentName = ComponentName(context, PoemWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}

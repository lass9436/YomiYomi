package com.lass.yomiyomi.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.lass.yomiyomi.MainActivity
import com.lass.yomiyomi.R
import com.lass.yomiyomi.domain.model.constant.StudyStatus
import com.lass.yomiyomi.data.repository.StudyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WidgetStreakProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val repo = StudyRepository(context)
            val records = repo.getRecentRecords()
            val streak = repo.getCurrentStreak()

            Log.d("WidgetStreakProvider", "Updating widget with streak: $streak")

            withContext(Dispatchers.Main) {
                for (appWidgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.widget_streak)

                    views.setTextViewText(R.id.title, "🔥 $streak days")

                    val iconIds = listOf(
                        R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4, R.id.icon5
                    )

                    val iconsToSet = mutableListOf<Int>()
                    for (i in 0 until 5) {
                        if (i < records.size) {
                            val record = records[i]
                            val iconRes = when (record.status) {
                                StudyStatus.STUDIED -> R.drawable.ic_star_yellow
                                StudyStatus.NOT_STUDIED -> R.drawable.ic_dot_yellow
                                StudyStatus.ITEM_USED -> R.drawable.ic_check_pink
                            }
                            iconsToSet.add(iconRes)
                        } else {
                            iconsToSet.add(R.drawable.ic_dot_yellow)
                        }
                    }

                    iconsToSet.forEachIndexed { index, iconRes ->
                        views.setImageViewResource(iconIds[index], iconRes)
                    }

                    val intent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}

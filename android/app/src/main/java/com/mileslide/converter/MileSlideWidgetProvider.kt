package com.mileslide.converter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.round

class MileSlideWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_DECREMENT -> {
                val current = Prefs.getKm(context)
                val next = max(0.0, round((current - 1.0) * 10.0) / 10.0)
                Prefs.setKm(context, next)
                updateAllWidgets(context)
            }
            ACTION_INCREMENT -> {
                val current = Prefs.getKm(context)
                val next = round((current + 1.0) * 10.0) / 10.0
                Prefs.setKm(context, next)
                updateAllWidgets(context)
            }
            ACTION_TOGGLE_THEME -> {
                val dark = Prefs.isDarkMode(context)
                Prefs.setDarkMode(context, !dark)
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_DECREMENT = "com.mileslide.converter.ACTION_DECREMENT"
        const val ACTION_INCREMENT = "com.mileslide.converter.ACTION_INCREMENT"
        const val ACTION_TOGGLE_THEME = "com.mileslide.converter.ACTION_TOGGLE_THEME"

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MileSlideWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateWidget(context, manager, id)
            }
        }

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val km = Prefs.getKm(context)
            val mi = Prefs.kmToMiles(km)
            val darkMode = Prefs.isDarkMode(context)

            val views = RemoteViews(context.packageName, R.layout.widget_mileslide)

            // Apply Dark or Light widget appearance
            views.setInt(
                R.id.widget_container,
                "setBackgroundResource",
                if (darkMode) R.drawable.widget_bg_dark else R.drawable.widget_bg_light
            )

            views.setTextViewText(R.id.txt_km_value, Prefs.formatOneDecimal(km))
            views.setTextViewText(R.id.txt_mi_value, Prefs.formatOneDecimal(mi))

            views.setTextColor(
                R.id.txt_km_value,
                ContextCompat.getColor(
                    context,
                    if (darkMode) R.color.dark_text_primary else R.color.light_text_primary
                )
            )
            views.setTextColor(
                R.id.txt_mi_value,
                ContextCompat.getColor(
                    context,
                    if (darkMode) R.color.cyan_accent_dark else R.color.cyan_accent_light
                )
            )
            val unitColor = ContextCompat.getColor(
                context,
                if (darkMode) R.color.dark_text_muted else R.color.light_text_muted
            )
            views.setTextColor(R.id.txt_km_unit, unitColor)
            views.setTextColor(R.id.txt_mi_unit, unitColor)

            views.setImageViewResource(
                R.id.btn_theme,
                if (darkMode) R.drawable.ic_sun else R.drawable.ic_moon
            )

            // Render crisp center tick ruler bitmap
            val rulerBitmap = RulerBitmapRenderer.render(context, km, darkMode)
            views.setImageViewBitmap(R.id.img_ruler, rulerBitmap)

            // PendingIntents for widget buttons
            views.setOnClickPendingIntent(
                R.id.btn_minus,
                broadcastPendingIntent(context, ACTION_DECREMENT, 101)
            )
            views.setOnClickPendingIntent(
                R.id.btn_plus,
                broadcastPendingIntent(context, ACTION_INCREMENT, 102)
            )
            views.setOnClickPendingIntent(
                R.id.btn_theme,
                broadcastPendingIntent(context, ACTION_TOGGLE_THEME, 103)
            )

            // Tap km or mi to open floating numeric entry dialog right over home screen
            views.setOnClickPendingIntent(
                R.id.group_km,
                PendingIntent.getActivity(
                    context,
                    201,
                    EnterValueActivity.createIntent(context, EnterValueActivity.UNIT_KM),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            views.setOnClickPendingIntent(
                R.id.group_mi,
                PendingIntent.getActivity(
                    context,
                    202,
                    EnterValueActivity.createIntent(context, EnterValueActivity.UNIT_MI),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Tap center ruler to open interactive slide screen
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            views.setOnClickPendingIntent(
                R.id.img_ruler,
                PendingIntent.getActivity(
                    context,
                    203,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun broadcastPendingIntent(
            context: Context,
            action: String,
            requestCode: Int
        ): PendingIntent {
            val intent = Intent(context, MileSlideWidgetProvider::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}

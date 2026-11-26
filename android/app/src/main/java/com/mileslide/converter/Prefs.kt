package com.mileslide.converter

import android.content.Context
import java.util.Locale
import kotlin.math.max
import kotlin.math.round

object Prefs {
    private const val PREFS_NAME = "mileslide_prefs"
    private const val KEY_KM = "km_value"
    private const val KEY_DARK_MODE = "dark_mode"

    const val MI_TO_KM = 1.609344
    const val KM_TO_MI = 1.0 / MI_TO_KM

    fun getKm(context: Context): Double {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_KM, 10.0f).toDouble().coerceAtLeast(0.0)
    }

    fun setKm(context: Context, km: Double) {
        val clamped = max(0.0, km)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_KM, clamped.toFloat())
            .apply()
    }

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, true)
    }

    fun setDarkMode(context: Context, dark: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, dark)
            .apply()
    }

    fun kmToMiles(km: Double): Double = km * KM_TO_MI

    fun milesToKm(miles: Double): Double = miles * MI_TO_KM

    fun formatOneDecimal(value: Double): String {
        val rounded = round(value * 10.0) / 10.0
        val clean = if (rounded == 0.0) 0.0 else rounded
        return String.format(Locale.US, "%.1f", clean)
    }
}

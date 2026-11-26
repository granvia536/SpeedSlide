package com.mileslide.converter

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private lateinit var rootContainer: FrameLayout
    private lateinit var widgetBar: LinearLayout
    private lateinit var txtKmValue: TextView
    private lateinit var txtKmUnit: TextView
    private lateinit var txtMiValue: TextView
    private lateinit var txtMiUnit: TextView
    private lateinit var btnMinus: ImageView
    private lateinit var btnPlus: ImageView
    private lateinit var btnTheme: ImageView
    private lateinit var rulerView: InteractiveRulerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootContainer = findViewById(R.id.root_container)
        widgetBar = findViewById(R.id.widget_bar)
        txtKmValue = findViewById(R.id.txt_km_value)
        txtKmUnit = findViewById(R.id.txt_km_unit)
        txtMiValue = findViewById(R.id.txt_mi_value)
        txtMiUnit = findViewById(R.id.txt_mi_unit)
        btnMinus = findViewById(R.id.btn_minus)
        btnPlus = findViewById(R.id.btn_plus)
        btnTheme = findViewById(R.id.btn_theme)
        rulerView = findViewById(R.id.interactive_ruler)

        btnMinus.setOnClickListener {
            val next = max(0.0, round((Prefs.getKm(this) - 1.0) * 10.0) / 10.0)
            Prefs.setKm(this, next)
            syncUi()
            MileSlideWidgetProvider.updateAllWidgets(this)
        }

        btnPlus.setOnClickListener {
            val next = round((Prefs.getKm(this) + 1.0) * 10.0) / 10.0
            Prefs.setKm(this, next)
            syncUi()
            MileSlideWidgetProvider.updateAllWidgets(this)
        }

        findViewById<LinearLayout>(R.id.group_km).setOnClickListener {
            startActivity(EnterValueActivity.createIntent(this, EnterValueActivity.UNIT_KM))
        }

        findViewById<LinearLayout>(R.id.group_mi).setOnClickListener {
            startActivity(EnterValueActivity.createIntent(this, EnterValueActivity.UNIT_MI))
        }

        btnTheme.setOnClickListener {
            Prefs.setDarkMode(this, !Prefs.isDarkMode(this))
            syncUi()
            MileSlideWidgetProvider.updateAllWidgets(this)
        }

        rulerView.onValueChange = { nextKm ->
            Prefs.setKm(this, nextKm)
            updateNumbersOnly(nextKm)
            MileSlideWidgetProvider.updateAllWidgets(this)
        }
    }

    override fun onResume() {
        super.onResume()
        syncUi()
    }

    private fun updateNumbersOnly(km: Double) {
        txtKmValue.text = Prefs.formatOneDecimal(km)
        txtMiValue.text = Prefs.formatOneDecimal(Prefs.kmToMiles(km))
    }

    private fun syncUi() {
        val km = Prefs.getKm(this)
        val darkMode = Prefs.isDarkMode(this)

        updateNumbersOnly(km)
        rulerView.valueKm = km
        rulerView.darkMode = darkMode

        if (darkMode) {
            rootContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_bg))
            window.statusBarColor = ContextCompat.getColor(this, R.color.dark_bg)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.dark_bg)
            widgetBar.setBackgroundResource(R.drawable.widget_bg_dark)
            txtKmValue.setTextColor(ContextCompat.getColor(this, R.color.dark_text_primary))
            txtMiValue.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent_dark))
            val unitColor = ContextCompat.getColor(this, R.color.dark_text_muted)
            txtKmUnit.setTextColor(unitColor)
            txtMiUnit.setTextColor(unitColor)
            btnTheme.setImageResource(R.drawable.ic_sun)
        } else {
            rootContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.light_bg))
            window.statusBarColor = ContextCompat.getColor(this, R.color.light_bg)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.light_bg)
            widgetBar.setBackgroundResource(R.drawable.widget_bg_light)
            txtKmValue.setTextColor(ContextCompat.getColor(this, R.color.light_text_primary))
            txtMiValue.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent_light))
            val unitColor = ContextCompat.getColor(this, R.color.light_text_muted)
            txtKmUnit.setTextColor(unitColor)
            txtMiUnit.setTextColor(unitColor)
            btnTheme.setImageResource(R.drawable.ic_moon)
        }
    }
}

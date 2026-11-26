package com.mileslide.converter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.math.max

class EnterValueActivity : AppCompatActivity() {

    private var unit: String = UNIT_KM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_value)

        unit = intent.getStringExtra(EXTRA_UNIT) ?: UNIT_KM
        val darkMode = Prefs.isDarkMode(this)
        val currentKm = Prefs.getKm(this)

        val container = findViewById<LinearLayout>(R.id.dialog_container)
        val titleView = findViewById<TextView>(R.id.dialog_title)
        val editValue = findViewById<EditText>(R.id.edit_value)
        val unitView = findViewById<TextView>(R.id.dialog_unit)
        val previewView = findViewById<TextView>(R.id.txt_converted_preview)
        val btnCancel = findViewById<TextView>(R.id.btn_cancel)
        val btnSet = findViewById<TextView>(R.id.btn_set)

        val isKm = unit == UNIT_KM
        titleView.text = getString(if (isKm) R.string.enter_km_title else R.string.enter_mi_title)
        unitView.text = getString(if (isKm) R.string.unit_km else R.string.unit_mi)

        if (darkMode) {
            container.setBackgroundResource(R.drawable.dialog_bg_dark)
            titleView.setTextColor(Color.WHITE)
            editValue.setTextColor(Color.WHITE)
            unitView.setTextColor(ContextCompat.getColor(this, R.color.dark_text_muted))
            previewView.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent_dark))
            btnCancel.setTextColor(Color.parseColor("#8892A4"))
            btnSet.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent_dark))
        } else {
            container.setBackgroundResource(R.drawable.dialog_bg_light)
            titleView.setTextColor(ContextCompat.getColor(this, R.color.light_text_primary))
            editValue.setTextColor(ContextCompat.getColor(this, R.color.light_text_primary))
            unitView.setTextColor(ContextCompat.getColor(this, R.color.light_text_muted))
            previewView.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent_light))
            btnCancel.setTextColor(Color.parseColor("#64748B"))
            btnSet.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent_light))
        }

        val initialNumber = if (isKm) currentKm else Prefs.kmToMiles(currentKm)
        editValue.setText(Prefs.formatOneDecimal(initialNumber))
        editValue.setSelection(0, editValue.text.length)

        fun updatePreview(raw: String) {
            val parsed = raw.replace(',', '.').toDoubleOrNull()
            if (parsed != null && parsed >= 0.0) {
                val converted = if (isKm) Prefs.kmToMiles(parsed) else Prefs.milesToKm(parsed)
                val targetUnit = if (isKm) getString(R.string.unit_mi) else getString(R.string.unit_km)
                previewView.text = "= ${Prefs.formatOneDecimal(converted)} $targetUnit"
                btnSet.isEnabled = true
                btnSet.alpha = 1f
            } else {
                val targetUnit = if (isKm) getString(R.string.unit_mi) else getString(R.string.unit_km)
                previewView.text = "= — $targetUnit"
                btnSet.isEnabled = false
                btnSet.alpha = 0.4f
            }
        }

        updatePreview(editValue.text.toString())

        editValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePreview(s?.toString().orEmpty())
            }
        })

        fun commitAndFinish() {
            val parsed = editValue.text.toString().replace(',', '.').toDoubleOrNull() ?: return
            if (parsed < 0.0) return
            val nextKm = max(0.0, if (isKm) parsed else Prefs.milesToKm(parsed))
            Prefs.setKm(this, nextKm)
            MileSlideWidgetProvider.updateAllWidgets(this)
            finish()
        }

        editValue.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                commitAndFinish()
                true
            } else {
                false
            }
        }

        btnCancel.setOnClickListener { finish() }
        btnSet.setOnClickListener { commitAndFinish() }

        editValue.requestFocus()
    }

    companion object {
        const val EXTRA_UNIT = "extra_unit"
        const val UNIT_KM = "km"
        const val UNIT_MI = "mi"

        fun createIntent(context: Context, unit: String): Intent {
            return Intent(context, EnterValueActivity::class.java).apply {
                putExtra(EXTRA_UNIT, unit)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
    }
}

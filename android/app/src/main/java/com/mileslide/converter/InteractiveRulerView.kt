package com.mileslide.converter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.round

class InteractiveRulerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var valueKm: Double = 10.0
        set(value) {
            field = max(0.0, value)
            invalidate()
        }

    var darkMode: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var onValueChange: ((Double) -> Unit)? = null

    private var startX = 0f
    private var startKm = 10.0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val density = resources.displayMetrics.density
        RulerBitmapRenderer.drawRuler(
            canvas = canvas,
            width = width.toFloat(),
            height = height.toFloat(),
            density = density,
            valueKm = valueKm,
            darkMode = darkMode
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val density = resources.displayMetrics.density
        val pxPerKm = 5.0f * density

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                startX = event.x
                startKm = valueKm
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - startX
                val nextKm = max(0.0, round((startKm - dx / pxPerKm) * 10.0) / 10.0)
                if (nextKm != valueKm) {
                    valueKm = nextKm
                    onValueChange?.invoke(nextKm)
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

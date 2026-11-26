package com.mileslide.converter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

object RulerBitmapRenderer {
    fun render(context: Context, valueKm: Double, darkMode: Boolean): Bitmap {
        val density = context.resources.displayMetrics.density
        val widthPx = (112f * density).toInt().coerceAtLeast(160)
        val heightPx = (40f * density).toInt().coerceAtLeast(64)

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawRuler(canvas, widthPx.toFloat(), heightPx.toFloat(), density, valueKm, darkMode)
        return bitmap
    }

    fun drawRuler(
        canvas: Canvas,
        width: Float,
        height: Float,
        density: Float,
        valueKm: Double,
        darkMode: Boolean
    ) {
        val pxPerKm = 5.0f * density
        val centerX = width / 2f
        val bottomY = height - 4f * density

        val majorColor = if (darkMode) Color.parseColor("#5E687B") else Color.parseColor("#64748B")
        val medColor = if (darkMode) Color.parseColor("#414A5C") else Color.parseColor("#94A3B8")
        val minorColor = if (darkMode) Color.parseColor("#2A3140") else Color.parseColor("#CBD5E1")
        val labelColor = if (darkMode) Color.parseColor("#6B768A") else Color.parseColor("#64748B")
        val needleColor = if (darkMode) Color.parseColor("#5A6A82") else Color.parseColor("#0EA5E9")

        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 1.1f * density
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = labelColor
            textSize = 9f * density
            textAlign = Paint.Align.CENTER
        }
        val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = needleColor
            strokeWidth = 1.6f * density
        }

        val saveLayer = canvas.saveLayer(0f, 0f, width, height, null)

        val halfSpanKm = (width / 2f) / pxPerKm + 2.0
        val minTick = max(0, floor(valueKm - halfSpanKm).toInt())
        val maxTick = ceil(valueKm + halfSpanKm).toInt()

        for (t in minTick..maxTick) {
            val x = centerX + ((t - valueKm) * pxPerKm).toFloat()
            val isMajor = t % 10 == 0
            val isMedium = !isMajor && t % 5 == 0

            val tickHeight = when {
                isMajor -> 15f * density
                isMedium -> 10f * density
                else -> 6f * density
            }
            tickPaint.color = when {
                isMajor -> majorColor
                isMedium -> medColor
                else -> minorColor
            }

            canvas.drawLine(x, bottomY, x, bottomY - tickHeight, tickPaint)

            if (isMajor) {
                canvas.drawText(t.toString(), x, bottomY - tickHeight - 3f * density, textPaint)
            }
        }

        // Center hairline indicator
        canvas.drawLine(centerX, 3f * density, centerX, height - 3f * density, needlePaint)

        // Smooth edge fade mask on left & right
        val fadePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            shader = LinearGradient(
                0f, 0f, width, 0f,
                intArrayOf(Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.TRANSPARENT),
                floatArrayOf(0f, 0.22f, 0.78f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width, height, fadePaint)
        canvas.restoreToCount(saveLayer)
    }
}

package com.example.weatherapp.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class WindCompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var windSpeed: String = "9.7"
    private var windUnit: String = "km/h"
    private var windDirectionDegrees: Float = 270f // e.g., pointing West

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66FFFFFF // Semi-transparent white for ticks
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val speedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
        textSize = 72f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
        textSize = 36f
        typeface = Typeface.DEFAULT
    }

    fun setWindData(speed: String, unit: String, degrees: Float) {
        windSpeed = speed
        windUnit = unit
        windDirectionDegrees = degrees
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) * 0.85f

        // 1. Draw Compass Tick Marks (Degree lines around the circle)
        for (i in 0 until 60) {
            val angle = i * 6f * (Math.PI / 180f).toFloat()
            val isMajor = i % 5 == 0
            val tickLength = if (isMajor) 16f else 8f
            tickPaint.strokeWidth = if (isMajor) 3f else 1.5f

            val startX = centerX + (radius - tickLength) * sin(angle)
            val startY = centerY - (radius - tickLength) * cos(angle)
            val endX = centerX + radius * sin(angle)
            val endY = centerY - radius * cos(angle)

            canvas.drawLine(startX, startY, endX, endY, tickPaint)
        }

        // 2. Draw Cardinal Directions & Pointers (N, W, E)
        textPaint.textSize = 40f

        // North (N) at top (0 degrees)
        canvas.drawText("N", centerX, centerY - radius + 40f, textPaint)
        // Small directional arrow above N
        canvas.drawCircle(centerX, centerY - radius + 10f, 3f, textPaint)

        // West (W) on the left (270 degrees) with pointing arrow
        canvas.drawText("W", centerX - radius + 30f, centerY + 15f, textPaint)
        // Draw custom arrow indicator for West
        canvas.drawLine(centerX - radius - 20f, centerY, centerX - radius + 10f, centerY, tickPaint)

        // East (E) on the right (90 degrees) with marker
        canvas.drawText("E", centerX + radius - 30f, centerY + 15f, textPaint)
        canvas.drawLine(centerX + radius - 10f, centerY, centerX + radius + 20f, centerY, tickPaint)
        canvas.drawCircle(centerX + radius + 15f, centerY, 4f, tickPaint)

        // 3. Draw Center Speed Text and Unit
        canvas.drawText(windSpeed, centerX, centerY - 5f, speedPaint)
        canvas.drawText(windUnit, centerX, centerY + 40f, unitPaint)
    }
}
package com.example.weatherapp.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.Path
import kotlin.math.sin

class SunriseArcView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress: Float = 0.3f // 0.0 (sunrise) to 1.0 (sunset)

    private val horizonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFA0A0A5.toInt()
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF1B315E.toInt() // Dark blue arc track
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66D4D4F7 // Semi-transparent glow effect around the sun thumb
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE8E8FF.toInt() // White/light purple sun indicator
        style = Paint.Style.FILL
    }

    /**
     * Updates the sun's position along the arc.
     * @param value Float between 0.0 and 1.0
     */
    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 1f)
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerY = height / 2f

        // 1. Draw the horizontal baseline/horizon
        canvas.drawLine(0f, centerY, width, centerY, horizonPaint)

        // 2. Draw the sine-wave arc path using a cubic bezier curve
        val path = Path().apply {
            moveTo(0f, centerY)
            cubicTo(
                width * 0.25f, -centerY * 0.8f,
                width * 0.75f, -centerY * 0.8f,
                width, centerY
            )
        }
        canvas.drawPath(path, arcPaint)

        // 3. Calculate current sun indicator coordinates based on progress (FIXED MULTIPLICATION SIGN)
        val currentX = width * progress
        val currentY = centerY - (sin(progress * Math.PI).toFloat() * (centerY * 0.8f))

        // 4. Draw the glowing halo and the sun indicator circle
        canvas.drawCircle(currentX, currentY, 24f, glowPaint)
        canvas.drawCircle(currentX, currentY, 10f, thumbPaint)
    }
}
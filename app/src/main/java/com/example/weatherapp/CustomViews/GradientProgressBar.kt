package com.example.weatherapp.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class GradientProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var max: Float = 100f
    private var progress: Float = 60f

    // Lock the bar height strictly to 6dp
    private val barHeightPx = 6f * resources.displayMetrics.density

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x30000000 // Semi-transparent background track
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt() // Solid white thumb
        style = Paint.Style.FILL
    }

    private val trackRect = RectF()
    private val progressRect = RectF()

    fun setProgress(current: Float, maxValue: Float = 100f) {
        progress = current.coerceIn(0f, maxValue)
        max = maxValue
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (barHeightPx * 2.5f).toInt() // Gives room for the thumb circle
        setMeasuredDimension(
            resolveSize(suggestedMinimumWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            // Setup gradient colors across the width of the view
            progressPaint.shader = LinearGradient(
                0f, 0f, w.toFloat(), 0f,
                intArrayOf(0xFF3B5EE8.toInt(), 0xFF9C49C5.toInt(), 0xFFE93B81.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val top = (height - barHeightPx) / 2f
        val bottom = top + barHeightPx
        val cornerRadius = barHeightPx / 2f

        // 1. Draw background track
        trackRect.set(0f, top, width, bottom)
        canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, trackPaint)

        // 2. Draw gradient progress fill
        val progressRatio = (progress / max).coerceIn(0f, 1f)
        val filledWidth = width * progressRatio

        if (filledWidth > 0f) {
            progressRect.set(0f, top, filledWidth, bottom)
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint)

            // 3. Draw white thumb indicator at the edge
            val thumbRadius = barHeightPx * 0.9f
            val cx = filledWidth.coerceIn(thumbRadius, width - thumbRadius)
            val cy = height / 2f
            canvas.drawCircle(cx, cy, thumbRadius, thumbPaint)
        }
    }
}
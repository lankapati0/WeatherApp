package com.example.weatherapp.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class UvProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var max: Float = 12f
    private var progress: Float = 4f

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x30000000 // Semi-transparent track
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
    }

    private val trackRect = RectF()
    private val progressRect = RectF()

    fun setProgress(current: Float, maxValue: Float = 12f) {
        progress = current.coerceIn(0f, maxValue)
        max = maxValue
        invalidate()
    }

    // Fixes XML preview sizing by giving a safe default height if set to wrap_content
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minHeightInPx = (8f * resources.displayMetrics.density).toInt() // Default ~8dp height
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom + minHeightInPx

        val resolvedHeight = resolveSize(desiredHeight, heightMeasureSpec)
        val resolvedWidth = resolveSize(suggestedMinimumWidth + paddingLeft + paddingRight, widthMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
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

        // Define a safe track height (e.g., 6dp converted to pixels, or proportional to view height)
        val trackHeight = 16f
        val top = (height - trackHeight) / 2f
        val bottom = top + trackHeight
        val cornerRadius = trackHeight / 2f

        // 1. Draw background track centered vertically
        trackRect.set(0f, top, width, bottom)
        canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, trackPaint)

        // 2. Calculate filled progress width
        val progressRatio = (progress / max).coerceIn(0f, 1f)
        val filledWidth = width * progressRatio

        if (filledWidth > 0f) {
            // 3. Draw gradient progress portion
            progressRect.set(0f, top, filledWidth, bottom)
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint)

            // 4. Draw the white circular thumb indicator cleanly at the edge
            val thumbRadius = trackHeight * 0.7f
            val cx = filledWidth.coerceIn(thumbRadius, width - thumbRadius)
            val cy = height / 2f
            canvas.drawCircle(cx, cy, thumbRadius, thumbPaint)
        }
    }
}
package com.example.weatherapp.CustomViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class WaveCardBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF4B38A4.toInt() // Deep purple card color from your image
        style = Paint.Style.FILL
    }

    private val backgroundPath = Path()

    init {
        // Allows the custom view to draw its background path
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val width = w.toFloat()
        val height = h.toFloat()
        val cornerRadius = 48f // Smooth rounded corners for the card

        backgroundPath.reset()
        // Start near top-left, create a smooth dip/wave for the cloud area on the right, and round the corners
        backgroundPath.apply {
            // Top-left rounded corner
            moveTo(0f, cornerRadius)
            quadTo(0f, 0f, cornerRadius, 0f)

            // Top edge line going right, then dipping down in a smooth curve for the weather icon
            lineTo(width * 0.55f, 0f)
            cubicTo(
                width * 0.70f, 0f,
                width * 0.75f, height * 0.35f,
                width, height * 0.25f
            )

            // Right edge down to bottom-right corner
            lineTo(width, height - cornerRadius)
            quadTo(width, height, width - cornerRadius, height)

            // Bottom edge to bottom-left corner
            lineTo(cornerRadius, height)
            quadTo(0f, height, 0f, height - cornerRadius)
            close()
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the custom wavy card shape before drawing child text/images
        canvas.drawPath(backgroundPath, backgroundPaint)
        super.onDraw(canvas)
    }
}
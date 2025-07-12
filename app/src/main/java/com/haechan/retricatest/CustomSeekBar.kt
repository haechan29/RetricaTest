package com.haechan.retricatest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller

class CustomSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        private const val SCROLL_SENSITIVITY = 0.3f
    }

    var onValueChanged: ((Float) -> Unit)? = null

    private var value: Float = -1f
    private val scroller = OverScroller(context)

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        private var lastFlingX = 0f
        private val flingRunnable = object : Runnable {
            override fun run() {
                if (scroller.computeScrollOffset()) {
                    val newX = scroller.currX.toFloat()
                    val delta = (lastFlingX - newX) / width * SCROLL_SENSITIVITY
                    value = (value + delta).coerceIn(-1f, 1f)
                    onValueChanged?.invoke(value)
                    invalidate()

                    lastFlingX = newX
                    postOnAnimation(this)
                }
            }
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val delta = distanceX / width
            value = (value + delta).coerceIn(-1f, 1f)
            onValueChanged?.invoke(value)
            invalidate()
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            scroller.forceFinished(true)
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            scroller.fling(
                0, 0,
                velocityX.toInt(), 0,
                Int.MIN_VALUE, Int.MAX_VALUE,
                0, 0
            )
            lastFlingX = 0f
            postOnAnimation(flingRunnable)
            return true
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        val tickIntervalRatio = 0.1f
        val totalTicks = (2f / tickIntervalRatio).toInt()

        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            strokeWidth = 3f
        }

        for (i in 0..totalTicks) {
            val tickValue = -1f + i * tickIntervalRatio
            val tickX = centerX + (tickValue - value) * width / 2f

            if (tickX in 0f..width.toFloat()) {
                canvas.drawLine(tickX, centerY - 20f, tickX, centerY + 20f, tickPaint)
            }
        }

        val centerLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = 4f
        }
        canvas.drawLine(centerX, 0f, centerX, height.toFloat(), centerLinePaint)
    }

    fun stopFling() {
        scroller.forceFinished(true)
    }
}
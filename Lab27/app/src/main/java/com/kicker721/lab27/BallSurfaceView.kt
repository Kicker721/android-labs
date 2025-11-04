package com.kicker721.lab27

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.concurrent.thread

class BallSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var ballX = 0f
    private var ballY = 0f
    private var vx = 0f
    private var vy = 0f
    private val radius = 60f

    private var ax = 0f
    private var ay = 0f

    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var running = false

    init {
        holder.addCallback(this)
    }

    fun updateAcceleration(ax: Float, ay: Float) {
        this.ax = ax
        this.ay = ay
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        ballX = width / 2f
        ballY = height / 2f
        running = true
        startDrawing()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
    }

    private fun startDrawing() {
        thread {
            while (running) {
                val canvas = holder.lockCanvas()
                if (canvas != null) {
                    synchronized(holder) {
                        updatePhysics()
                        drawBall(canvas)
                    }
                    holder.unlockCanvasAndPost(canvas)
                }
                Thread.sleep(16)
            }
        }
    }

    private fun updatePhysics() {
        vx += ax
        vy += ay
        ballX += vx
        ballY += vy

        if (ballX - radius < 0) {
            ballX = radius
            vx = -vx * 0.8f
        } else if (ballX + radius > width) {
            ballX = width - radius
            vx = -vx * 0.8f
        }

        if (ballY - radius < 0) {
            ballY = radius
            vy = -vy * 0.8f
        } else if (ballY + radius > height) {
            ballY = height - radius
            vy = -vy * 0.8f
        }
    }

    private fun drawBall(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawCircle(ballX, ballY, radius, paint)
    }
}
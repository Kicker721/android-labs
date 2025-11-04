package com.kicker721.lab24

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.concurrent.thread
import kotlin.random.Random

class AquariumView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var drawingThread: Thread? = null
    private var running = false

    private val fishList = mutableListOf<Fish>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 165, 0)
        style = Paint.Style.FILL
    }

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val width = width
        val height = height

        repeat(5) {
            val x = Random.nextInt(10, width - 10)
            val y = Random.nextInt(10, height - 10)
            val speed = Random.nextFloat() * 5 + 2
            val direction = if (Random.nextBoolean()) 1 else -1
            fishList.add(Fish(x.toFloat(), y.toFloat(), speed, direction))
        }

        running = true
        drawingThread = thread {
            val frameTime = 16L
            while (running) {
                val canvas = holder.lockCanvas()
                if (canvas != null) {
                    synchronized(holder) {
                        drawScene(canvas)
                    }
                    holder.unlockCanvasAndPost(canvas)
                }
                Thread.sleep(frameTime)
            }
        }
    }

    private fun drawScene(canvas: Canvas) {
        canvas.drawColor(Color.rgb(0, 150, 255))

        for (fish in fishList) {
            fish.update(width)
            fish.draw(canvas, paint)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        drawingThread?.join()
    }

    private data class Fish(
        var x: Float,
        var y: Float,
        var speed: Float,
        var direction: Int
    ) {
        private val bodyLength = 80f
        private val bodyHeight = 40f

        fun update(screenWidth: Int) {
            x += speed * direction
            if (x > screenWidth - bodyLength || x < bodyLength-1) {
                direction *= -1
            }
        }

        fun draw(canvas: Canvas, paint: Paint) {
            val path = Path()

            path.moveTo(x, y)
            path.lineTo(x + bodyLength / 2 * direction, y - bodyHeight / 2)
            path.lineTo(x + bodyLength * direction, y)
            path.lineTo(x + bodyLength / 2 * direction, y + bodyHeight / 2)
            path.close()

            canvas.drawPath(path, paint)

            val tail = Path()
            if (direction > 0) {
                tail.moveTo(x, y)
                tail.lineTo(x - bodyLength / 3, y - bodyHeight / 3)
                tail.lineTo(x - bodyLength / 3, y + bodyHeight / 3)
            } else {
                tail.moveTo(x, y)
                tail.lineTo(x + bodyLength / 3, y - bodyHeight / 3)
                tail.lineTo(x + bodyLength / 3, y + bodyHeight / 3)
            }
            tail.close()

            canvas.drawPath(tail, paint)
        }
    }
}

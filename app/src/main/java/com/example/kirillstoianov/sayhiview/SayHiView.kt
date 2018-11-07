package com.example.kirillstoianov.sayhiview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.roundToInt


/**
 * Created by Kirill Stoianov on 07.11.18.
 * http://android-er.blogspot.com/2014/05/draw-star-on-canvas.html
 * http://android-er.blogspot.com/2014/05/draw-path-of-polygon-on-canvas-of.html
 */
class SayHiView(context: Context) : View(context) {

    private var confettiItems: ArrayList<ConfettiShape> = ArrayList()

    private val handBitmap: Bitmap by lazy {
        val source = BitmapFactory.decodeResource(resources, R.drawable.img_sayhi_hand)
        val ratio = 1.3
        val bitmapWidth = Math.min(width, height) / 3
        val bitmapHeight = (Math.min(width, height) / 3 * ratio).roundToInt()
        return@lazy Bitmap.createScaledBitmap(source, bitmapWidth, bitmapHeight, true)
    }

    //Animated values
    private var animateRadius: Float = 0f
    private var handDegree: Float = 0f

    private val confettiDistanceAnimator by lazy {
        ValueAnimator.ofFloat(0f, 300f).apply {
            duration = 1000
            addUpdateListener {
                animateRadius = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private val handDegreeAnimator by lazy {
        ValueAnimator.ofFloat(-15f, 15f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                handDegree = it.animatedValue as Float
                invalidate()
            }
        }
    }

    /**
     * Initialize block
     */
    init {
        post {
            val circle = ConfettiShape()
            circle.setCircle(width / 4f, height / 4f, width / 10f, Path.Direction.CCW)
            confettiItems.add(circle)

            val rect = ConfettiShape()
            rect.setPolygon(width / 3f, height / 3f, width / 10f, 4)
            confettiItems.add(rect)

            val start = ConfettiShape()
            start.setStar(width / 2f, height / 2f, width / 10f, width / 20f, 5)
            confettiItems.add(start)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawHand(this)

            /*confettiItems.forEach { shape ->
                drawConfetti(this, shape)
            }*/
        }
    }

    fun startAnimate() {
        handDegreeAnimator.start()
        confettiDistanceAnimator.start()
    }

    private fun drawHand(canvas: Canvas) {
        val handLeft = width / 2f - handBitmap.width / 2
        val handTop = height / 2f - handBitmap.height / 2


        val matrix = Matrix()
        matrix.postRotate(handDegree, handBitmap.width / 2f, handBitmap.height.toFloat())
        matrix.postTranslate(handLeft, handTop)
        canvas.drawBitmap(handBitmap, matrix, null)
    }

    private fun drawConfetti(canvas: Canvas, shape: ConfettiShape) {
        canvas.drawPath(shape.path, shape.paint)
    }

    private fun getShapeRadius(confettiShape: ConfettiShape): Float {
        return when (confettiShape.size) {
            ConfettiShape.Size.SMALL -> Math.min(width, height) / 36f
            ConfettiShape.Size.MEDIUM -> Math.min(width, height) / 27f
            ConfettiShape.Size.LARGE -> Math.min(width, height) / 12f
        }
    }
}
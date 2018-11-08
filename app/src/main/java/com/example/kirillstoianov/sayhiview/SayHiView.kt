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
        //        ValueAnimator.ofFloat(Math.max(width, height).toFloat()/4,Math.max(width, height).toFloat()/2).apply {
        ValueAnimator.ofFloat(0f/*Math.min(width, height).toFloat() / 4*/, Math.min(width, height).toFloat() / 2f)
            .apply {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                duration = 2000
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
            generateConfetti()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {

//            canvas.drawOval(
//                RectF(
//                    width / 2f + animateRadius,
//                    height / 2f + animateRadius,
//                    width / 2f - animateRadius,
//                    height / 2f - animateRadius
//                ), Paint().apply { color = Color.RED })

            confettiItems.forEach { shape ->
                drawConfetti(this, shape)
            }
            drawHand(this)

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
        shape.pX = width/2+((animateRadius) * Math.cos(Math.toRadians(shape.degree.toDouble())).toFloat())
        shape.pY = height/2+((animateRadius) * Math.sin(Math.toRadians(shape.degree.toDouble())).toFloat())
        shape.r = getShapeRadius(shape)

        shape.draw(canvas)
    }

    private fun getShapeRadius(confettiShape: ConfettiShape): Float {
        val diameter = when (confettiShape.size) {
            ConfettiShape.Size.SMALL -> Math.min(width, height) / 36f
            ConfettiShape.Size.MEDIUM -> Math.min(width, height) / 27f
            ConfettiShape.Size.LARGE -> Math.min(width, height) / 12f
        }
        return diameter / 2
    }

    private fun generateConfetti() {
        val circle = ConfettiShape(ConfettiShape.Type.CIRCLE)
        circle.size = ConfettiShape.Size.SMALL
        circle.degree = 1f
        confettiItems.add(circle)

        val rect = ConfettiShape(ConfettiShape.Type.RECT)
        rect.size = ConfettiShape.Size.MEDIUM
        rect.degree = 45f
        rect.setColor(Color.RED)
        confettiItems.add(rect)

        val star = ConfettiShape(ConfettiShape.Type.STAR)
        star.size = ConfettiShape.Size.LARGE
        star.degree = 90f
        star.setColor(Color.YELLOW)
        confettiItems.add(star)
    }

    private fun getRandomShape():ConfettiShape{
        TODO("Implement this")
    }
}
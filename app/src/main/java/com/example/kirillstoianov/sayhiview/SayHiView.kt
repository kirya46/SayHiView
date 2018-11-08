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
    private val colors: ArrayList<Int> = arrayListOf(
        Color.parseColor("#ff0096"), Color.parseColor("#fe692e"),
        Color.parseColor("#50e3c2"), Color.parseColor("#ffb300"),
        Color.parseColor("#ff415c"), Color.parseColor("#ff415c"),
        Color.parseColor("#04d95c"), Color.parseColor("#135bfe")
    )

    private val handBitmap: Bitmap by lazy {
        val source = BitmapFactory.decodeResource(resources, R.drawable.img_sayhi_hand)
        val bitmapWidth = getBitmapWidth()
        val bitmapHeight = getBitmapHeight()
        return@lazy Bitmap.createScaledBitmap(source, bitmapWidth, bitmapHeight, true)
    }

    private fun getBitmapWidth(): Int = Math.min(width, height) / 3
    private fun getBitmapHeight(): Int {
        val ratio = 1.3
        return (Math.min(width, height) / 3 * ratio).roundToInt()
    }

    //Animated values
    private var animateRadius: Float = 0f
    private var handDegree: Float = 0f
    private var handAnimatedWidth: Float = 0f

    val handMatrix = Matrix()


    private val confettiDistanceAnimator by lazy {
        ValueAnimator.ofFloat(0f/*Math.min(width, height).toFloat() / 4*/, Math.min(width, height).toFloat() / 2.5f)
            .apply {
                duration = 400
                addUpdateListener {
                    animateRadius = it.animatedValue as Float
                    invalidate()
                }
            }
    }

    private val handDegreeAnimator by lazy {
        ValueAnimator.ofFloat(-15f, 15f).apply {
            duration = 500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                handDegree = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private val handScaleAnimator by lazy {
        ValueAnimator.ofFloat(0f, getBitmapWidth().toFloat()).apply {
            startDelay = 350
            duration = 250
            addUpdateListener { handAnimatedWidth = it.animatedValue as Float }
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

            //draw confetti items
            confettiItems.forEach { shape ->
                drawConfetti(this, shape)
            }

            //draw hand
            drawHand(this)
        }
    }

    fun startAnimate() {
        handScaleAnimator.start()
        confettiDistanceAnimator.start()
        handDegreeAnimator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handBitmap.recycle()
    }

    private fun drawHand(canvas: Canvas) {

        handMatrix.reset()

        val scaleWidthFactor = handAnimatedWidth / handBitmap.width
        val scaleHeightFactor = handAnimatedWidth * 1.3f / handBitmap.height

        val newWidth = handBitmap.width * scaleWidthFactor
        val newHeight = handBitmap.height * scaleHeightFactor

        val handLeft = (width / 2f) - newWidth / 2
        val handTop = (height / 2f) - newHeight / 2

        handMatrix.preRotate(handDegree, newWidth / 2f, newHeight)
        handMatrix.postScale(scaleWidthFactor, scaleHeightFactor)
        handMatrix.postTranslate(handLeft, handTop)

        canvas.drawBitmap(handBitmap, handMatrix, null)
    }

    private fun drawConfetti(canvas: Canvas, shape: ConfettiShape) {
        shape.pX = width / 2 +
                ((animateRadius + shape.radiusOffset) * Math.cos(Math.toRadians(shape.angle.toDouble())).toFloat())
        shape.pY = height / 2 +
                ((animateRadius + shape.radiusOffset) * Math.sin(Math.toRadians(shape.angle.toDouble())).toFloat())
        shape.radius = getShapeRadius(shape)

        shape.draw(canvas)
    }

    private fun getShapeRadius(confettiShape: ConfettiShape): Float {
        val diameter = when (confettiShape.size) {
            ConfettiShape.Size.SMALL -> Math.min(width, height) / 36f
            ConfettiShape.Size.MEDIUM -> Math.min(width, height) / 27f
            ConfettiShape.Size.LARGE -> Math.min(width, height) / 17f
        }
        return diameter / 2
    }

    private fun generateConfetti() {
        IntRange(0, 30).forEach {
            confettiItems.add(getRandomShape())
        }
    }

    private fun getRandomShape(): ConfettiShape {
        val confettiShape = ConfettiShape(getRandomShapeType())
        confettiShape.angle = getRandomDegree().toFloat()
        confettiShape.radiusOffset = getRandomRadiusOffset()
        confettiShape.size = getRandomShapeSize()
        confettiShape.setColor(getRandomColor())
        return confettiShape
    }

    private fun getRandomDegree(): Int {
        return (0 until 180).random() * 2
    }

    private fun getRandomRadiusOffset(): Int = (0 until (width / 5)).random()

    private fun getRandomShapeSize(): ConfettiShape.Size {
        val random = (0 until 4).random()
        return when (random) {
            1 -> ConfettiShape.Size.SMALL
            2 -> ConfettiShape.Size.MEDIUM
            3 -> ConfettiShape.Size.LARGE
            else -> ConfettiShape.Size.MEDIUM
        }
    }

    private fun getRandomColor(): Int {
        val random = (0 until colors.size).random()
        return colors[random]
    }

    private fun getRandomShapeType(): ConfettiShape.Type {
        val random = (0 until 5).random()
        return when (random) {
            1 -> ConfettiShape.Type.CIRCLE
            2 -> ConfettiShape.Type.RECT
            3 -> ConfettiShape.Type.PENTAGON
            4 -> ConfettiShape.Type.STAR
            else -> ConfettiShape.Type.RECT
        }
    }

}
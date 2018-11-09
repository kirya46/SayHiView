package com.example.kirillstoianov.sayhiview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View
import kotlin.math.roundToInt


/**
 * Created by Kirill Stoianov on 07.11.18.
 *
 * @link http://android-er.blogspot.com/2014/05/draw-star-on-canvas.html
 * @link http://android-er.blogspot.com/2014/05/draw-path-of-polygon-on-canvas-of.html
 * @link https://stackoverflow.com/questions/3630086/how-to-get-string-width-on-android
 */
class SayHiView(context: Context) : View(context) {

    //TODO: add listener
    private val listener: () -> Unit = {}

    //CONFETTI
    /**
     * List with confetti shapes.
     */
    private var confettiItems: ArrayList<ConfettiShape> = ArrayList()

    /**
     * List with colors for confetti shapes.
     */
    private val confettiColors: ArrayList<Int> = arrayListOf(
        Color.parseColor("#ff0096"), Color.parseColor("#fe692e"),
        Color.parseColor("#50e3c2"), Color.parseColor("#ffb300"),
        Color.parseColor("#ff415c"), Color.parseColor("#ff415c"),
        Color.parseColor("#04d95c"), Color.parseColor("#135bfe")
    )

    //HAND
    /**
     * Bitmap with hand.
     */
    private val handBitmap: Bitmap by lazy {
        val source = BitmapFactory.decodeResource(resources, R.drawable.img_sayhi_hand)
        val bitmapWidth = getHandBitmapWidth()
        val bitmapHeight = getHandBitmapHeight()
        return@lazy Bitmap.createScaledBitmap(source, bitmapWidth, bitmapHeight, true)
    }

    /**
     * Value which need multiply on [getHandBitmapWidth]
     * for get [handBitmap] height.
     */
    private val handBitmapHeightRatio = 1.3f


    //TITLE
    /**
     * Title text.
     */
    private val titleText: String = "You said “Hi!”"

    /**
     * Title text bounds for calculate
     * text position on canvas.
     */
    private val titleTextBound = Rect()

    /**
     * Title text paint.
     */
    private val titleTextPaint by lazy {
        Paint().apply {
            color = Color.WHITE
            typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
            textSize = height / 12f

            val shadowRadiusDpSize = 4
            val scaledShadowSizeInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                shadowRadiusDpSize.toFloat(),
                context.resources.displayMetrics
            )
            setShadowLayer(scaledShadowSizeInPx, 0f, 4f, Color.parseColor("#80000000"))
        }
    }

    //BACKGROUND
    /**
     * Background paint for draw gradient on canvas.
     */
    private val backgroundPaint by lazy {
        Paint().apply {
            val linearGradient = LinearGradient(
                0f, 0f,
                0f, height.toFloat(),
                Color.parseColor("#00000000"), Color.parseColor("#80000000"),
                Shader.TileMode.CLAMP
            )
            shader = linearGradient
            isDither = true
        }
    }

    //ANIMATED VALUES
    /**
     * Radius of imaginary circle
     * which increase with [confettiDistanceAnimator].
     *
     * Need for animate [ConfettiShape]'s transition from center
     * to destination position.
     */
    private var animatedConfettiRadius: Float = 0f

    /**
     * Rotate degree anim value if [handBitmap].
     *
     * Need for animate 'say hi' gesture.
     */
    private var animatedHandDegree: Float = 0f

    /**
     * Scale anim value of [handBitmap].
     *
     * Need for animate [handBitmap] showing effect.
     */
    private var animatedHandWidth: Float = 0f

    /**
     * Matrix for draw [handBitmap] on [Canvas]
     * with: 'translate','rotate' and 'scale' effects.
     */
    private val handMatrix = Matrix()

    /**
     * Animator of [animatedConfettiRadius] for animate [ConfettiShape]'s
     * transition effect.
     */
    private val confettiDistanceAnimator by lazy {
        ValueAnimator.ofFloat(0f, Math.min(width, height).toFloat() / 2.5f)
            .apply {
                duration = 400
                addUpdateListener {
                    animatedConfettiRadius = it.animatedValue as Float
                    invalidate()
                }
            }
    }

    /**
     * Animator of [handBitmap] rotation.
     */
    private val handDegreeAnimator by lazy {
        ValueAnimator.ofFloat(-15f, 15f).apply {
            duration = 500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                animatedHandDegree = it.animatedValue as Float
                invalidate()
            }
        }
    }

    /**
     * Animator of [handBitmap] scaling effetc.
     */
    private val handScaleAnimator by lazy {
        ValueAnimator.ofFloat(0f, getHandBitmapWidth().toFloat()).apply {
            startDelay = 350
            duration = 250
            addUpdateListener { animatedHandWidth = it.animatedValue as Float }
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

            //draw gradient background
            drawGradientBackground(this)

            //draw confetti items
            confettiItems.forEach { shape ->
                drawConfetti(this, shape)
            }

            //draw hand
            drawHand(this)

            //draw title text
            drawTitle(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //reset resources
        handBitmap.recycle()
        confettiItems.clear()
        matrix.reset()
    }

    /**
     * Start view animation.
     */
    fun startAnimate() {
        handScaleAnimator.start()
        confettiDistanceAnimator.start()
        handDegreeAnimator.start()
    }

    /**
     * Draw view gradient background.
     */
    private fun drawGradientBackground(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
    }

    /**
     * Draw confetti shape.
     *
     */
    private fun drawConfetti(canvas: Canvas, shape: ConfettiShape) {
        shape.pX = width / 2 +
                ((animatedConfettiRadius + shape.radiusOffset) * Math.cos(Math.toRadians(shape.angle.toDouble())).toFloat())
        shape.pY = height / 2 +
                ((animatedConfettiRadius + shape.radiusOffset) * Math.sin(Math.toRadians(shape.angle.toDouble())).toFloat())
        shape.radius = getShapeRadius(shape)

        shape.draw(canvas)
    }

    /**
     * Draw view title text.
     */
    private fun drawTitle(canvas: Canvas) {

        titleTextPaint.getTextBounds(titleText, 0, titleText.length, titleTextBound)

        val textWidth = titleTextBound.width()
        val textHeight = titleTextBound.height()


        val textLeft = width / 2f - (textWidth / 2)
        val textTop = height / 5f //- (textHeight / 2)
        canvas.drawText(titleText, textLeft, textTop, titleTextPaint)
    }

    /**
     * Draw [handBitmap].
     */
    private fun drawHand(canvas: Canvas) {

        handMatrix.reset()

        val scaleWidthFactor = animatedHandWidth / handBitmap.width
        val scaleHeightFactor = animatedHandWidth * handBitmapHeightRatio / handBitmap.height

        val newWidth = handBitmap.width * scaleWidthFactor
        val newHeight = handBitmap.height * scaleHeightFactor

        val handLeft = (width / 2f) - newWidth / 2
        val handTop = (height / 2f) - newHeight / 2

        handMatrix.preRotate(
            animatedHandDegree,
            newWidth / 2f/*point of rotation by x-asix*/,
            newHeight/*point of rotate by y-axis*/
        )
        handMatrix.postScale(scaleWidthFactor, scaleHeightFactor)
        handMatrix.postTranslate(handLeft, handTop)

        canvas.drawBitmap(handBitmap, handMatrix, null)
    }

    /**
     * Get shape radius for drawing.
     *
     * [SayHiView] support 3 different size's for [ConfettiShape].
     */
    private fun getShapeRadius(confettiShape: ConfettiShape): Float {
        val diameter = when (confettiShape.size) {
            ConfettiShape.Size.SMALL -> Math.min(width, height) / 36f
            ConfettiShape.Size.MEDIUM -> Math.min(width, height) / 27f
            ConfettiShape.Size.LARGE -> Math.min(width, height) / 17f
        }
        return diameter / 2
    }

    /**
     * Fill local list with random [ConfettiShape]'s.
     */
    private fun generateConfetti() {
        IntRange(0, 30).forEach {
            confettiItems.add(getRandomShape())
        }
    }

    /**
     * Get random [ConfettiShape] item with random configuration.
     */
    private fun getRandomShape(): ConfettiShape {
        val confettiShape = ConfettiShape(getRandomShapeType())
        confettiShape.angle = getRandomDegree().toFloat()
        confettiShape.radiusOffset = getRandomRadiusOffset()
        confettiShape.size = getRandomShapeSize()
        confettiShape.setColor(getRandomColor())
        return confettiShape
    }

    /**
     * Get random degree for [ConfettiShape]
     * on imaginary circle.
     *
     * @return - values in range 0.. 360.
     */
    private fun getRandomDegree(): Int {
        return (0 until 180).random() * 2
    }

    /**
     * Get random value which wil attach to
     * [animatedConfettiRadius] for make different radius
     * of imaginary circle for each [ConfettiShape].
     */
    private fun getRandomRadiusOffset(): Int {
        if (width == 0) return 0
        return (0 until (width / 5)).random()
    }

    /**
     * Get random shape size.
     */
    private fun getRandomShapeSize(): ConfettiShape.Size {
        val random = (0 until 4).random()
        return when (random) {
            1 -> ConfettiShape.Size.SMALL
            2 -> ConfettiShape.Size.MEDIUM
            3 -> ConfettiShape.Size.LARGE
            else -> ConfettiShape.Size.MEDIUM
        }
    }

    /**
     * Get random color for each [ConfettiShape].
     */
    private fun getRandomColor(): Int {
        val random = (0 until confettiColors.size).random()
        return confettiColors[random]
    }

    /**
     * Get random shape type.
     */
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

    /**
     * Get [handBitmap] width.
     */
    private fun getHandBitmapWidth(): Int {
        if (width == 0 || height == 0) return 0
        return Math.min(width, height) / 3
    }

    /**
     * Get [handBitmap] height.
     */
    private fun getHandBitmapHeight(): Int {
        if (width == 0 || height == 0) return 0
        return (getHandBitmapWidth() * handBitmapHeightRatio).roundToInt()
    }

    /**
     *
     */
    class ConfettiShape(private var type: Type) {

        /**
         * Paint of shape.
         */
        val paint: Paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.GREEN
        }

        /**
         * Path of shape.
         */
        val path: Path = Path()

        /**
         * Position of shape by x-axis
         */
        var pX: Float = 0f

        /**
         * Position of shape by y-axis
         */
        var pY: Float = 0f

        /**
         * Radius of shape.
         */
        var radius: Float = 0f

        /**
         * Angle on confetti increase circle
         */
        var angle = 0f

        /**
         * Deviation of confetti increase circle.
         */
        var radiusOffset: Int = 0

        /**
         * Shape size.
         */
        var size = Size.MEDIUM

        fun setCircle(x: Float, y: Float, radius: Float, dir: Path.Direction) {
            path.reset()
            path.addCircle(x, y, radius, dir)
        }

        fun setPolygon(x: Float, y: Float, radius: Float, numOfPt: Int) {

            val section = 2.0 * Math.PI / numOfPt

            path.reset()
            path.moveTo(
                (x + radius * Math.cos(0.0)).toFloat(),
                (y + radius * Math.sin(0.0)).toFloat()
            )

            for (i in 1 until numOfPt) {
                path.lineTo(
                    (x + radius * Math.cos(section * i)).toFloat(),
                    (y + radius * Math.sin(section * i)).toFloat()
                )
            }

            path.close()
        }

        fun setStar(x: Float, y: Float, radius: Float, innerRadius: Float, numOfPt: Int) {

            val section = 2.0 * Math.PI / numOfPt

            path.reset()
            path.moveTo(
                (x + radius * Math.cos(0.0)).toFloat(),
                (y + radius * Math.sin(0.0)).toFloat()
            )
            path.lineTo(
                (x + innerRadius * Math.cos(0 + section / 2.0)).toFloat(),
                (y + innerRadius * Math.sin(0 + section / 2.0)).toFloat()
            )

            for (i in 1 until numOfPt) {
                path.lineTo(
                    (x + radius * Math.cos(section * i)).toFloat(),
                    (y + radius * Math.sin(section * i)).toFloat()
                )
                path.lineTo(
                    (x + innerRadius * Math.cos(section * i + section / 2.0)).toFloat(),
                    (y + innerRadius * Math.sin(section * i + section / 2.0)).toFloat()
                )
            }

            path.close()
        }

        fun setColor(color: Int) {
            paint.color = color
        }

        fun draw(canvas: Canvas) {
            when (type) {
                Type.CIRCLE -> {
                    setCircle(pX, pY, radius, Path.Direction.CCW)
                    canvas.drawPath(path, paint)
                }
                Type.RECT -> {
                    setPolygon(pX, pY, radius, 4)
                    canvas.drawPath(path, paint)
                }
                Type.PENTAGON -> {
                    setPolygon(pX, pY, radius, 5)
                    canvas.drawPath(path, paint)
                }
                Type.STAR -> {
                    setStar(pX, pY, radius, radius / 2, 5)
                    canvas.drawPath(path, paint)
                }
                else -> throw UnsupportedOperationException("Unsupported type for draw: [$type]")
            }
        }

        enum class Size { SMALL, MEDIUM, LARGE }
        enum class Type { CIRCLE, RECT, PENTAGON, STAR }
    }
}
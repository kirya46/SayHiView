package com.example.kirillstoianov.sayhiview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path


/**
 * Created by Kirill Stoianov on 07.11.18.
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

    fun createPath() {
        when (type) {
            Type.CIRCLE -> {
                setCircle(pX, pY, radius, Path.Direction.CCW)
            }
            Type.RECT -> {
                setPolygon(pX, pY, radius, 4)
            }
            Type.PENTAGON -> {
                setPolygon(pX, pY, radius, 5)
            }
            Type.STAR -> {
                setStar(pX, pY, radius, radius / 2, 5)
            }
            else -> throw UnsupportedOperationException("Unsupported type for draw: [$type]")
        }
    }

    enum class Size { SMALL, MEDIUM, LARGE }
    enum class Type { CIRCLE, RECT, PENTAGON, STAR }
}
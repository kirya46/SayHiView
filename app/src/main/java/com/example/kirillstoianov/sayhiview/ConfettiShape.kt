package com.example.kirillstoianov.sayhiview

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path


/**
 * Created by Kirill Stoianov on 07.11.18.
 */
class ConfettiShape {

    val paint: Paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.GREEN
    }

    val size = Size.MEDIUM

    val path: Path = Path()

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

    enum class Size { SMALL, MEDIUM, LARGE }
}
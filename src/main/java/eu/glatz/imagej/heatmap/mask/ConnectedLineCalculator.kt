package eu.glatz.imagej.heatmap.mask

import java.awt.Point
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Calculates all points from p1 to p2 for a continues line
 */
class ConnectedLineCalculator() {

    fun getIntersectionPixels(p1: Point, p2: Point): Array<Point> {
        return if (abs(p2.x - p1.x) > abs(p2.y - p1.y)) {
            if (p1.x > p2.x)
                getIntersectionPixelsX(p2, p1)
            else
                getIntersectionPixelsX(p1, p2)
        } else {
            if (p1.y > p2.y)
                getIntersectionPixelsY(p2, p1)
            else
                getIntersectionPixelsY(p1, p2)
        }
    }

    private fun getIntersectionPixelsX(p1: Point, p2: Point): Array<Point> {
        val result = mutableListOf<Point>()
        val ySlope = (p2.y - p1.y).toDouble() / (p2.x - p1.x).toDouble()
        val ySlopeDir = if (ySlope < 0) -1 else 1

        for (x in p1.x + 1..p2.x + 1) {
            val y = (if (ySlopeDir > 0)
                ceil(((x - (p1.x + 1)) * ySlope)).toInt()
            else
                floor(((x - (p1.x + 1)) * ySlope).toDouble()).toInt()) + p1.y
            val point = Point(x - 1, y)

            if (result.size > 0 && result.last().y != y) {
                val postPoint = Point(x - 1, y - ySlopeDir)
                result.add(postPoint)
            }
            result.add(point)
        }
        return result.toTypedArray()
    }

    private fun getIntersectionPixelsY(p1: Point, p2: Point): Array<Point> {
        val result = mutableListOf<Point>()
        val xSlope = (p2.x - p1.x).toDouble() / (p2.y - p1.y).toDouble()
        val xSlopeDir = if (xSlope < 0) -1 else 1

        for (y in p1.y + 1..p2.y + 1) {
            val x = (if (xSlopeDir > 0)
                ceil(((y - (p1.y + 1)) * xSlope).toDouble()).toInt()
            else
                floor(((y - (p1.y + 1)) * xSlope).toDouble()).toInt()) + p1.x

            val point = Point(x, y - 1)

            if (result.size > 0 && result.last().x != x) {
                val postPoint = Point(x - xSlopeDir, y - 1)
                result.add(postPoint)
            }
            result.add(point)
        }
        return result.toTypedArray()
    }
}

package eu.glatz.imagej.heatmap.mask

import eu.glatz.imagej.model.Point
import kotlin.math.pow

/**
 * Calculates start and endpoint ov 512 normals for a parabola
 */
class RayCalculator {

    fun calcRaysStartAndEndPoint(): MutableList<Pair<Point, Point>> {
        val parabola = Parabola(Point(256, 20), 0.004F)

        val result = mutableListOf<Pair<Point, Point>>()

        val startX = 256
        val startY = 20
        val factor = 0.004F

        for (x0 in 0 until 255) {
            val sPoint = Point()
            val endPoint = Point()

            var start = false
            for (x in 0 until 512) {
                val y = (1 / (-factor * 2 * (x0 - startX))) * (x - x0) + (factor * (x0 - startX).toDouble().pow(2) + startY)
                if (y >= 0 && !start) {
                    sPoint.x = x
                    sPoint.y = y.toInt()
                    start = true
                } else if (y > 511 && start) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                    break
                } else if (x == 511) {
                    sPoint.x = x
                    sPoint.y = y.toInt()
                }
            }
            result.add(Pair(sPoint, endPoint))
        }

        result.add(Pair(Point(255, 0), Point(255, 511)))

        for (x0 in 256 until 512) {
            val sPoint = Point()
            val endPoint = Point()

            var start = false

            for (x in 0 until 512) {
                val y = (1 / (-factor * 2 * (x0 - startX))) * (x - x0) + (factor * (x0 - startX).toDouble().pow(2) + startY)
                if (y <= 511 && !start) {
                    sPoint.x = x
                    sPoint.y = y.toInt()
                    start = true
                } else if (y < 0 && start) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                    break
                } else if (x == 511) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                }
            }
            result.add(Pair(sPoint, endPoint))
        }

        return result
    }
}
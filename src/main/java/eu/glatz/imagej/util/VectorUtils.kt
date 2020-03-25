package eu.glatz.imagej.util

import eu.glatz.imagej.model.Point
import kotlin.math.pow
import kotlin.math.sqrt

object VectorUtils {

    fun distance(p1: Point, p2: Point): Double {
        return sqrt((p1.x - p2.x).toDouble().pow(2) + (p1.y - p2.y).toDouble().pow(2))
    }

    fun directionVector(p1: Point, p2: Point): Point {
        return Point(p1.x - p2.x, p1.y - p2.y)
    }

}
package eu.glatz.imagej.model

import java.awt.geom.Point2D
import kotlin.math.sqrt

class Point {
    var x: Int = 0
    var y: Int = 0
    var value: Int = 0

    constructor()
    constructor(x: Int, y: Int) : this(x, y, 0)
    constructor(x: Int, y: Int, value: Int) {
        this.x = x
        this.y = y
        this.value = value
    }

    fun distance(pt: Point): Double {
        val px: Double = (pt.x - this.x).toDouble()
        val py: Double = (pt.y - this.y).toDouble()
        return sqrt(px * px + py * py)
    }
}
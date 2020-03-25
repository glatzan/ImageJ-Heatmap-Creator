package eu.glatz.imagej.model

open class SimpleLine : Line {
    override var id: Int = 0
    var points = mutableListOf<Point>()
    override var length: Double = 0.0

    constructor()
    constructor(id: Int) {
        this.id = id
    }
    constructor(id: Int, points: List<Point>) {
        this.id = id
        this.addPoint(*points.toTypedArray())
    }

    override fun addPoint(vararg point: Point): Line {
        point.forEach { this.points.add(it) }
        return this
    }

    override fun updateLength() {
        this.length = calcLength(points)
    }

    companion object {
        fun calcLength(points: MutableList<Point>): Double {
            var length = 0.0
            for (i in 1 until points.size) {
                length += points[i - 1].distance(points[i])
            }
            return length
        }
    }

    override fun firstPoint(): Point {
        return points.first()
    }

    override fun lastPoint(): Point {
        return points.last()
    }

    override fun getAllPoints(): List<Point> {
        return points
    }

}
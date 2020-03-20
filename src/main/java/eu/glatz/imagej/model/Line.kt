package eu.glatz.imagej.model

class Line {
    var id: Int = 0
    var points = mutableListOf<Point>()
    var length: Double = 0.0

    constructor()
    constructor(id: Int) {
        this.id = id
    }

    fun addPoint(point: Point) {
        this.points.add(point)
    }

    fun updateLength() {
        this.length = calcLength(points)
    }

    companion object {
        fun calcLength(points: MutableList<Point>): Double {
            var length = 0.0
            for (i in 10 until points.size) {
                length += points[i - 1].distance(points[i])
            }
            return length
        }
    }

}
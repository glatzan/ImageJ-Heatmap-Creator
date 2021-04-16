package eu.glatz.imagej.model

interface Line {
    var id: Int
    var length: Double
    fun addPoint(vararg point: Point): Line
    fun updateLength()
    fun firstPoint(): Point
    fun lastPoint(): Point
    fun getAllPoints(): List<Point>

    fun calculateIntersection(line: Line) {
//        val equation = "(((${x})+(${direction.x})*t)*(${direction.x}))+(((${y})+(${direction.y})*t)*(${direction.y}))"
    }
}

package eu.glatz.imagej.model

class JoinedLine(id: Int) : Line {
    override var id: Int = 0
    override var length: Double = 0.0
    val lines: MutableList<Line> = mutableListOf();

    fun addLine(line: Line) {
        lines.add(line)
    }

    fun addLines(lines: List<Line>) {
        lines.forEach { addLine(it) }
    }

    override fun addPoint(vararg point: Point) : Line {
        lines.last().addPoint(*point)
        return this
    }

    override fun updateLength() {
        this.length = lines.map { it.length }.sum()
    }

    override fun firstPoint(): Point {
        return lines.first().firstPoint()
    }

    override fun lastPoint(): Point {
        return lines.last().lastPoint()
    }

    override fun getAllPoints(): List<Point> {
        return lines.flatMap { it.getAllPoints() }
    }
}
package eu.glatz.imagej.model

import eu.glatz.imagej.util.VectorUtils

/**
 * Holder for lines with distance to parabola
 */
class LineClassification {
    var line: Line
    var stepsToParabola: Int = 0
    var correlation: Double = 0.0
    var distanceToParabola: Double = 0.0

    lateinit var firstParabolaNormalPoint: Point
    lateinit var lastParabolaNormalPoint: Point

    constructor(line: Line, parabola: Parabola) {
        this.line = line
        this.firstParabolaNormalPoint = parabola.calculatePointOfNormalOnParabola(line.firstPoint())
        this.lastParabolaNormalPoint = parabola.calculatePointOfNormalOnParabola(line.lastPoint())
        distanceToParabola = doubleArrayOf(VectorUtils.distance(line.firstPoint(), this.firstParabolaNormalPoint),
                VectorUtils.distance(line.lastPoint(), this.lastParabolaNormalPoint)).average()
    }

    /**
     * Calculates how many lines are inbetween one line an the parabola
     */
    fun calculateStepsToParabola(lines: List<LineClassification>) {
        for (otherLine in lines) {
            // in between
            if ((otherLine.line.firstPoint().x >= line.firstPoint().x && otherLine.line.firstPoint().x < line.lastPoint().x) ||
                    (otherLine.line.lastPoint().x >= line.firstPoint().x && otherLine.line.lastPoint().x < line.lastPoint().x)) {
                if (distanceToParabola > otherLine.distanceToParabola)
                    stepsToParabola++
            }
        }
    }
}
package eu.glatz.imagej.heatmap.ray

import ij.process.ImageProcessor
import org.matheclipse.core.eval.ExprEvaluator
import java.awt.Color
import java.awt.Point
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Parabola
 */
class Parabola {

    var startPoint: Point = Point()
    var factor: Float = 0.001F

    constructor()
    constructor(startPoint: Point, factor: Float) {
        this.startPoint = startPoint
        this.factor = factor
    }

    fun draw(ip: ImageProcessor, color: Color = Color(0, 0, 0), fromX: Int = 0, toX: Int = ip.width) {
        ip.setColor(color)
        for (x in fromX until toX) {
            ip.drawDot(x, calcY(x))
        }
    }

    fun calcY(x: Int): Int {
        return (factor * (x - startPoint.x).toDouble().pow(2) + startPoint.y).toInt()
    }

    fun calcX(y: Int): IntArray {
        val x = sqrt(((y - startPoint.y) / factor) + startPoint.x).toInt()
        return intArrayOf(x, -x)
    }

    fun calcDistanceOfPointToParabola(externalPoint: Point): Double {
        val parabolaPoint = calculatePointOfNormalOnParabola(externalPoint)
        return externalPoint.distance(parabolaPoint)
    }

    fun calculatePointOfNormalOnParabola(externalPoint: Point): Point {
        val util = ExprEvaluator(false, 5)
        val equation = "(1 / (-${factor} * 2 * (x - ${startPoint.x}))) * (${externalPoint.x} - x) + (${factor} * (x - ${startPoint.x})^2 + ${startPoint.y}) == ${externalPoint.y}"
        val solveEquation = util.eval("Solve(${equation},x)")
        val x = solveEquation.first().first().second().re().toString().toDouble();
        val y = calcY(x.toInt())
        return Point(x.toInt(), y)
    }

    fun calculateShortestWayPointOnParabola(externalPoint: Point){
        val y = "(${factor} * (x - ${startPoint.x})^2 + ${startPoint.y})"
    }
}

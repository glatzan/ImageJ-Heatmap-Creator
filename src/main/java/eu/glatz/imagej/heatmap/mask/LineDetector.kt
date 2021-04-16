package eu.glatz.imagej.heatmap.mask

import ij.IJ
import ij.process.ImageProcessor
import java.awt.Point
import java.util.*
import kotlin.math.floor

class LineDetector {

    fun shortestPointOnMask(hostLine: MaskLine, point: Point): Point? {
        var shortDist = Double.MAX_VALUE
        var result: Point? = null

        for (p in hostLine.points) {
            val di = p.distance(point)
            if (shortDist > di) {
                shortDist = di
                result = p
            }
        }

        return result
    }

    fun findLines(ip: ImageProcessor, kernelSize: Int = 5): List<MaskLine> {
        val lines = mutableListOf<MaskLine>()
        val activeFront = Stack<Pair<Point, UInt>>()
        val visitedPoints = Array(ip.width) { BooleanArray(ip.height) }

        var idCounter = 0
        for (x in 0 until ip.width) {
            for (y in 0 until ip.height) {
                val pix = ip.get(x, y).toUInt()
                if (pix != 0xFF000000u && !visitedPoints[x][y]) {
                    activeFront.push(Pair(Point(x, y), pix))
                    visitedPoints[x][y] = true
                    val line = MaskLine()
                    line.type = if (pix.and(0xFF0000u) == 0xFF0000u) MatchType.Host else MatchType.Graft
                    lines.add(sortLine(connectLine(line, activeFront, visitedPoints, ip, kernelSize, if (line.type == MatchType.Host) 0xFF0000u else 0xFF00u)))
                }
            }
        }
        return lines
    }

    private fun connectLine(line: MaskLine, activeFront: Stack<Pair<Point, UInt>>, visitedPoints: Array<BooleanArray>, ip: ImageProcessor, kernelSize: Int, value: UInt): MaskLine {
        val kernelCalc = floor(kernelSize.toDouble() / 2).toInt()
        while (!activeFront.empty()) {
            val point = activeFront.pop()

            if (point.second.and(value) != value)
                continue

            line.points.add(point.first)

            for (x in -kernelCalc..kernelCalc) {
                for (y in -kernelCalc..kernelCalc) {
                    val searchPoint = Point(point.first.x + x, point.first.y + y)
                    if (searchPoint.x in 0 until ip.width && searchPoint.y in 0 until ip.height && !visitedPoints[point.first.x + x][point.first.y + y]) {
                        val value = ip.get(searchPoint.x, searchPoint.y).toUInt()
                        activeFront.push(Pair(searchPoint, value))
                    }
                    visitedPoints[point.first.x][point.first.y] = true
                }
            }
        }
        return line
    }

    private fun sortLine(line: MaskLine): MaskLine {
        val points = mutableListOf<Point>()
        var currentPoint = line.points.removeAt(0)
        var minDistanceToCurrentPoint = Double.MAX_VALUE
        var nearestPoint: Point? = null
        var addAtStart = false
        var lostPoints = 0
        points.add(currentPoint)

        val resultImage = IJ.createImage("Parabola", "RGB", 512, 512, 1)
        val resultProcessor = resultImage.processor

        while (line.points.isNotEmpty()) {
            for (point in line.points) {
                val distanceToCurrentPoint = point.distance(currentPoint)
                if (distanceToCurrentPoint < 5 && distanceToCurrentPoint < minDistanceToCurrentPoint) {
                    nearestPoint = point
                    minDistanceToCurrentPoint = distanceToCurrentPoint
                }
            }

            if (nearestPoint != null) {
                resultProcessor.drawDot(nearestPoint.x, nearestPoint.y)
                line.points.remove(nearestPoint)
                if (addAtStart)
                    points.add(0, nearestPoint)
                else
                    points.add(nearestPoint)
                currentPoint = nearestPoint

                minDistanceToCurrentPoint = Double.MAX_VALUE
                nearestPoint = null
                lostPoints = 0
            } else {
                // point was in the middel starting from front
                addAtStart = true
                currentPoint = points.first()

                if (lostPoints > 1) {
                    break
                }

                lostPoints++
            }
        }
        resultImage.show()
        line.points = points

        return line
    }

}

class MaskLine {
    var type: MatchType = MatchType.Graft
    var points: MutableList<Point> = mutableListOf<Point>()
    var startPoint: Point = Point()
    var endPoint: Point = Point()
}

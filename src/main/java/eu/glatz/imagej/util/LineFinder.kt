package eu.glatz.imagej.util

import eu.glatz.imagej.model.SimpleLine
import eu.glatz.imagej.model.Point
import ij.process.ImageProcessor
import java.util.*
import kotlin.math.floor

object LineFinder {

    fun findConnectedRegion(ip: ImageProcessor, kernelSize: Int = 5): List<SimpleLine> {
        val lines = mutableListOf<SimpleLine>();
        val activeFront = Stack<Point>()
        val visitedPoints = Array(ip.width) { BooleanArray(ip.height) }

        var idCounter = 0;
        for (x in 0 until ip.width) {
            for (y in 0 until ip.height) {
                val pix = ip.get(x, y)
                if (pix == 255 && !visitedPoints[x][y]) {
                    activeFront.push(Point(x, y, pix))
                    visitedPoints[x][y] = true
                    val line = SimpleLine(idCounter++)
                    lines.add(connectedRegion(line, activeFront, visitedPoints, ip, kernelSize))
                }
            }
        }

        sortLines(lines)

        return lines
    }

    //    , out: ImageProcessor
    private fun sortLines(lines: List<SimpleLine>) {
        lines.forEach { sortLine(it) }
    }

    private fun sortLine(line: SimpleLine) {
        val points = mutableListOf<Point>()
        var currentPoint = line.points.removeAt(0)
        var minDistanceToCurrentPoint = Double.MAX_VALUE
        var nearestPoint: Point? = null
        var addAtStart = false
        var lostPoints = 0
        points.add(currentPoint)

//        out.setColor(Color.RED)
//        out.drawDot(currentPoint.x, currentPoint.y)
//        out.setColor(Color.GREEN)

//        val img = ImagePlus("test", out)
//        img.show()

        while (line.points.isNotEmpty()) {
            for (point in line.points) {
                val distanceToCurrentPoint = point.distance(currentPoint)
                if (distanceToCurrentPoint < 5 && distanceToCurrentPoint < minDistanceToCurrentPoint) {
                    nearestPoint = point
                    minDistanceToCurrentPoint = distanceToCurrentPoint
                }
            }

            if (nearestPoint != null) {
                line.points.remove(nearestPoint)
                if (addAtStart)
                    points.add(0, nearestPoint)
                else
                    points.add(nearestPoint)
                currentPoint = nearestPoint

//                if(minDistanceToCurrentPoint > 2)
//                    out.setColor(Color.BLUE)
//                else
//                    out.setColor(Color.GREEN)
//                out.drawDot(currentPoint.x, currentPoint.y)
                minDistanceToCurrentPoint = Double.MAX_VALUE
                nearestPoint = null
                lostPoints = 0
            } else {
                // point was in the middel starting from front
                addAtStart = true
                currentPoint = points.first()
//                out.setColor(Color.YELLOW)

                if (lostPoints > 1) {
                    break
                }

                lostPoints++;
            }

//            img.updateAndDraw()
        }


//        for(point in line.points){
//            out.setColor(Color.CYAN)
//            out.drawDot(point.x, point.y)
//        }

        line.points = points
        line.updateLength()
        print("done!")

    }


    private fun connectedRegion(line: SimpleLine, activeFront: Stack<Point>, visitedPoints: Array<BooleanArray>, ip: ImageProcessor, kernelSize: Int): SimpleLine {
        val kernelCalc = floor(kernelSize.toDouble() / 2).toInt()
        while (!activeFront.empty()) {
            val point = activeFront.pop();
            if (point.value == 255) {
                line.addPoint(point)
                // 8x8 neighborhood
                for (x in -kernelCalc..kernelCalc) {
                    for (y in -kernelCalc..kernelCalc) {
                        val searchPoint = Point(point.x + x, point.y + y)
                        if (searchPoint.x in 0 until ip.width && searchPoint.y in 0 until ip.height && !visitedPoints[point.x + x][point.y + y]) {
                            searchPoint.value = ip.get(searchPoint.x, searchPoint.y)
                            activeFront.push(searchPoint)
                        }
                        visitedPoints[point.x][point.y] = true
                    }
                }
            }
        }

        return line
    }


    fun removeShortLines(lines: List<SimpleLine>, threshold: Double): List<SimpleLine> {
        return lines.filter { it.length > threshold }
    }
}
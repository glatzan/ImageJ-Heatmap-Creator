package eu.glatz.imagej.heatmap.segmentaion

import ij.process.ImageProcessor
import java.awt.Point
import java.awt.Rectangle
import java.util.*
import kotlin.math.max
import kotlin.math.min

object ImageSegmentation {

    lateinit var visitedMap: Array<BooleanArray>

    fun imageSegmentation(processor: ImageProcessor): List<SegmentedImage> {

        clearVisitedMap(processor.width, processor.height)

        val segments = mutableListOf<SegmentedImage>()

        for (x in 0 until processor.width) {
            for (y in 0 until processor.height) {
                if (!visitedMap[x][y] && processor.get(x, y) > 0)
                    segments.add(imageSegmentation(processor, Point(x, y), visitedMap, segments.size))
            }
        }
        return segments
    }


    private fun imageSegmentation(processor: ImageProcessor, start: Point, visitedMap: Array<BooleanArray>, segmentCount: Int): SegmentedImage {

        val connectedRegion = mutableListOf<Point>()
        val activeFront = Stack<Point>()

        activeFront.push(start)

        var maxX = Int.MIN_VALUE
        var minX = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE
        var minY = Int.MAX_VALUE

        while (!activeFront.empty()) {
            val activePixel = activeFront.pop()

            if (visitedMap[activePixel.x][activePixel.y])
                continue
            else
                visitedMap[activePixel.x][activePixel.y] = true

            val currentValue = processor.get(activePixel.x, activePixel.y)

            if (currentValue > 0) {
                connectedRegion.add(activePixel)

                maxX = max(maxX, activePixel.x)
                minX = min(minX, activePixel.x)

                maxY = max(maxY, activePixel.y)
                minY = min(minY, activePixel.y)

                for (x in activePixel.x - 1..activePixel.x + 1) {
                    for (y in activePixel.y - 1..activePixel.y + 1) {
                        if (x < 0 || x >= processor.width)
                            continue
                        if (y < 0 || y >= processor.height)
                            continue

                        val newPoint = Point(x, y)

                        if (!visitedMap[newPoint.x][newPoint.y])
                            activeFront.add(newPoint)
                    }
                }
            }
        }

        val dimension = Rectangle(minX, minY, maxX - minX+1, maxY - minY+1)

        val segment = SegmentedImage(segmentCount)
        segment.dimension = dimension
        segment.pixels = connectedRegion.toTypedArray()

        val map = Array(dimension.width) { BooleanArray(dimension.height) }
        for (point in connectedRegion) {
            map[point.x - minX][point.y - minY] = true
        }

        segment.pixelMap = map

        return segment
    }

    private fun clearVisitedMap(width: Int, height: Int) {
        if (!this::visitedMap.isInitialized || this.visitedMap.size != width ||
                this.visitedMap.isEmpty() || this.visitedMap[0].size != height)
            visitedMap = Array(width) { BooleanArray(height) }

        for (x in 0 until visitedMap.size) {
            for (y in 0 until visitedMap[x].size) {
                visitedMap[x][y] = false
            }
        }
    }
}

package eu.glatz.imagej.heatmap.mask

import eu.glatz.imagej.heatmap.ray.Parabola
import ij.IJ
import ij.ImagePlus
import ij.process.ImageProcessor
import java.awt.Color
import java.awt.Point

class ParabolaLineDetector {

    fun floodWithParabola(rawImageProcessor: ImageProcessor): ImagePlus {
        val resultImage = IJ.createImage("Mask", 512, 512, 1, 8)
        val resultImageProcessor = resultImage.processor
        resultImageProcessor.setColor(Color.WHITE)

//        val resultImage2 = IJ.createImage("test", "RGB", 512, 512, 1)
//        val resultProcessor2 = resultImage2.processor

        val parabolaPoints = getParabolaPoints()
        val calc = ConnectedLineCalculator()

        for (x in 0 until rawImageProcessor.width) {
            for (y in 0 until rawImageProcessor.height) {
                val value = rawImageProcessor.get(x, y).toUInt()

                if (value.and(0xFF00u) == 0xFF00u) {
                    val shortesPoint = findShortestPointOnParabola(parabolaPoints, Point(x, y))

                    if (shortesPoint != null) {
                        val linePoints = calc.getIntersectionPixels(Point(x, y), shortesPoint)
                        for (p in linePoints) {
                            val value1 = rawImageProcessor.get(p.x, p.y).toUInt()
                            if (value1.and(0xFF0000u) == 0xFF0000u) {
                                resultImageProcessor.drawLine(x, y, p.x, p.y)
                                break
                            }
                        }
                    }

                }
            }
        }

        for (x in 155 until 356) {

            var foundHost: Point? = null
            var foundGraft: Point? = null

            for (y in 0 until rawImageProcessor.height) {

                val value = rawImageProcessor.get(x, y).toUInt()

                if (value == 0xFF000000u)
                    continue

                if (foundHost == null && value.and(0xFF0000u) == 0xFF0000u) {
                    foundHost = Point(x, y)
                } else if (value.and(0xFF00u) == 0xFF00u) {
                    foundGraft = Point(x, y)
                }
            }

            if (foundHost != null && foundGraft != null) {
                resultImageProcessor.drawLine(foundHost.x, foundHost.y, foundGraft.x, foundGraft.y)
            }
        }

        return resultImage
    }

    private fun findShortestPointOnParabola(points: List<Point>, orig: Point): Point? {
        var dist = Double.MAX_VALUE
        var result: Point? = null

        for (point in points) {
            val d = point.distance(orig)
            if (d < dist) {
                dist = d
                result = point
            }
        }
        return result
    }

    private fun getParabolaPoints(): List<Point> {
        val parabola = Parabola(Point(256, 75), 0.004F)
        val points = mutableListOf<Point>()
        for (x in 0 until 512) {
            val y = parabola.calcY(x)
            points.add(Point(x, y))
        }
        return points
    }
}

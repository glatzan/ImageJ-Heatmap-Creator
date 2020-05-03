package eu.glatz.imagej.legacy

import eu.glatz.imagej.heatmap.ray.Parabola
import eu.glatz.imagej.model.Point
import eu.glatz.imagej.util.DrawUtil
import eu.glatz.imagej.util.LineFinder
import ij.IJ
import ij.ImagePlus
import ij.plugin.filter.PlugInFilter
import ij.process.ImageProcessor
import java.awt.Color
import kotlin.math.floor


class HostParabola : PlugInFilter {

    var imp: ImagePlus? = null
    var startXScanAt = 375
    var xScanWidth = 600
    var startYScanAt = 6
    var yScanHeight = 400

    var debug = false

    companion object {
        @JvmStatic
        lateinit var hostParabola: Parabola
    }

    var lineRemoveThreshold = 50

    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        return PlugInFilter.DOES_ALL
    }

    override fun run(ip: ImageProcessor) {
        IJ.run(imp, "Canny Edge Detector", "gaussian=3 low=3 high=5")

        val processor = imp!!.processor
        val out2 = processor.duplicate().convertToRGB()

        val lines = LineFinder.findConnectedRegion(processor, 3)
        val lineImage = IJ.createImage("AcceptedLines", "8-bit black", processor.width, processor.height, 1)
        val edgeDetection = IJ.createImage("EdgeDetection", "RGB", processor.width, processor.height, 1)
        val acceptedLinesProcessor = lineImage.processor
        val edgeDetectionProcessor = edgeDetection.processor

        lines.forEach { line ->
            if (line.length < lineRemoveThreshold) {
                DrawUtil.drawLine(line, Color.RED, out2)
            } else {
                DrawUtil.drawLine(line, Color.WHITE, acceptedLinesProcessor)
                DrawUtil.drawLine(line, Color.GREEN, out2)
            }
        }

        var topPointList = mutableListOf<Point>()

        val kernelIter = floor((3 / 2).toDouble()).toInt()

        for (x in startXScanAt + kernelIter until xScanWidth + startXScanAt - kernelIter) {
            var first = false
            for (y in startYScanAt + kernelIter until yScanHeight + startYScanAt - kernelIter) {

                var line = 0;
                for (x_kernel in -kernelIter..kernelIter) {
                    for (y_kernel in -kernelIter..kernelIter) {
                        line += acceptedLinesProcessor.get(x + x_kernel, y + y_kernel)
                    }
                }

                line = line / (3 * 3)

                if (line != 0) {
                    if (!first) {
                        edgeDetectionProcessor.setColor(Color.BLUE)
                        topPointList.add(Point(x, y))
                        first = true
                    } else
                        edgeDetectionProcessor.setColor(if (line < 0) Color.RED else Color.GREEN)

                    edgeDetectionProcessor.drawDot(x, y)
                }

//                println(line)

            }
//            println(".....")
        }

        val topPoint = findTopPoint(topPointList, edgeDetectionProcessor)

//        val parabola = Parabola(topPoint, 0.001F)
//        parabola.draw(edgeDetectionProcessor, Color.BLACK, 0, edgeDetectionProcessor.width)
//
//        if(debug) {
//            ImagePlus("HostParabola_AcceptedLines", out2).show()
//            ImagePlus("HostParabola_Grey", acceptedLinesProcessor).show()
//            ImagePlus("HostParabola_EdgeDetection", edgeDetectionProcessor).show()
//        }
//        hostParabola = parabola
    }

    fun findTopPoint(points: List<Point>, out: ImageProcessor): Point {

        fun loop(points: List<Point>): Point {
            var minPoint = Point(Int.MAX_VALUE, Int.MAX_VALUE)
            var sameY = 0

            for (point in points) {
                if (point.y < minPoint.y) {
                    minPoint = point
                    sameY = 0;
                } else if (point.y == minPoint.y) {
                    if (sameY < 0)
                        sameY = 0
                    sameY++
                } else {
                    sameY--
                }

                if (sameY < -20) {
                    out.setColor(Color.MAGENTA)
                    out.drawDot(point.x, point.y)
                    break
                }
            }

            return minPoint
        }

        val pointRight = loop(points)
        out.setColor(Color.ORANGE)
        out.drawDot(pointRight.x, pointRight.y)

        val pointLeft = loop(points.reversed())
        out.setColor(Color.ORANGE)
        out.drawDot(pointLeft.x, pointLeft.y)

        val result = Point(((pointRight.x + pointLeft.x) / 2), (pointRight.y + pointLeft.y) / 2)

        out.setColor(Color.RED)
        out.drawDot(result.x, result.y)

        return result
    }

}
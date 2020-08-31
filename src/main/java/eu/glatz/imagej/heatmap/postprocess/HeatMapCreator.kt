package eu.glatz.imagej.heatmap.postprocess

import ij.IJ
import ij.ImagePlus
import ij.ImageStack
import ij.measure.ResultsTable
import java.awt.Color
import java.awt.Point
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

object HeatMapCreator {

    fun heatMapFromFolder(folder: File, totalDegreeSpan: Double = 180.0): HeatMap {
        val stack = NetImageProbabilityMapCreator.loadStack(folder).imageStack
        return heatMapFromLoadedStack(stack, totalDegreeSpan)
    }

    fun heatMapFromProbabilityMap(probabilityMap: ProbabilityMap, totalDegreeSpan: Double = 180.0, threshold: Float = 1.0F): HeatMap {
        val stack = NetImageProbabilityMapCreator.toBinaryImageStack(probabilityMap)
        return heatMapFromLoadedStack(stack, totalDegreeSpan)
    }

    fun heatMapFromLoadedStack(imageStack: ImageStack, totalDegreeSpan: Double = 180.0): HeatMap {
        val imageCount = imageStack.size

        val slideDegree = totalDegreeSpan / imageCount
        val centerOffset = (imageStack.getProcessor(1).width / 2).toInt()
        val maxWidth = imageStack.getProcessor(1).width
        val maxHeight = imageStack.getProcessor(1).height

        val result = HeatMap(maxWidth, maxHeight)

        for (imageNumber in 0 until imageCount) {
            val currentImageProcessor = imageStack.getProcessor(imageNumber + 1)

            if (currentImageProcessor.width != maxWidth || currentImageProcessor.height != maxHeight)
                throw IllegalStateException("Images must match dimension! width: $maxWidth,  height: $maxHeight (current is: ${currentImageProcessor.width} / ${currentImageProcessor.height})")

            var columnValue = 0
            for (x in 0 until maxWidth) {
                for (y in 0 until maxHeight) {
                    if (currentImageProcessor.get(x, y) > 0)
                        columnValue++;
                }

                if (columnValue != 0) {
                    val cartesianPoint = calculateCartesianFromRadial(x, imageNumber * slideDegree, centerOffset, maxWidth - 1, maxHeight - 1)
                    result.data[cartesianPoint.x][cartesianPoint.y] = columnValue
                }

                columnValue = 0
            }
        }

        return result
    }

    fun heatmapToInterpolatedImage(heatMap: HeatMap, interpolationKernel: Int = 3, imageName: String = "Heatmap", greyScale: Boolean = false, interpolate: Boolean = true): ImagePlus {
        val kernelOffset = Math.floor(((interpolationKernel.toDouble() / 2))).toInt()
        val image = if (!greyScale) IJ.createImage(imageName, "RGB", heatMap.width, heatMap.height, 1) else IJ.createImage(imageName, 512, 512, 1, 8)
        val pB = image.processor
        if (!greyScale) pB.setColor(Color(255, 255, 191)) else pB.setColor(Color.WHITE)
        pB.fillRect(0, 0, heatMap.width, heatMap.height)

        val max = heatMap.findMaxValue()

        var value = 0
        var count = 0

        for (x in kernelOffset until heatMap.width - kernelOffset - 1 step 1) {
            for (y in kernelOffset until heatMap.height - kernelOffset - 1 step 1) {

                if (interpolate)
                    for (xx in -kernelOffset..kernelOffset) {
                        for (yy in -kernelOffset..kernelOffset) {
                            if (heatMap.data[x + xx][y + yy] != 0) {
                                value += heatMap.data[x + xx][y + yy]
                                count++
                            }
                        }
                    } else {
                    count = 1
                    value = 255 - heatMap.data[x][y]
                }

                if (count > 0) {
                    if (!greyScale) {
                        val calc = 1 - (value / count).toDouble() / max
                        pB.setColor(Color(255, (255 * calc).toInt(), (191 * calc).toInt()))
                    } else pB.setColor(value)
                    pB.drawPixel(x, y)
                }

                value = 0
                count = 0
            }
        }

        return image
    }

    fun saveScatteredHeatMapToCVS(heatMap: Array<IntArray>, target: File) {
        val results = ResultsTable()
        results.incrementCounter()

        for (x in 0 until heatMap.size) {
            for (y in 0 until heatMap[x].size) {
                if (heatMap[x][y] != 0) {
                    results.addValue("x", x.toDouble() - 512)
                    results.addValue("y", y.toDouble() - 512)
                    results.addValue("z", heatMap[x][y].toDouble())
                    results.incrementCounter()
                }
            }
        }
        results.save(target.path)
    }

     fun calculateCartesianFromRadial(length: Int, r: Double, center: Int, maxX: Int, maxY: Int): Point {
        val x = ((length - center) * cos(2 * Math.PI - Math.toRadians(r))).toInt() + center
        val y = ((length - center) * sin(2 * Math.PI - Math.toRadians(r))).toInt() + center
        return Point(if (x <= maxX) x else maxX, if (x <= maxY) y else maxY)
    }
}

package eu.glatz.imagej.heatmap

import ij.IJ
import ij.ImagePlus
import ij.ImageStack
import ij.measure.ResultsTable
import ij.plugin.FolderOpener
import ij.process.ColorProcessor
import java.awt.Color
import java.awt.Point
import java.io.File
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin


class HeatMapProcessor {


    fun loadSimpleHeatMap(folder: File, totalDegreeSpan: Double = 180.0): HeatMap {
        val imageStack = loadStack(folder)
        val imageCount = imageStack.imageStackSize

        val slideDegree = totalDegreeSpan / imageCount
        val centerOffset = (imageStack.imageStack.getProcessor(1).width / 2).toInt()

        val maxWidth = imageStack.imageStack.getProcessor(1).width
        val maxHeight = imageStack.imageStack.getProcessor(1).height

        val result = HeatMap(maxWidth, maxHeight)

        for (imageNumber in 0 until imageCount) {
            val currentImageProcessor = imageStack.imageStack.getProcessor(imageNumber + 1)

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

    fun loadProbabilityMap(folder: File, weights: FloatArray = floatArrayOf(0.05F, 0.1F, 0.2F, 0.2F, 0.5F, 0.2F, 0.2F, 0.1F, 0.05F)): ProbabilityMap {
        val imageStack = loadStack(folder)
        val imageCount = imageStack.imageStackSize

        if (weights.size % 2 == 0)
            throw IllegalArgumentException("Weights array must have an odd number of elements")

        val maxWidth = imageStack.imageStack.getProcessor(1).width
        val maxHeight = imageStack.imageStack.getProcessor(1).height

        val probabilityMap = ProbabilityMap(maxWidth, maxHeight, imageCount)

        val weightsOffset = floor(weights.size / 2F).toInt()

        for (i in 0 until imageCount) {
            val currentImageProcessor = imageStack.imageStack.getProcessor(i + 1)

            if (currentImageProcessor.width != maxWidth || currentImageProcessor.height != maxHeight)
                throw IllegalStateException("Images must match dimension! width: $maxWidth,  height: $maxHeight (current is: ${currentImageProcessor.width} / ${currentImageProcessor.height})")

            for (x in 0 until maxWidth) {
                for (y in 0 until maxHeight) {
                    for (n in -weightsOffset..weightsOffset) {
                        when {
                            i + n >= imageCount -> probabilityMap.data[(i + n) - imageCount][maxWidth - 1 - x][y] += if (currentImageProcessor.get(x, y) > 0) 1 * weights[n + weightsOffset] else 0F
                            i + n < 0 -> probabilityMap.data[imageCount + (i + n)][maxWidth - 1 - x][y] += if (currentImageProcessor.get(x, y) > 0) 1 * weights[n + weightsOffset] else 0F
                            else -> probabilityMap.data[(i + n)][x][y] += if (currentImageProcessor.get(x, y) > 0) 1 * weights[n + weightsOffset] else 0F
                        }
                    }
                }
            }
        }
        return probabilityMap
    }

    fun convertProbabilityMapToHeatMap(probabilityMap: ProbabilityMap, totalDegreeSpan: Double = 180.0): HeatMap {
        val imageCount = probabilityMap.count

        val slideDegree = totalDegreeSpan / imageCount
        val centerOffset = (probabilityMap.width / 2).toInt()

        val result = HeatMap(probabilityMap.width, probabilityMap.height)

        for (counter in 0 until imageCount) {
            val current = probabilityMap.data[counter]

            var columnValue = 0
            for (x in 0 until probabilityMap.width) {
                for (y in 0 until probabilityMap.height) {
                    if (current[x][y] > 0)
                        columnValue++;
                }

                if (columnValue != 0) {
                    val cartesianPoint = calculateCartesianFromRadial(x, counter * slideDegree, centerOffset, probabilityMap.width - 1, probabilityMap.height - 1)
                    result.data[cartesianPoint.x][cartesianPoint.y] = columnValue
                }

                columnValue = 0
            }
        }

        return result
    }

    fun probabilityMapImageStack(probabilityMap: ProbabilityMap): ImageStack {
        val probabilityStack = ImageStack(probabilityMap.width, probabilityMap.height)

        for (i in 0 until probabilityMap.count) {
            val processor = ColorProcessor(probabilityMap.width, probabilityMap.height)

            for (x in 0 until probabilityMap.width) {

                var found = 0
                for (y in 0 until probabilityMap.height) {
                    val prob = probabilityMap.data[i - 1][x][y]
                    if (prob > 0) {
                        if (prob >= 10)
                            processor.setColor(Color(255, 255, 255))
                        else if (prob >= 1) {
                            processor.setColor(Color(255, 255, 0))
                            found++
                        } else
                            processor.setColor(Color((1 - prob), prob, 0F))
                    } else {
                        processor.setColor(0)
                    }
                    processor.drawPixel(x, y)
                }
            }
            probabilityStack.addSlice(processor)
        }

        return probabilityStack
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

    fun createHeatMap(heatMap: HeatMap, interpolationKernel: Int = 3): ImagePlus {
        val kernelOffset = Math.floor(((interpolationKernel.toDouble() / 2))).toInt()
        val image = IJ.createImage("Heatmap", "RGB", heatMap.width, heatMap.height, 1)
        val pB = image.processor as ColorProcessor
        pB.setColor(Color(255, 255, 191))
        pB.fillRect(0, 0, heatMap.width, heatMap.height)

        val max = heatMap.findMaxValue()

        var value = 0
        var count = 0

        for (x in kernelOffset until heatMap.width - kernelOffset - 1 step 1) {
            for (y in kernelOffset until heatMap.height - kernelOffset - 1 step 1) {

                for (xx in -kernelOffset..kernelOffset) {
                    for (yy in -kernelOffset..kernelOffset) {
                        if (heatMap.data[x + xx][y + yy] != 0) {
                            value += heatMap.data[x + xx][y + yy]
                            count++
                        }
                    }
                }

                if (count > 0) {
                    val calc = 1 - (value / count).toDouble() / max
                    pB.setColor(Color(255, (255 * calc).toInt(), (191 * calc).toInt()))
                    pB.drawPixel(x, y)
                }

                value = 0
                count = 0
            }
        }

        return image
    }

    private fun calculateCartesianFromRadial(length: Int, r: Double, center: Int, maxX: Int, maxY: Int): Point {
        val x = ((length - center) * cos(2 * Math.PI - Math.toRadians(r))).toInt() + center
        val y = ((length - center) * sin(2 * Math.PI - Math.toRadians(r))).toInt() + center
        return Point(if (x <= maxX) x else maxX, if (x <= maxY) y else maxY)
    }

    private fun loadStack(folder: File): ImagePlus {
        val imageStack = FolderOpener.open(folder.path)

        if (imageStack.imageStackSize <= 1)
            throw IllegalStateException("Images not found!")

        return imageStack
    }
}

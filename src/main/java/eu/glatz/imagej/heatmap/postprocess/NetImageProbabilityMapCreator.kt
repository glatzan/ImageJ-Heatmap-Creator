package eu.glatz.imagej.heatmap.postprocess

import ij.ImagePlus
import ij.ImageStack
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.process.ByteProcessor
import ij.process.ColorProcessor
import java.awt.Color
import java.io.File
import kotlin.math.floor

object NetImageProbabilityMapCreator {

    fun convertToProbabilityMap(folder: File, weights: FloatArray = floatArrayOf(0.1F, 0.1F, 0.2F, 0.3F, 0.5F, 0.3F, 0.2F, 0.1F, 0.1F)): ProbabilityMap {
        val imageStack = loadStack(folder)
        val imageCount = imageStack.imageStackSize

        if (weights.size % 2 == 0)
            throw IllegalArgumentException("Weights array must have an odd number of elements")

        val maxWidth = imageStack.imageStack.getProcessor(1).width
        val maxHeight = imageStack.imageStack.getProcessor(1).height

        val probabilityMap = ProbabilityMap(imageCount, maxWidth, maxHeight)

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
                            i + n < 0 -> probabilityMap.data[imageCount + (i + n)][maxWidth - 1 - x][y] += if (currentImageProcessor.get(x, y) > 0) weights[n + weightsOffset] else 0F
                            else -> probabilityMap.data[(i + n)][x][y] += if (currentImageProcessor.get(x, y) > 0) 1 * weights[n + weightsOffset] else 0F
                        }
                    }
                }
            }
        }
        return probabilityMap
    }

    fun toColorCodedImageStack(probabilityMap: ProbabilityMap): ImageStack {
        val probabilityStack = ImageStack(probabilityMap.width, probabilityMap.height)

        for (i in 0 until probabilityMap.count) {
            val processor = ColorProcessor(probabilityMap.width, probabilityMap.height)

            for (x in 0 until probabilityMap.width) {

                for (y in 0 until probabilityMap.height) {
                    val prob = probabilityMap.data[i][x][y]
                    if (prob > 0) {
                        if (prob >= 10)
                            processor.setColor(Color(255, 255, 255))
                        else if (prob >= 1) {
                            processor.setColor(Color(255, 255, 0))
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

    fun toBinaryImageStack(probabilityMap: ProbabilityMap,threshold: Float = 1.0F): ImageStack {
        val probabilityStack = ImageStack(probabilityMap.width, probabilityMap.height)

        for (i in 0 until probabilityMap.count) {
            val processor = ByteProcessor(probabilityMap.width, probabilityMap.height)

            for (x in 0 until probabilityMap.width) {

                for (y in 0 until probabilityMap.height) {
                    val prob = probabilityMap.data[i][x][y]
                    if (prob >= threshold) {
                        processor.setColor(Color.WHITE)
                    } else
                        processor.setColor(Color.BLACK)
                    processor.drawPixel(x, y)
                }
            }
            probabilityStack.addSlice(processor)
        }
        return probabilityStack
    }

    fun writeAsStackToFolder(probabilityMap: ProbabilityMap, targetFolder: File, binary: Boolean = true, suffix: String = "") {
        val stack = if (binary) toBinaryImageStack(probabilityMap) else toColorCodedImageStack(probabilityMap)
        for (i in 0 until stack.size) {
            val pro = stack.getProcessor(i + 1)
            FileSaver(ImagePlus("", pro)).saveAsPng(File(targetFolder, "${i}${suffix}.png").absolutePath)
        }
    }

    @JvmStatic
    fun loadStack(folder: File): ImagePlus {
        val imageStack = FolderOpener.open(folder.path)

        if (imageStack.imageStackSize <= 1)
            throw IllegalStateException("Images not found!")

        return imageStack
    }
}

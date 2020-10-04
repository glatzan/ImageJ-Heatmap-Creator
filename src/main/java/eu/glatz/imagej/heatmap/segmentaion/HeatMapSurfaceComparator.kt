package eu.glatz.imagej.heatmap.segmentaion

import eu.glatz.imagej.heatmap.segmentaion.output.ImageKeyFigureData
import ij.IJ
import ij.ImagePlus
import ij.ImageStack
import ij.io.FileSaver
import ij.process.ImageProcessor
import java.awt.Color
import java.io.File

object HeatMapSurfaceComparator {


    fun compareImageStacks(name: String, maskStack: ImageStack, netStack: ImageStack, targetFile: File, inverted: Boolean): ImageKeyFigureData {
        val heatmapResult = ImageKeyFigureData()
        heatmapResult.name = name

        for (i in 0 until maskStack.size) {
            heatmapResult + compareImage(maskStack.getProcessor(i + 1), netStack.getProcessor(i + 1), targetFile, inverted)
        }

        return heatmapResult
    }

    fun compareImage(mask: ImageProcessor, net: ImageProcessor, targetFile: File, inverted: Boolean): ImageKeyFigureData {
        val resultImage = IJ.createImage("Parabola", "RGB", 512, 512, 1)
        val resultProcessor = resultImage.processor

        val heatmapResult = ImageKeyFigureData()
        for (x in 0 until mask.width) {
            for (y in 0 until mask.height) {

                val mValue = mask.get(x, y)
                val nValue = net.get(x, y)

                if (inverted) {
                    if (mValue < 255 && nValue < 255) {
                        resultProcessor.setColor(Color.GREEN)
                        resultProcessor.drawDot(x, y)
                        heatmapResult.overlappingNetMaskPixelCount++
                        heatmapResult.overlappingMaskTotalPixelCount++
                        heatmapResult.overlappingNetTotalPixelCount++
                    } else if (mValue < 255) {
                        resultProcessor.setColor(Color.RED)
                        resultProcessor.drawDot(x, y)
                        heatmapResult.noneOverlappingMaskPixelCount++
                    } else if (nValue < 255) {
                        resultProcessor.setColor(Color.YELLOW)
                        resultProcessor.drawDot(x, y)
                        heatmapResult.noneOverlappingNetPixelCount++
                    }
                } else {
                    if (mValue > 0 && nValue > 0) {
                        resultProcessor.setColor(Color.GREEN)
                        resultProcessor.drawDot(x, y)
                        heatmapResult.overlappingNetMaskPixelCount++
                        heatmapResult.overlappingMaskTotalPixelCount++
                        heatmapResult.overlappingNetTotalPixelCount++
                    } else if (mValue > 0) {
                        resultProcessor.setColor(Color.RED)
                        resultProcessor.drawDot(x, y)
                        heatmapResult.noneOverlappingMaskPixelCount++
                    } else if (nValue > 0) {
                        resultProcessor.setColor(Color.YELLOW)
                        resultProcessor.drawDot(x, y)
                        heatmapResult.noneOverlappingNetPixelCount++
                    }
                }

            }
        }

        FileSaver(resultImage).saveAsPng(targetFile.absolutePath)

        return heatmapResult
    }
}

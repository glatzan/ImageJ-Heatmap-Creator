package eu.glatz.imagej.heatmap.segmentaion.output

import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import ij.IJ
import ij.ImagePlus
import ij.process.ImageProcessor
import java.awt.Color
import kotlin.math.max

class ImageSegmentationDrawer {

    fun createSegmentationImage(segmentationList: List<OverlappingSegmentResult>, titel: String, imageHeight: Int = 512) : ImagePlus {
        val resultImage = IJ.createImage("Segmentation Result", "RGB", segmentationList.size * 2, imageHeight, 1)
        val resultProcessor = resultImage.processor

        val imageData = getDrawData(segmentationList)

        val maxValue = maxValue(imageData)

        for ((index, data) in imageData.withIndex()) {
            drawSegment(data, resultProcessor, index, maxValue)
        }

        resultProcessor.setColor(Color.BLACK)
        resultProcessor.drawString("Name: ${titel}", 10, 30)
//        resultProcessor.drawString("MAX: ${maxOverlapping - 20} ", 10, 50)
//        resultProcessor.drawString("Orig Arr: ${arr1} = 1.0", 10, 70)
//        resultProcessor.drawString("Comapre Arr: ${arr2} = ${arr2.toDouble()/arr1.toDouble()} ", 10, 90)

        resultImage.updateAndDraw()
        return resultImage
    }

    fun drawSegment(data: ImageSegmentationDrawData, processor: ImageProcessor, index: Int, maxValue: Int) {
        if (data.olMaskCount >= data.olNetCount) {
            processor.setColor(Color.ORANGE)
            processor.drawRect((index * 2), 512 - (data.olNetCount * 512 / maxValue).toInt(), 2, (data.olNetCount * 512 / maxValue).toInt())
            processor.setColor(Color.GREEN)
            processor.drawRect((index * 2), 512 - (data.olMaskCount * 512 / maxValue).toInt(), 2, ((data.olMaskCount * 512 / maxValue).toInt() - (data.olNetCount * 512 / maxValue).toInt()))
        } else {
            processor.setColor(Color.ORANGE)
            processor.drawRect((index * 2), 512 - (data.olMaskCount * 512 / maxValue).toInt(), 2, (data.olMaskCount * 512 / maxValue).toInt())
            processor.setColor(Color.GREEN)
            processor.drawRect((index * 2), 512 - (data.olNetCount * 512 / maxValue).toInt(), 2, (((data.olNetCount * 512 / maxValue).toInt() - data.olMaskCount * 512 / maxValue).toInt()))
        }

        processor.setColor(Color.BLACK)
        processor.drawLine(index * 2, 512 - (data.olMatchCount * 512 / maxValue).toInt(), index * 2 + 1, 512 - (data.olMatchCount * 512 / maxValue).toInt())

        processor.setColor(Color.GRAY)
        processor.drawRect((index * 2), 0, 2, ((data.nOlNetCount * 512 / maxValue).toInt()))

    }

    fun getDrawData(segmentationList: List<OverlappingSegmentResult>): Array<ImageSegmentationDrawData> {
        val imageValues = Array<ImageSegmentationDrawData>(segmentationList.size) { ImageSegmentationDrawData() }

        for ((index, segment) in segmentationList.withIndex()) {
            for (os in segment.overlappingSegments) {
                imageValues[index].olMaskCount += os.maskSegment.positivePixelCount
                imageValues[index].olNetCount += os.netSegment.positivePixelCount
                imageValues[index].olMatchCount += os.overlappingCount
            }

            for (ms in segment.nonOverlappingMaskSegments)
                imageValues[index].olMaskCount += ms.positivePixelCount

            for (ns in segment.nonOverlappingNetSegments)
                imageValues[index].nOlNetCount += ns.positivePixelCount
        }

        return imageValues
    }

    private fun maxValue(drawData: Array<ImageSegmentationDrawData>): Int {
        var max = Int.MIN_VALUE

        for (data in drawData) {
            max = max(max, data.olMaskCount + data.nOlNetCount + 5)
            max = max(max, data.olMatchCount + data.nOlNetCount + 5)
            max = max(max, data.olNetCount + data.nOlNetCount + 5)
        }

        return max
    }
}
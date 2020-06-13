package eu.glatz.imagej.heatmap.segmentaion.output

import eu.glatz.imagej.heatmap.segmentaion.ImageSegment
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import ij.IJ
import ij.ImagePlus
import ij.process.ImageProcessor
import java.awt.Color
import kotlin.math.max

class ImageSegmentationDrawer {

    fun createSegmentationImage(segmentationList: List<OverlappingSegmentResult>, titel: String, imageHeight: Int = 512, drawKeyFigures: Boolean = true): Pair<ImagePlus, ImageKeyFigureData> {
        val resultImage = IJ.createImage(titel, "RGB", segmentationList.size * 2, imageHeight, 1)
        val resultProcessor = resultImage.processor

        val imageData = getImageKeyFigureData(segmentationList)

        val maxValue = maxValue(imageData)

        for ((index, data) in imageData.withIndex()) {
            drawSegment(data, resultProcessor, index, maxValue)
        }

        val stackKeyFigureData = getKeyFigureDataForStack(imageData, titel)

        resultProcessor.setColor(Color.BLACK)
        resultProcessor.drawString("Name: ${titel}", 10, 30)
        resultProcessor.drawString("Matching: ${stackKeyFigureData.overlappingNetMaskPixelCount} ", 10, 50)
        resultProcessor.drawString("Net/Mask: ${if (stackKeyFigureData.overlappingMaskTotalPixelCount > 0) stackKeyFigureData.overlappingNetTotalPixelCount.toDouble() / stackKeyFigureData.overlappingMaskTotalPixelCount.toDouble() else 0}", 10, 70)
        resultProcessor.drawString("Not matched Net Pixel: ${stackKeyFigureData.noneOverlappingNetPixelCount} (${if (stackKeyFigureData.overlappingMaskTotalPixelCount > 0) stackKeyFigureData.noneOverlappingNetPixelCount.toDouble() / stackKeyFigureData.overlappingMaskTotalPixelCount.toDouble() else 0})", 10, 90)

        resultImage.updateAndDraw()
        return Pair(resultImage, stackKeyFigureData)
    }

    fun drawSegment(data: ImageKeyFigureData, processor: ImageProcessor, index: Int, maxValue: Int) {
        if (data.overlappingMaskTotalPixelCount >= data.overlappingNetTotalPixelCount) {
            processor.setColor(Color.ORANGE)
            processor.drawRect((index * 2), 512 - (data.overlappingNetTotalPixelCount * 512 / maxValue).toInt(), 2, (data.overlappingNetTotalPixelCount * 512 / maxValue).toInt())
            processor.setColor(Color.RED)
            processor.drawRect((index * 2), 512 - (data.overlappingMaskTotalPixelCount * 512 / maxValue).toInt(), 2, ((data.overlappingMaskTotalPixelCount * 512 / maxValue).toInt() - (data.overlappingNetTotalPixelCount * 512 / maxValue).toInt()))
        } else {
            processor.setColor(Color.ORANGE)
            processor.drawRect((index * 2), 512 - (data.overlappingMaskTotalPixelCount * 512 / maxValue).toInt(), 2, (data.overlappingMaskTotalPixelCount * 512 / maxValue).toInt())
            processor.setColor(Color.GREEN)
            processor.drawRect((index * 2), 512 - (data.overlappingNetTotalPixelCount * 512 / maxValue).toInt(), 2, (((data.overlappingNetTotalPixelCount * 512 / maxValue).toInt() - data.overlappingMaskTotalPixelCount * 512 / maxValue).toInt()))
        }

        processor.setColor(Color.BLACK)
        processor.drawLine(index * 2, 512 - (data.overlappingNetMaskPixelCount * 512 / maxValue).toInt(), index * 2 + 1, 512 - (data.overlappingNetMaskPixelCount * 512 / maxValue).toInt())

        processor.setColor(Color.GRAY)
        processor.drawRect((index * 2), 0, 2, ((data.noneOverlappingNetPixelCount * 512 / maxValue).toInt()))

    }

    /**
     * Creates Key Figures for every image in Stack (OverlappingSegmentResult -> ImageKeyFigureData)
     */
    fun getImageKeyFigureData(segmentationList: List<OverlappingSegmentResult>): Array<ImageKeyFigureData> {
        val imageValues = Array<ImageKeyFigureData>(segmentationList.size) { ImageKeyFigureData() }
        val uniqueMasks = mutableListOf<ImageSegment>()
        val uniqueNet = mutableListOf<ImageSegment>()

        for ((index, segment) in segmentationList.withIndex()) {

            for (os in segment.overlappingSegments) {
                if (uniqueMasks.contains(os.maskSegment)) {
                    imageValues[index].overlappingMaskNonUniqueTotalPixelCount += os.maskSegment.positivePixelCount
                } else {
                    imageValues[index].overlappingMaskTotalPixelCount += os.maskSegment.positivePixelCount
                    uniqueMasks.add(os.maskSegment)
                }

                if (uniqueNet.contains(os.netSegment)) {
                    imageValues[index].overlappingNetNonUniqueTotalPixelCount += os.netSegment.positivePixelCount
                } else {
                    imageValues[index].overlappingNetTotalPixelCount += os.netSegment.positivePixelCount
                    uniqueNet.add(os.netSegment)
                }

                imageValues[index].overlappingNetMaskPixelCount += os.overlappingCount
            }

            imageValues[index].overlappingSegmentsCount = segment.overlappingSegments.size

            for (ms in segment.nonOverlappingMaskSegments)
                imageValues[index].noneOverlappingMaskPixelCount += ms.positivePixelCount

            imageValues[index].nonOverlappingMaskCount = segment.nonOverlappingMaskSegments.size

            for (ns in segment.nonOverlappingNetSegments)
                imageValues[index].noneOverlappingNetPixelCount += ns.positivePixelCount

            imageValues[index].nonOverlappingNetCount = segment.nonOverlappingNetSegments.size
        }

        return imageValues
    }

    private fun maxValue(drawData: Array<ImageKeyFigureData>): Int {
        var max = Int.MIN_VALUE
        for (data in drawData) {
            max = max(max, data.overlappingMaskTotalPixelCount + data.noneOverlappingNetPixelCount + 5)
            max = max(max, data.overlappingNetMaskPixelCount + data.noneOverlappingNetPixelCount + 5)
            max = max(max, data.overlappingNetTotalPixelCount + data.noneOverlappingNetPixelCount + 5)
        }
        return max
    }

    fun getKeyFigureDataForStack(segmentationList: List<OverlappingSegmentResult>, name: String): ImageKeyFigureData {
        return getKeyFigureDataForStack(getImageKeyFigureData(segmentationList), name)
    }

    fun getKeyFigureDataForStack(keyFigureData: Array<ImageKeyFigureData>, name: String): ImageKeyFigureData {
        val result = ImageKeyFigureData()
        result.name = name
        for (keyFigure in keyFigureData) {
            result.overlappingNetTotalPixelCount += keyFigure.overlappingNetTotalPixelCount
            result.overlappingMaskTotalPixelCount += keyFigure.overlappingMaskTotalPixelCount
            result.overlappingNetMaskPixelCount += keyFigure.overlappingNetMaskPixelCount
            result.noneOverlappingNetPixelCount += keyFigure.noneOverlappingNetPixelCount
            result.noneOverlappingMaskPixelCount += keyFigure.noneOverlappingMaskPixelCount
            result.overlappingSegmentsCount += keyFigure.overlappingSegmentsCount
            result.nonOverlappingNetCount += keyFigure.nonOverlappingNetCount
            result.nonOverlappingMaskCount += keyFigure.nonOverlappingMaskCount
        }
        return result;
    }
}
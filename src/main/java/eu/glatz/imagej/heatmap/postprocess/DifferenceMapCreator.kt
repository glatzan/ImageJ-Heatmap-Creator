package eu.glatz.imagej.heatmap.postprocess

import ij.plugin.FolderOpener
import java.io.File
import kotlin.math.abs

object DifferenceMapCreator {

    fun differenceMapFromFolder(groundTruthFolder: File, netImageFolder: File, totalDegreeSpan: Double = 180.0): HeatMap {
        val opener = FolderOpener()
        val masks = opener.openFolder(groundTruthFolder.path)
        val net = opener.openFolder(netImageFolder.path)

        val imageCount = masks.stackSize
        val slideDegree = totalDegreeSpan / imageCount
        val centerOffset = (masks.stack.getProcessor(1).width / 2).toInt()
        val maxWidth = masks.stack.getProcessor(1).width
        val maxHeight = masks.stack.getProcessor(1).height

        val resultMap = HeatMap(maxWidth, maxHeight)

        for (imageNumber in 0 until imageCount) {
            val maskProcessor = masks.stack.getProcessor(imageNumber + 1)
            val netProcessor = net.stack.getProcessor(imageNumber + 1)

            for (x in 0 until maskProcessor.width) {
                var maskCount = 0
                var netCount = 0

                for (y in 0 until maskProcessor.height) {
                    if (maskProcessor.get(x, y) > 0)
                        maskCount++

                    if (netProcessor.get(x, y) > 0)
                        netCount++
                }

                val result = abs(maskCount - netCount)

                if (result != 0) {
                    val cartesianPoint = HeatMapCreator.calculateCartesianFromRadial(x, imageNumber * slideDegree, centerOffset, maxWidth - 1, maxHeight - 1)
                    resultMap.data[cartesianPoint.x][cartesianPoint.y] = result
                }
            }
        }

        return resultMap
    }
}

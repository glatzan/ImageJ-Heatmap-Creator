package eu.glatz.imagej.legacy

import eu.glatz.imagej.model.Point
import eu.glatz.imagej.model.SimpleLine
import ij.ImagePlus
import ij.plugin.filter.PlugInFilter
import ij.process.ImageProcessor

class CalculateDetachmentVolume : PlugInFilter {

    var imp: ImagePlus? = null
    var startXScanAt = 375
    var xScanWidth = 600
    var startYScanAt = 6
    var yScanHeight = 400

    companion object {
        @JvmStatic
        lateinit var result: MutableList<SimpleLine>
    }

    var lineRemoveThreshold = 50

    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        return PlugInFilter.DOES_ALL
    }

    override fun run(ip: ImageProcessor) {

        val result = mutableListOf<SimpleLine>()

        for (x in 0 until ip.width) {

            var foundHost: Int = -1
            var foundGraft: Int = -1

            for (y in 0 until ip.height) {

                val pix = imp!!.processor.get(x, y)

                if (pix == -1)
                    continue

                if (pix.and(0xFF0000) == 0xFF0000) {
                    foundHost = y
                } else if (pix.and(0xFF00) == 0xFF00) {
                    foundGraft = y
                }
            }

            if (foundHost != -1 && foundGraft != -1 && foundHost < foundGraft) {
                result.add(SimpleLine(result.size, listOf(Point(x, foundHost + 1), Point(x, foundGraft - 1))))
            }
        }

        Companion.result = result
    }
}

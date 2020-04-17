package eu.glatz.imagej

import eu.glatz.imagej.heatmap.HeatMapProcessor
import ij.IJ
import ij.plugin.PlugIn
import ij.plugin.filter.GaussianBlur
import java.io.File

class PreProcessedHeatMapRunner : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        if (args.size == 0) {
            IJ.error("Provide Arguments")
            return
        }

        val heatMapProcessor = HeatMapProcessor()

        for (element in args) {
            val imageFolder = File(element)
            if (!imageFolder.isDirectory) {
                IJ.error("Argument 1 is not a Folder!")
                return
            }

            val heatMap = heatMapProcessor.loadSimpleHeatMap(imageFolder)
            val heatMapImage = heatMapProcessor.drawInterpolatedHeatMap(heatMap)

            val blur = GaussianBlur()
            blur.blurGaussian(heatMapImage.processor,2.0)
            heatMapImage.show()
        }
    }
}
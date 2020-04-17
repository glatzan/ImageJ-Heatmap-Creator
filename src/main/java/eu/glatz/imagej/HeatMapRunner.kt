package eu.glatz.imagej

import eu.glatz.imagej.heatmap.HeatMapProcessor
import ij.IJ
import ij.ImagePlus
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File

class HeatMapRunner : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        if (args.size == 0) {
            IJ.error("Provide Arguments")
            return
        }

        val to = if (args.size == 1) 1 else args.size - 1

        for (i in 0 until to) {
            val imageFolder = File(args[i])
            if (!imageFolder.isDirectory) {
                IJ.error("Argument 1 is not a Folder!")
                return
            }

            val processor = HeatMapProcessor()
            val probabilityMap = processor.loadProbabilityMap(imageFolder)
            val probabilityImageStack = processor.convertProbabilityMapImageStack(probabilityMap)

            ImagePlus("${imageFolder.name}_Probability_Map", probabilityImageStack).show()

            val heatMap = processor.convertProbabilityMapToHeatMap(probabilityMap)

            val heatMapImage = processor.drawInterpolatedHeatMap(heatMap)
            heatMapImage.show()
        }

        if (args.size > 1) {
            val origFolder = File(args[args.size - 1])

            if (!origFolder.isDirectory) {
                IJ.error("Argument 2 is not a Folder!")
                return
            }

            val origStack = FolderOpener.open(origFolder.path)

            origStack.title = "Originale"
            origStack.show()
        }
    }
}
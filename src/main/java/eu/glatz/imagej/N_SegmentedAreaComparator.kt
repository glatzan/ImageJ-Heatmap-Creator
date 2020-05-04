package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import ij.IJ
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File

class N_SegmentedAreaComparator : PlugIn {
    override fun run(args: String) {
        val argArr = args.split(" ")

        if (argArr.size < 2) {
            IJ.error("Provide source and target Folder")
            return
        }

        val maskFolder = File(argArr[0])
        val netFolder = File(argArr[1])

        if (!maskFolder.isDirectory || !netFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        if (maskFolder.listFiles().size != netFolder.listFiles().size) {
            IJ.error("Number of pictures must match")
            return
        }

        val opener = FolderOpener()
        val masks = opener.openFolder(maskFolder.path)
        val net = opener.openFolder(netFolder.path)

        for (i in 0 until masks.stackSize) {
            val maskSegments = ImageSegmentation.imageSegmentation(masks.imageStack.getProcessor(i + 1))
            val netSegments = ImageSegmentation.imageSegmentation(net.imageStack.getProcessor(i + 1))

            val result = SegmentationComparator.compareSegmentation(maskSegments,netSegments)
            result
        }
    }
}

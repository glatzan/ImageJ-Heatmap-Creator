package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import ij.IJ
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File

class SegmentMaskAndNet : PlugIn {
    override fun run(args: String?) {
        val argArray = args?.split(" ") ?: return


        if (argArray.size != 2) {
            IJ.error("Provide two folders")
            return
        }


        val folders = argArray.map { File(it) }

        for (f in folders) {
            if (!f.isDirectory) {
                IJ.error("${f.path} is not a folder!")
                return
            }
        }

        if (folders[0].listFiles().size != folders[1].listFiles().size) {
            IJ.error("Number of pictures must match")
            return
        }

        val opener = FolderOpener()
        val masks = opener.openFolder(folders[0].path)
        val net = opener.openFolder(folders[1].path)

        for (i in 0 until masks.stackSize) {
            val maskSegments = ImageSegmentation.imageSegmentation(masks.imageStack.getProcessor(i + 1))
            val netSegments = ImageSegmentation.imageSegmentation(net.imageStack.getProcessor(i + 1))

            val result = SegmentationComparator.compareSegmentation(maskSegments,netSegments)
        }
    }
}
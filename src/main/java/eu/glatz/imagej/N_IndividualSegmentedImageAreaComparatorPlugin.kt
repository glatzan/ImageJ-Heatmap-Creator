package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationToCSV
import ij.IJ
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

/**
 * source comapre targetCSV
 */
class N_IndividualSegmentedImageAreaComparatorPlugin : PlugIn {
    override fun run(args: String) {
        val argArr = args.split(" ")
        var folderMode = false

        if (argArr.size < 3) {
            IJ.error("Provide source, groundtruth, target csv ")
            return
        }

        val groundTruthFolder = File(argArr[0])
        val netFolder = File(argArr[1])
        val targetCSVFile = File(argArr[2])

        if (!groundTruthFolder.isDirectory || !netFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        val groundTruthFiles = groundTruthFolder.listFiles().filter { Files.isDirectory(it.toPath()) }
        val netImageFiles = netFolder.listFiles().filter { Files.isDirectory(it.toPath()) }

        for (gFile in groundTruthFiles) {
            val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name) }
            println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
            for (nFiles in matchingNetImages) {
                runFolder(gFile, nFiles, targetCSVFile)
            }
        }

        println("end")
    }

    private fun runFolder(groundTruthFolder: File, netImageFolder: File, targetCSVOut: File) {

        if (groundTruthFolder.listFiles().size != netImageFolder.listFiles().size) {
            IJ.error("Number of pictures must match")
        }

        val opener = FolderOpener()
        val masks = opener.openFolder(groundTruthFolder.path)
        val net = opener.openFolder(netImageFolder.path)

        val res = mutableListOf<OverlappingSegmentResult>()
        for (i in 0 until masks.stackSize) {
            val maskSegments = ImageSegmentation.imageSegmentation(masks.imageStack.getProcessor(i + 1))
            val netSegments = ImageSegmentation.imageSegmentation(net.imageStack.getProcessor(i + 1))

            val result = SegmentationComparator.compareSegmentation(maskSegments, netSegments)
            res.add(result)
        }

        val imageKeyFigureData = ImageSegmentationDrawer().getImageKeyFigureData(res)

        ImageSegmentationToCSV.writeSegmentResultsToCSV(targetCSVOut, imageKeyFigureData.toList(), "Comparison_${groundTruthFolder.name}_vs_${netImageFolder.name}", false)


    }

}

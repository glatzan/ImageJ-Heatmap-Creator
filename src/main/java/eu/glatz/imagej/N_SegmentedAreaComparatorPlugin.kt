package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationToCSV
import ij.IJ
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File
import java.lang.reflect.Field
import java.nio.file.Files

/**
 * [folderMode], sourceFolder, targetFolder, [targetImageFolder], [targetCVSFile]
 */
class N_SegmentedAreaComparatorPlugin : PlugIn {
    override fun run(args: String) {
        val argArr = args.split(" ")
        var folderMode = false

        if (argArr.size < 2) {
            IJ.error("Provide source and target Folder")
            return
        }

        if (argArr[0] == "folder_mode")
            folderMode = true

        if (folderMode && argArr.size < 3) {
            IJ.error("Provide source and target Folder")
            return
        }

        val groundTruthFolder = if (folderMode) File(argArr[1]) else File(argArr[0])
        val netFolder = if (folderMode) File(argArr[2]) else File(argArr[1])
        val targetImageFolder = if (argArr.size >= 4 && File(argArr[3]).isDirectory) File(argArr[3]) else null
        val targetCSVFile = if (argArr.size == 5) File(argArr[4]) else null

        if (!groundTruthFolder.isDirectory || !netFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        if (folderMode) {
            val groundTruthFiles = groundTruthFolder.listFiles().filter { Files.isDirectory(it.toPath()) }
            val netImageFiles = netFolder.listFiles().filter { Files.isDirectory(it.toPath()) }

            for (gFile in groundTruthFiles) {
                val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name) }
                println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
                for (nFiles in matchingNetImages) {
                    runFolder(gFile, nFiles, targetImageFolder, targetCSVFile)
                }
            }
        } else {
            runFolder(groundTruthFolder, netFolder, targetImageFolder, targetCSVFile)
        }

        println("end")
    }

    private fun runFolder(groundTruthFolder: File, netImageFolder: File, targetImageFolder: File? = null, targetCSVOut: File? = null) {
        if (groundTruthFolder.listFiles().size != netImageFolder.listFiles().size) {
            IJ.error("Number of pictures must match")
            return
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

        val img = ImageSegmentationDrawer().createSegmentationImage(res, "Comparison_${groundTruthFolder.name}_vs_${netImageFolder.name}")
        //img.first.show()

        if (targetImageFolder != null) {
            FileSaver(img.first).saveAsPng(File(targetImageFolder, "${img.first.title}.png").absolutePath)
        }

        if (targetCSVOut != null) {
            ImageSegmentationToCSV.writeToCsv(targetCSVOut, img.second, false)
        }

    }
}
package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageKeyFigureData
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationToCSV
import ij.IJ
import ij.ImagePlus
import ij.Macro
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.concurrent.Executors

/**
 * [folderMode], sourceFolder, targetFolder, [targetImageFolder], [targetCVSFile]
 */
class N_SegmentedAreaComparatorPlugin : PlugIn {
    override fun run(args: String?) {

        var groundTruthFolder: String? = null
        var netFolder: String? = null
        var targetFolder: String? = null
        var targetCsv: String? = null
        var folderMode = true

        fun processCMD(str: String) {
            IJ.log(str)
            when {
                str.startsWith("-g=") -> groundTruthFolder = str.substringAfter("-g=").replace("\"", "")
                str.startsWith("-n=") -> netFolder = str.substringAfter("-n=").replace("\"", "")
                str.startsWith("-t=") -> targetFolder = str.substringAfter("-t=").replace("\"", "")
                str.startsWith("-c=") -> targetCsv = str.substringAfter("-c=").replace("\"", "")
                str.startsWith("-f=") -> folderMode = str.substringAfter("-f").toLowerCase() == "true"
            }
        }

        if (args.isNullOrBlank() || args.split(" ").isEmpty()) {
            IJ.log("No args $args ${Macro.getOptions()}")
            if (Macro.getOptions() != null && Macro.getOptions().split(" ").isNotEmpty()) {
                Macro.getOptions().split(" ").forEach {
                    processCMD(it)
                }
            } else {
                groundTruthFolder = IJ.getDirectory("Choose ground Truth Dir ")
                netFolder = IJ.getDirectory("Choose net Dir ")
                targetFolder = IJ.getDirectory("Choose target Dir ")
                targetCsv = IJ.getFilePath("Choose target csv ")
            }
        } else {
            IJ.log("Args: ${args}")
            args.split(" ").forEach {
                processCMD(it)
            }
        }

        if (groundTruthFolder == null || targetFolder == null || netFolder == null ||
                !File(groundTruthFolder).isDirectory || (!targetFolder.isNullOrBlank() && !File(targetFolder).isDirectory) || !File(netFolder).isDirectory) {
            IJ.error("Source or target is not a folder ($groundTruthFolder / $netFolder / $targetFolder )")
            return
        }

        if (folderMode) {
            val groundTruthFiles = File(groundTruthFolder).listFiles().filter { Files.isDirectory(it.toPath()) }
            val netImageFiles = File(netFolder).listFiles().filter { Files.isDirectory(it.toPath()) }

            val executor = Executors.newFixedThreadPool(2)

            for (gFile in groundTruthFiles) {
                val worker = Runnable {
                    val resultVector = Vector<ImageKeyFigureData>()
                    val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name) }
                    println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
                    val opener = FolderOpener()
                    val masks = opener.openFolder(gFile.path)
                    var count = 0
                    for (nFiles in matchingNetImages) {
                        resultVector.clear()
                        runFolder(masks, gFile.name, nFiles, File(targetFolder), null, resultVector)
                        saveCVSsyn(targetCsv!!, resultVector, "$count Comparison_${gFile.name}_vs_${nFiles.name}")
                        count++
                    }
                    System.gc()

                }
                executor.execute(worker)
            }

            executor.shutdown()

            while (!executor.isTerminated) {
            }


        } else {
            val opener = FolderOpener()
            val masks = opener.openFolder(File(groundTruthFolder).path)
            runFolder(masks, File(groundTruthFolder).name, File(netFolder), File(targetFolder), File(targetCsv))
        }

        println("end")
    }

    private fun runFolder(masks: ImagePlus, groundTruthFolderName: String, netImageFolder: File, targetImageFolder: File? = null, targetCSVOut: File? = null, targetCollection: Vector<ImageKeyFigureData>? = null) {

        if (masks.stack.size != netImageFolder.listFiles().size) {
            IJ.error("Number of pictures must match")
        }

        val opener = FolderOpener()
        val net = opener.openFolder(netImageFolder.path)

        val res = mutableListOf<OverlappingSegmentResult>()
        for (i in 0 until masks.stackSize) {
            val maskSegments = ImageSegmentation.imageSegmentation(masks.imageStack.getProcessor(i + 1))
            val netSegments = ImageSegmentation.imageSegmentation(net.imageStack.getProcessor(i + 1))

            val result = SegmentationComparator.compareSegmentation(maskSegments, netSegments)
            res.add(result)
        }

        val img = ImageSegmentationDrawer().createSegmentationImage(res, "Comparison_${groundTruthFolderName}_vs_${netImageFolder.name}")
        //img.first.show()

        if (!targetImageFolder?.path.isNullOrBlank()) {
            FileSaver(img.first).saveAsPng(File(targetImageFolder, "${img.first.title}.png").absolutePath)
        }

        if (targetCollection != null)
            targetCollection.add(img.second)
        else if (targetCSVOut != null) {
            ImageSegmentationToCSV.writeKeyFigureDataToCsv(targetCSVOut, img.second, false)
        }

    }

    @Synchronized
    fun saveCVSsyn(targetCsv: String, targetCollection: Vector<ImageKeyFigureData>, name: String) {
        println("Saving $name")
        ImageSegmentationToCSV.writeKeyFigureDataToCsv(File(targetCsv), targetCollection, false)

    }

}

/**
 * Compares ground truth with a folder net image folder. Matches areas.
 */
fun main(vararg args: String) {
    // folder compare
    val groundTruth = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_mask_grey_interpolated"
    val netImages = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_net_grey_interpolated_250_0015"
    val saveImageTO = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\difference_maps_net_raw_250_0015"
    val saveCSVTO = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\difference_maps_net_raw_250_0015\\compare.csv"

    IJ.runPlugIn(N_SegmentedAreaComparatorPlugin::class.qualifiedName, "-g=$groundTruth -n=$netImages -t=$saveImageTO -c=$saveCSVTO")
}


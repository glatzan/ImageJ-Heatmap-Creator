package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.HeatMapSurfaceComparator
import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
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

class N_HeatMapSurfaceComparatorPlugin : PlugIn {
    override fun run(args: String?) {
        IJ.log("Running surface compare")
        var groundTruthFolder: String? = null
        var netFolder: String? = null
        var targetFolder: String? = null
        var targetCsv: String? = null
        var inverted: Boolean = false

        fun processCMD(str: String) {
            IJ.log(str)
            when {
                str.startsWith("-g=") -> groundTruthFolder = str.substringAfter("-g=").replace("\"", "")
                str.startsWith("-n=") -> netFolder = str.substringAfter("-n=").replace("\"", "")
                str.startsWith("-t=") -> targetFolder = str.substringAfter("-t=").replace("\"", "")
                str.startsWith("-c=") -> targetCsv = str.substringAfter("-c=").replace("\"", "")
                str.startsWith("-i=") -> inverted = str.substringAfter("-i=").toLowerCase() == "true"
            }
        }

        if (args.isNullOrBlank() || args.split(" ").isEmpty()) {
            IJ.log("No args $args ${Macro.getOptions()}")
            if (Macro.getOptions() != null && Macro.getOptions().split(" ").isNotEmpty()) {
                Macro.getOptions().split(" ").forEach {
                    processCMD(it)
                }
            } else {
                groundTruthFolder = IJ.getDirectory("Choose ground Truth Dir ");
                netFolder = IJ.getDirectory("Choose net Dir ");
                targetFolder = IJ.getDirectory("Choose target Dir ");
                targetCsv = IJ.getFilePath("Choose target csv ");
            }
        } else {
            IJ.log("Args: ${args}")
            args.split(" ").forEach {
                processCMD(it)
            }
        }

        if (groundTruthFolder == null || targetFolder == null || netFolder == null ||
                !File(groundTruthFolder).isDirectory || !File(targetFolder).isDirectory || !File(netFolder).isDirectory) {
            IJ.error("Source or target is not a folder ($groundTruthFolder / $netFolder / $targetFolder )")
            return
        }

        val groundTruthFiles = File(groundTruthFolder).listFiles().filter { it.isFile }
        val netImageFiles = File(netFolder).listFiles().filter { it.isFile }
        for (gFile in groundTruthFiles) {
            val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name.substringBeforeLast(".")) }
            println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
            for (nFiles in matchingNetImages) {
                runFolder(gFile, matchingNetImages, File(targetFolder), File(targetCsv), inverted)
            }
        }

        println("end")
    }

    private fun runFolder(groundTruthFile: File, netImageFiles: List<File>, targetDirectory: File, targetCSVOut: File, inverted: Boolean) {
        val opener = FolderOpener()
        val mask = ImagePlus(groundTruthFile.absolutePath)
        val nets = netImageFiles.map { ImagePlus(it.absolutePath) }

        for (net in nets) {
            val name = "Comparison_vali_${mask.title.substringBeforeLast(".")}_vs_${net.title.substringBeforeLast(".")}"
            val result = HeatMapSurfaceComparator.compareImage(mask.processor, net.processor, File(targetDirectory, "$name.png"), inverted)
            result.name = name
            ImageSegmentationToCSV.writeKeyFigureDataToCsv(targetCSVOut, result, false)
        }
    }
}

fun main(vararg args: String) {

    // folder compare
    val groundTruth = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_mask_grey_interpolated"
    val netImages = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_net_grey_interpolated_250_01"
    val targetDir = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\difference_map_net_grey_interpolated_250_01"
    val saveCSVTO = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\difference_map_net_grey_interpolated_250_01\\difference_map_compare.csv"
    val inverted = true

    IJ.runPlugIn(N_HeatMapSurfaceComparatorPlugin::class.qualifiedName, "-g=$groundTruth -n=$netImages -t=$targetDir -c=$saveCSVTO -i=${inverted.toString()}")
}


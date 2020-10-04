package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationToCSV
import ij.IJ
import ij.Macro
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

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

        if (folderMode) {
            val groundTruthFiles = File(groundTruthFolder).listFiles().filter { Files.isDirectory(it.toPath()) }
            val netImageFiles = File(netFolder).listFiles().filter { Files.isDirectory(it.toPath()) }

            for (gFile in groundTruthFiles) {
                val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name) }
                println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
                for (nFiles in matchingNetImages) {
                    runFolder(gFile, nFiles, File(targetFolder), File(targetCsv))
                }
            }
        } else {
            runFolder(File(groundTruthFolder), File(netFolder), File(targetFolder), File(targetCsv))
        }

        println("end")
    }

    private fun runFolder(groundTruthFolder: File, netImageFolder: File, targetImageFolder: File? = null, targetCSVOut: File? = null) {

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

        val img = ImageSegmentationDrawer().createSegmentationImage(res, "Comparison_${groundTruthFolder.name}_vs_${netImageFolder.name}")
        //img.first.show()

        if (targetImageFolder != null) {
            FileSaver(img.first).saveAsPng(File(targetImageFolder, "${img.first.title}.png").absolutePath)
        }

        if (targetCSVOut != null) {
            ImageSegmentationToCSV.writeKeyFigureDataToCsv(targetCSVOut, img.second, false)
        }

    }

}

/**
 * Compares ground truth with a folder net image folder. Matches areas.
 */
fun main(vararg args: String) {
    // folder compare
    val groundTruth = "D:\\Projekte\\1_vaa_export_test_learn_set\\ground_truth_ray"
    val netImages = "D:\\Projekte\\1_vaa_export_test_learn_set\\out_merge"
    val saveImageTO = "D:\\Projekte\\delete"
    val saveCSVTO = "D:\\Projekte\\1_vaa_vali_compare\\net_compare_final.csv"

    IJ.runPlugIn(N_SegmentedAreaComparatorPlugin::class.qualifiedName, "-g=$groundTruth -n=$netImages -t=$saveImageTO -c=$saveCSVTO")
}


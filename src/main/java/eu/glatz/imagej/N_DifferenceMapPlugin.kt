package eu.glatz.imagej

import eu.glatz.imagej.heatmap.postprocess.DifferenceMapCreator
import eu.glatz.imagej.heatmap.postprocess.HeatMapCreator
import ij.IJ
import ij.Macro
import ij.io.FileSaver
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

class N_DifferenceMapPlugin : PlugIn {

    override fun run(args: String?) {

        var groundTruthFolder: String? = null
        var netFolder: String? = null
        var targetFolder: String? = null

        fun processCMD(str: String) {
            when {
                str.startsWith("-g=") -> groundTruthFolder = str.substringAfter("-g=").replace("\"", "")
                str.startsWith("-n=") -> netFolder = str.substringAfter("-n=").replace("\"", "")
                str.startsWith("-t=") -> targetFolder = str.substringAfter("-t=").replace("\"", "")
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

        val groundTruthFiles = File(groundTruthFolder).listFiles().filter { Files.isDirectory(it.toPath()) }
        val netImageFiles = File(netFolder).listFiles().filter { Files.isDirectory(it.toPath()) }

        for (gFile in groundTruthFiles) {
            val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name) }
            println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
            for (nFiles in matchingNetImages) {
                val map = DifferenceMapCreator.differenceMapFromFolder(gFile, nFiles)
                val image = HeatMapCreator.heatmapToInterpolatedImage(map, imageName = "DifferenceMap_${nFiles.name}", greyScale = true, interpolate = false)

                if (targetFolder != null) {
                    FileSaver(image).saveAsPng(File(targetFolder, "${image.title}.png").absolutePath)
                }
            }
        }
    }
}

fun main(vararg args: String) {
    val groundTruthDir = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_mask_grey_interpolated"
    val netDir = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_net_grey_interpolated_250_0015_post_300"
    val targetDir = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\difference_maps_net_raw_250_0015_post_300"

    IJ.runPlugIn(N_DifferenceMapPlugin::class.qualifiedName, "-g=$groundTruthDir -n=$netDir -t=$targetDir")
}

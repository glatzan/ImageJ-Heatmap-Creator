package eu.glatz.imagej

import eu.glatz.imagej.heatmap.postprocess.DifferenceMapCreator
import eu.glatz.imagej.heatmap.postprocess.HeatMapCreator
import ij.IJ
import ij.io.FileSaver
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

class N_DifferenceMapPlugin : PlugIn {

    override fun run(args: String) {
        val argArr = args.split(" ")

        if (argArr.size != 3) {
            IJ.error("Provide Arguments!")
            return
        }
        val groundTruthFolder = File(argArr[0])
        val compareFolder = File(argArr[1])
        val targetFolder = File(argArr[2])

        if (!groundTruthFolder.isDirectory || !compareFolder.isDirectory || !targetFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        val groundTruthFiles = groundTruthFolder.listFiles().filter { Files.isDirectory(it.toPath()) }
        val netImageFiles = compareFolder.listFiles().filter { Files.isDirectory(it.toPath()) }

        for (gFile in groundTruthFiles) {
            val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name) }
            println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
            for (nFiles in matchingNetImages) {
                val map = DifferenceMapCreator.differenceMapFromFolder(gFile, nFiles)
                val image = HeatMapCreator.heatmapToInterpolatedImage(map, imageName = "DifferenceMap_", greyScale = true, interpolate = false)
                image.show()

                if (targetFolder != null) {
                    FileSaver(image).saveAsPng(File(targetFolder, "${image.title}.png").absolutePath)
                }
            }
        }
    }
}

fun main(vararg args: String) {
    val masks = "D:\\Projekte\\vaa_vali_compare\\5_x4_vali_mask_optimzed_1000_folder"
    val net = "D:\\Projekte\\vaa_vali_compare\\9_net_winners"

    val target = "D:\\Projekte\\vaa_vali_compare\\50_difference_maps"

    IJ.runPlugIn(N_DifferenceMapPlugin::class.qualifiedName, "$masks $net $target")
}

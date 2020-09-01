package eu.glatz.imagej

import eu.glatz.imagej.heatmap.postprocess.HeatMap
import eu.glatz.imagej.heatmap.postprocess.HeatMapCreator
import eu.glatz.imagej.heatmap.postprocess.NetImageProbabilityMapCreator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import ij.IJ
import ij.io.FileSaver
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

class N_HeatMapCreatorPlugin : PlugIn {
    override fun run(args: String) {
        val argArr = args.split(" ")
        var folderMode = false

        if (argArr.size < 1) {
            IJ.error("Provide source Folder")
            return
        }

        if (argArr[0] == "folder_mode")
            folderMode = true

        if (folderMode && argArr.size < 2) {
            IJ.error("Provide source")
            return
        }

        val convertToProbabilityMap = argArr.size > 2 && argArr[1] == "toProbabilityMap"

        val offset = if (folderMode && convertToProbabilityMap) 2 else if (folderMode || convertToProbabilityMap) 1 else 0

        val sourceFolder = if (argArr.size > offset) File(argArr[offset]) else null
        val targetImageFolder = if (argArr.size > offset + 1) File(argArr[offset + 1]) else null

        if (!sourceFolder!!.isDirectory || (targetImageFolder != null && !targetImageFolder.isDirectory)) {
            IJ.error("Source or target is not a folder")
            return
        }

        if (folderMode) {
            val sourceFolderFiles = sourceFolder.listFiles().filter { Files.isDirectory(it.toPath()) }
            for (sFiles in sourceFolderFiles) {
                runFolder(sFiles, convertToProbabilityMap, targetImageFolder)
            }
        } else {
            runFolder(sourceFolder, convertToProbabilityMap, targetImageFolder)
        }
    }

    private fun runFolder(sourceFolder: File, convertToProbabilityMap: Boolean, targetImageFolder: File?, maxV: Int? = null) {
        val heatmap = if (convertToProbabilityMap) {
            val probabilityMap = NetImageProbabilityMapCreator.convertToProbabilityMap(sourceFolder)
            HeatMapCreator.heatMapFromProbabilityMap(probabilityMap)
        } else {
            HeatMapCreator.heatMapFromFolder(sourceFolder)
        }

        val image = HeatMapCreator.heatmapToInterpolatedImage(heatmap, 3, "Heatmap_${sourceFolder.name}", true, false, maxV)
        image.show()

        if (targetImageFolder != null) {
            FileSaver(image).saveAsPng(File(targetImageFolder, "${image.title}.png").absolutePath)
        }
    }
}

fun main(vararg args: String) {
    val sourceDir = "D:\\Projekte\\vaa_vali_compare\\9_net_winners_0.1"
    val saveImageTO = "D:\\Projekte\\vaa_vali_compare\\10_net_01_heatmaps_raw"

    // run from net folder
    // IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode toProbabilityMap $sourceDir $saveImageTO")
    // run for ground truth or probability map folder
    IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode $sourceDir $saveImageTO")
}


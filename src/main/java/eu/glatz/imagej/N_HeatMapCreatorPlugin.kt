package eu.glatz.imagej

import eu.glatz.imagej.heatmap.postprocess.HeatMap
import eu.glatz.imagej.heatmap.postprocess.HeatMapCreator
import eu.glatz.imagej.heatmap.postprocess.NetImageProbabilityMapCreator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import ij.IJ
import ij.Macro
import ij.io.FileSaver
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

class N_HeatMapCreatorPlugin : PlugIn {
    override fun run(args: String?) {

        var subFolderMode = true
        var sourceFolder: String? = null
        var targetFolder: String? = null
        var postProcess = false
        var interpolate = false
        var grayscale = true
        var prefix: String = ""

        fun processCMD(str: String) {
            when {
                str.startsWith("-f=") -> subFolderMode = str.substringAfter("-f=").toLowerCase() == "true"
                str.startsWith("-s=") -> sourceFolder = str.substringAfter("-s=").replace("\"", "")
                str.startsWith("-t=") -> targetFolder = str.substringAfter("-t=").replace("\"", "")
                str.startsWith("-p=") -> postProcess = str.substringAfter("-p=").toLowerCase() == "true"
                str.startsWith("-i=") -> interpolate = str.substringAfter("-i=").toLowerCase() == "true"
                str.startsWith("-g=") -> grayscale = str.substringAfter("-g=").toLowerCase() == "true"
                str.startsWith("-prefix=") -> prefix = str.substringAfter("-prefix=").replace("\"", "")
            }
        }

        if (args.isNullOrBlank() || args.split(" ").isEmpty()) {
            IJ.log("No args $args ${Macro.getOptions()}")
            if (Macro.getOptions() != null && Macro.getOptions().split(" ").isNotEmpty()){
                Macro.getOptions().split(" ").forEach {
                    processCMD(it)
                }
            }else{
                sourceFolder = IJ.getDirectory("Choose source Dir ");
                targetFolder = IJ.getDirectory("Choose target Dir ");
            }
        } else {
            IJ.log("Args: ${args}")
            args.split(" ").forEach {
                processCMD(it)
            }
        }

        if (sourceFolder == null || targetFolder == null ||
                !File(sourceFolder).isDirectory || !File(targetFolder).isDirectory) {
            IJ.error("Source or target is not a folder ($sourceFolder) / $targetFolder)")
            return
        }

        if (subFolderMode) {
            val sourceFolderFiles = File(sourceFolder).listFiles().filter { Files.isDirectory(it.toPath()) }
            for (sFiles in sourceFolderFiles) {
                IJ.log("Running folder ${sFiles.absolutePath}")
                runFolder(sFiles, postProcess, File(targetFolder), grayscale, interpolate, prefix)
            }
        } else {
            runFolder(File(sourceFolder), postProcess, File(targetFolder), grayscale, interpolate, prefix)
        }
    }

    private fun runFolder(sourceFolder: File, postProcess: Boolean, targetImageFolder: File?, greyScale: Boolean, interpolate: Boolean, imagePrefix: String = "", maxV: Int? = null) {
        val heatmap = if (postProcess) {
            val probabilityMap = NetImageProbabilityMapCreator.convertToProbabilityMap(sourceFolder)
            HeatMapCreator.heatMapFromProbabilityMap(probabilityMap)
        } else {
            HeatMapCreator.heatMapFromFolder(sourceFolder)
        }

        val image = HeatMapCreator.heatmapToInterpolatedImage(heatmap, 3, "$imagePrefix${sourceFolder.name}", greyScale, interpolate, maxV)

        if (targetImageFolder != null) {
            FileSaver(image).saveAsPng(File(targetImageFolder, "${image.title}.png").absolutePath)
        }
    }
}

fun main(vararg args: String) {
    val sourceDir = "D:\\Projekte\\_VAA\\set_validation\\1_mask_images\\2_mask_folders"
    val saveImageTO = "D:\\Projekte\\_VAA\\set_validation\\3_heatmaps\\heatmap_mask_raw"
    IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "-f=true -s=\"$sourceDir\" -t=\"$saveImageTO\" -g=true")
}


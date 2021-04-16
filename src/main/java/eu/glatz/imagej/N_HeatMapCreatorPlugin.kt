package eu.glatz.imagej

import eu.glatz.imagej.heatmap.postprocess.HeatMapCreator
import eu.glatz.imagej.heatmap.postprocess.NetImageProbabilityMapCreator
import ij.IJ
import ij.Macro
import ij.io.FileSaver
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

class N_HeatMapCreatorPlugin : PlugIn {

    var subFolderMode = true
    var sourceFolder: String? = null
    var targetFolder: String? = null
    var postProcess = false
    var interpolate = false
    var grayscale = true
    var prefix: String = ""
    var autoImage: Boolean = false
    var ray: Boolean = false

    override fun run(args: String?) {

        fun processCMD(str: String) {
            when {
                str.startsWith("-f=") -> subFolderMode = str.substringAfter("-f=").toLowerCase() == "true"
                str.startsWith("-s=") -> sourceFolder = str.substringAfter("-s=").replace("\"", "")
                str.startsWith("-t=") -> targetFolder = str.substringAfter("-t=").replace("\"", "")
                str.startsWith("-p=") -> postProcess = str.substringAfter("-p=").toLowerCase() == "true"
                str.startsWith("-i=") -> interpolate = str.substringAfter("-i=").toLowerCase() == "true"
                str.startsWith("-g=") -> grayscale = str.substringAfter("-g=").toLowerCase() == "true"
                str.startsWith("-prefix=") -> prefix = str.substringAfter("-prefix=").replace("\"", "")
                str.startsWith("-o=") -> autoImage = str.substringAfter("-o").toLowerCase().matches(Regex(".*true.*"))
                str.startsWith("-r=") -> ray = str.substringAfter("-r=").toLowerCase() == "true"
            }
        }


        if (args.isNullOrBlank() || args.split(" ").isEmpty()) {
            IJ.log("No args $args ${Macro.getOptions()}")
            if (Macro.getOptions() != null && Macro.getOptions().split(" ").isNotEmpty()) {
                Macro.getOptions().split(" ").forEach {
                    processCMD(it)
                }
            } else {
                sourceFolder = IJ.getDirectory("Choose source Dir ");
                targetFolder = IJ.getDirectory("Choose target Dir ");
            }
        } else {
            IJ.log("Args: ${args}")
            args.split(" ").forEach {
                processCMD(it)
            }
        }

        if (!autoImage) {
            if (sourceFolder == null || targetFolder == null ||
                    !File(sourceFolder).isDirectory || !File(targetFolder).isDirectory) {
                IJ.error("Source or target is not a folder ($sourceFolder) / $targetFolder)")
                return
            }
            if (subFolderMode) {
                val sourceFolderFiles = File(sourceFolder).listFiles().filter { Files.isDirectory(it.toPath()) }
                for (sFiles in sourceFolderFiles) {
                    IJ.log("Running folder ${sFiles.absolutePath}")
                    runFolder(sFiles, postProcess, File(targetFolder), grayscale, interpolate, prefix, ray = ray)
                }
            } else {
                runFolder(File(sourceFolder), postProcess, File(targetFolder), grayscale, interpolate, prefix)
            }
        } else {
            runFolder(grayscale, interpolate, prefix)
        }


    }

    private fun runFolder(sourceFolder: File, postProcess: Boolean, targetImageFolder: File?, greyScale: Boolean, interpolate: Boolean, imagePrefix: String = "", maxV: Int? = null, ray: Boolean = false) {
        val heatmap = if (postProcess) {
            val probabilityMap = NetImageProbabilityMapCreator.convertToProbabilityMap(sourceFolder)
            HeatMapCreator.heatMapFromProbabilityMap(probabilityMap)
        } else {
            if (!ray)
                HeatMapCreator.heatMapFromFolder(sourceFolder)
            else
                HeatMapCreator.heatMapByRayFromFolder(sourceFolder)
        }

        val image = HeatMapCreator.heatmapToInterpolatedImage(heatmap, 3, "$imagePrefix${sourceFolder.name}", greyScale, interpolate, maxV)

        if (targetImageFolder != null) {
            FileSaver(image).saveAsPng(File(targetImageFolder, "${image.title}.png").absolutePath)
        }
    }

    private fun runFolder(greyScale: Boolean, interpolate: Boolean, imagePrefix: String = "", maxV: Int? = null, ray: Boolean = false) {
        val stack = IJ.getImage()
        stack.stack.size
        val heatmap = if (!ray) HeatMapCreator.heatMapFromLoadedStack(stack.stack) else HeatMapCreator.heatMapFromLoadedStackByRay(stack.stack)

        val image = HeatMapCreator.heatmapToInterpolatedImage(heatmap, 3, "$imagePrefix${stack.title}", greyScale, interpolate, maxV)

        image.show()
    }
}

fun main(vararg args: String) {
    val sourceDir = "D:\\Projekte\\_VAA\\set_learn\\2_net_images\\net_results"
    val saveImageTO = "D:\\Projekte\\_VAA\\set_learn\\3_heatmaps\\heatmap_net_raw_all"
    IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "-f=true -s=\"$sourceDir\" -t=\"$saveImageTO\" -g=true")
}


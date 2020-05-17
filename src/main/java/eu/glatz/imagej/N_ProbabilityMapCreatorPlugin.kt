package eu.glatz.imagej

import eu.glatz.imagej.heatmap.postprocess.NetImageProbabilityMapCreator
import ij.IJ
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

/**
 * Creates a probability map for an image Folder
 */
class N_ProbabilityMapCreatorPlugin : PlugIn {
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

        val sourceFolder = if (folderMode) File(argArr[1]) else File(argArr[0])
        val targetFolder = if (folderMode) File(argArr[2]) else File(argArr[1])
        val folder_suffix = if (argArr.size == 4) File(argArr[3]) else ""

        if (!sourceFolder.isDirectory || !targetFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        if (folderMode) {
            val sourceFolderFiles = sourceFolder.listFiles().filter { Files.isDirectory(it.toPath()) }
            for (sFile in sourceFolderFiles) {
                val tFolder = File(targetFolder, "${sFile.name}${folder_suffix}")
                Files.createDirectories(tFolder.toPath())
                runFolder(sFile, tFolder)
            }
        } else {
            runFolder(sourceFolder, targetFolder)
        }

    }

    private fun runFolder(sourceFolder: File, targetFolder: File, binary: Boolean = true) {
        val probabilityMap = NetImageProbabilityMapCreator.convertToProbabilityMap(sourceFolder)
        val stack = NetImageProbabilityMapCreator.writeAsStackToFolder(probabilityMap, targetFolder, binary)
    }
}
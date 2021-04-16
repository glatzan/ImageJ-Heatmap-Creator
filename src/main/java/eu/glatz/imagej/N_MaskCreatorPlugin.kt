package eu.glatz.imagej

import eu.glatz.imagej.heatmap.mask.MaskCreator
import eu.glatz.imagej.heatmap.ray.RayCalculator
import eu.glatz.imagej.heatmap.ray.RayList
import ij.IJ
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File

class N_MaskCreatorPlugin : PlugIn {
    override fun run(args: String) {

        val argArr = args.split(" ")

        if (argArr.size < 2) {
            IJ.error("Provide source and target Folder")
            return
        }

        val topDown = argArr.size == 3 && argArr[2].contains("topdown")
        val line = argArr.size == 3 && argArr[2].contains("line")
        val parabola = argArr.size == 3 && argArr[2].contains("parabola")

        val sourceFolder = File(argArr[0])
        val targetFolder = File(argArr[1])

        if (!sourceFolder.isDirectory || !targetFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        val opener = FolderOpener()
        val sourceFiles = opener.openFolder(sourceFolder.path)

        val maskCreator = MaskCreator()

        var rays: RayList? = null
        if (!topDown && !line) {
            rays = RayCalculator().calcRaysStartAndEndPoint()
        }

        val maskPrefix = if (topDown) "_mask_td" else "_mask_ray"

        for (i in 1 until sourceFiles.stackSize + 1) {
            val processor = sourceFiles.imageStack.getProcessor(i)

            val resultImage = if (topDown) {
                maskCreator.createMaskTopDown(processor)
            } else if (line) {
                maskCreator.createMaskLine(processor)
            } else if (parabola) {
                maskCreator.createMaskParabola(processor)
            } else {
                maskCreator.createMaskRay(processor, rays!!)
            }

            IJ.run(resultImage, "Options...", "iterations=3 count=1 black edm=8-bit do=Close")

            FileSaver(resultImage).saveAsPng(File(targetFolder, "${sourceFiles.imageStack.getSliceLabel(i)}").absolutePath)
        }
    }
}

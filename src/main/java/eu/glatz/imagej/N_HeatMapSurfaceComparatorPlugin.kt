package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.HeatMapSurfaceComparator
import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.OverlappingSegmentResult
import eu.glatz.imagej.heatmap.segmentaion.SegmentationComparator
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationDrawer
import eu.glatz.imagej.heatmap.segmentaion.output.ImageSegmentationToCSV
import ij.IJ
import ij.ImagePlus
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File
import java.nio.file.Files

class N_HeatMapSurfaceComparatorPlugin : PlugIn {
    override fun run(args: String) {
        val argArr = args.split(" ")

        if (argArr.size != 3) {
            IJ.error("Provide source and target Folder")
            return
        }

        val groundTruthFolder = File(argArr[0])
        val netFolder = File(argArr[1])
        val targetCSVFile = File(argArr[2])

        if (!groundTruthFolder.isDirectory || !netFolder.isDirectory) {
            IJ.error("Source or target is not a folder")
            return
        }

        val groundTruthFiles = groundTruthFolder.listFiles().filter { it.isFile }
        val netImageFiles = netFolder.listFiles().filter { it.isFile }

        for (gFile in groundTruthFiles) {
            val matchingNetImages = netImageFiles.filter { it.name.startsWith(gFile.name.substringBeforeLast(".")) }
            println("Found ${matchingNetImages.size} Images for ground truth : ${gFile.name}")
            for (nFiles in matchingNetImages) {
                runFolder(gFile, matchingNetImages, targetCSVFile)
            }
        }

        println("end")
    }

    private fun runFolder(groundTruthFile: File, netImageFiles: List<File>, targetCSVOut: File) {
        val opener = FolderOpener()
        val mask = ImagePlus(groundTruthFile.absolutePath)
        val nets = netImageFiles.map { ImagePlus(it.absolutePath) }

        for(net in nets){
            val result = HeatMapSurfaceComparator.compareImage(mask.processor, net.processor)
            result.name = "Comparison_vali_${mask.title.substringBeforeLast(".")}_vs_${net.title.substringBeforeLast(".")}"
            ImageSegmentationToCSV.writeKeyFigureDataToCsv(targetCSVOut, result, false)
        }
    }
}

fun main(vararg args: String) {

    // folder compare
    val groundTruth = "D:\\Projekte\\vaa_vali_compare\\10_vali_heatmaps_raw_open"
    val netImages = "D:\\Projekte\\vaa_vali_compare\\10_net_01_heatmaps_raw_open"
    val saveCSVTO = "D:\\Projekte\\vaa_export_test_learn_set\\heatmap_compare.csv"

    IJ.runPlugIn(N_HeatMapSurfaceComparatorPlugin::class.qualifiedName, "$groundTruth $netImages $saveCSVTO")
}


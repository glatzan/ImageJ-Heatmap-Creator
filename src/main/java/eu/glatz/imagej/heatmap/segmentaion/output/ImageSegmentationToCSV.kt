package eu.glatz.imagej.heatmap.segmentaion.output

import ij.measure.ResultsTable
import java.io.File

object ImageSegmentationToCSV {

    fun writeToCsv(file: File, data: ImageKeyFigureData, override: Boolean = false) {
        val results = if (!override && file.exists()) {
            ResultsTable.open(file.absolutePath)
        } else {
            ResultsTable()
        }
        results.incrementCounter()
        results.addValue("Name", data.name)

        results.addValue("Overlapping Pixel", data.overlappingNetMaskPixelCount.toDouble())
        results.addValue("Total Net Pixel (connected to overlapping region)", data.overlappingNetTotalPixelCount.toDouble())
        results.addValue("Total Mask Pixel (connected to overlapping region)", data.overlappingMaskTotalPixelCount.toDouble())

        results.addValue("Total Net Pixel (not connected to overlapping region)", data.noneOverlappingNetPixelCount.toDouble())
        results.addValue("Total Mask Pixel (not connected to overlapping region)", data.noneOverlappingMaskPixelCount.toDouble())

        results.addValue("Total overlapping Segments", data.overlappingSegmentsCount.toDouble())
        results.addValue("Total none overlapping Net Segments", data.nonOverlappingNetCount.toDouble())
        results.addValue("Total none overlapping Mask Segments", data.nonOverlappingMaskCount.toDouble())

        results.save(file.path)
    }
}
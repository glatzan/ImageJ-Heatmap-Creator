package eu.glatz.imagej.heatmap.segmentaion.output

import ij.measure.ResultsTable
import java.io.File

object ImageSegmentationToCSV {

    fun writeKeyFigureDataToCsv(file: File, data: ImageKeyFigureData, override: Boolean = false) {
        val results = if (!override && file.exists()) {
            ResultsTable.open(file.absolutePath)
        } else {
            ResultsTable()
        }

        addRow(data, results)

        results.save(file.path)
    }


    fun writeKeyFigureDataToCsv(file: File, data: List<ImageKeyFigureData>, override: Boolean = false) {
        val results = if (!override && file.exists()) {
            ResultsTable.open(file.absolutePath)
        } else {
            ResultsTable()
        }

        data.forEach {
            addRow(it, results)
        }

        results.save(file.path)
    }

    fun addRow(data: ImageKeyFigureData, results: ResultsTable) {
        results.incrementCounter()
        results.addValue("Name", data.name)

        val nameArray = data.name.split("_")
        val post = nameArray.last().equals("pm")

        val probability = if (post) {
            if (nameArray[nameArray.size - 2].matches(Regex("[0-9.]*")))
                nameArray[nameArray.size - 2]
            else
                "Error"
        } else {
            if (nameArray[nameArray.size - 1].matches(Regex("[0-9.]*")))
                nameArray[nameArray.size - 1]
            else "Error"
        }

        val net = if (post) {
            nameArray[nameArray.size - 3]
        } else {
            nameArray[nameArray.size - 2]
        }

        results.addValue("Net", net)
        results.addValue("Probability", probability)
        results.addValue("Post", if (post) "post" else "")

        results.addValue("Overlapping Pixel", data.overlappingNetMaskPixelCount.toDouble())
        results.addValue("Total Net Pixel (connected to overlapping region)", data.overlappingNetTotalPixelCount.toDouble())
        results.addValue("Total Mask Pixel (connected to overlapping region)", data.overlappingMaskTotalPixelCount.toDouble())
        results.addValue("Net Pixel - Mask Pixel (connected to overlapping region)", data.overlappingNetTotalPixelCount.toDouble() - data.overlappingMaskTotalPixelCount.toDouble())

        results.addValue("Total Net Pixel (not connected to overlapping region)", data.noneOverlappingNetPixelCount.toDouble())
        results.addValue("Total Mask Pixel (not connected to overlapping region)", data.noneOverlappingMaskPixelCount.toDouble())

        results.addValue("Total overlapping Segments", data.overlappingSegmentsCount.toDouble())
        results.addValue("Total none overlapping Net Segments", data.nonOverlappingNetCount.toDouble())
        results.addValue("Total none overlapping Mask Segments", data.nonOverlappingMaskCount.toDouble())
    }


    fun writeSegmentResultsToCSV(file: File, imageKeyFigureData: List<ImageKeyFigureData>, imageName: String, override: Boolean = false) {
        val results = if (!override && file.exists()) {
            ResultsTable.open(file.absolutePath)
        } else {
            ResultsTable()
        }
        results.incrementCounter()
        val nameArray = imageName.split("_")
        val post = nameArray.last().equals("pm")

        val probability = if (post) {
            if (nameArray[nameArray.size - 2].matches(Regex("[0-9.]*")))
                nameArray[nameArray.size - 2]
            else
                "Error"
        } else {
            if (nameArray[nameArray.size - 1].matches(Regex("[0-9.]*")))
                nameArray[nameArray.size - 1]
            else "Error"
        }

        val net = if (post) {
            nameArray[nameArray.size - 3]
        } else {
            nameArray[nameArray.size - 2]
        }

        for ((imageNumber, imageKeyFigure) in imageKeyFigureData.withIndex()) {
            results.addValue("Name", imageName)

            results.addValue("Net", net)
            results.addValue("Probability", probability)
            results.addValue("Post", if (post) "post" else "")

            results.addValue("Image", imageNumber.toString())

            results.addValue("Overlapping Pixel", imageKeyFigure.overlappingNetMaskPixelCount.toDouble())
            results.addValue("Total Net Pixel (connected to overlapping region)", imageKeyFigure.overlappingNetTotalPixelCount.toDouble())
            results.addValue("Total Mask Pixel (connected to overlapping region)", imageKeyFigure.overlappingMaskTotalPixelCount.toDouble())
            results.addValue("Net Pixel - Mask Pixel (connected to overlapping region)", imageKeyFigure.overlappingNetTotalPixelCount.toDouble() - imageKeyFigure.overlappingMaskTotalPixelCount.toDouble())

            results.addValue("Total Net Pixel (not connected to overlapping region)", imageKeyFigure.noneOverlappingNetPixelCount.toDouble())
            results.addValue("Total Mask Pixel (not connected to overlapping region)", imageKeyFigure.noneOverlappingMaskPixelCount.toDouble())

            results.addValue("Total overlapping Segments", imageKeyFigure.overlappingSegmentsCount.toDouble())
            results.addValue("Total none overlapping Net Segments", imageKeyFigure.nonOverlappingNetCount.toDouble())
            results.addValue("Total none overlapping Mask Segments", imageKeyFigure.nonOverlappingMaskCount.toDouble())
            results.incrementCounter()

        }


        results.save(file.path)
    }

}

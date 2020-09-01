package eu.glatz.imagej.heatmap.segmentaion.output

import eu.glatz.imagej.heatmap.segmentaion.HeatMapSurfaceComparator

class ImageKeyFigureData {
    var name = ""
    var overlappingNetNonUniqueTotalPixelCount = 0
    var overlappingNetTotalPixelCount = 0
    var overlappingMaskNonUniqueTotalPixelCount = 0
    var overlappingMaskTotalPixelCount = 0
    var overlappingNetMaskPixelCount = 0
    var noneOverlappingNetPixelCount = 0
    var noneOverlappingMaskPixelCount = 0

    var overlappingSegmentsCount = 0
    var nonOverlappingNetCount = 0
    var nonOverlappingMaskCount = 0



    operator fun plus(imageKeyFigureData: ImageKeyFigureData) {
        overlappingNetMaskPixelCount += imageKeyFigureData.overlappingNetMaskPixelCount
        noneOverlappingNetPixelCount += imageKeyFigureData.noneOverlappingNetPixelCount
        noneOverlappingMaskPixelCount += imageKeyFigureData.noneOverlappingMaskPixelCount
        overlappingMaskTotalPixelCount += imageKeyFigureData.overlappingMaskTotalPixelCount
        overlappingNetTotalPixelCount += imageKeyFigureData.overlappingNetTotalPixelCount
    }
}

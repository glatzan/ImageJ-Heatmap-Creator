package eu.glatz.imagej.heatmap.segmentaion.output

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
}
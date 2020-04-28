package eu.glatz.imagej.heatmap.segmentaion

object SegmentationComparator {

    fun compareSegmentation(maskSegments: List<ImageSegment>, netSegment: List<ImageSegment>): OverlappingSegmentResult {
        val overlappingSegments = mutableListOf<OverlappingSegment>()
        val overlappingMasks = mutableListOf<ImageSegment>()
        val nonOverlappingNets = mutableListOf<ImageSegment>()

        for (netSeg in netSegment) {
            val res = compareSegmentation(netSeg, maskSegments)

            if(res.isEmpty()){
                nonOverlappingNets.add(netSeg)
            }else{
                overlappingSegments.addAll(res)
                overlappingMasks.addAll(res.map { it.maskSegment })
            }
        }

        val result = OverlappingSegmentResult()
        result.overlappingSegments = overlappingSegments
        result.nonOverlappingNetSegments = nonOverlappingNets
        result.nonOverlappingMaskSegments = maskSegments.minus(overlappingMasks.distinct())
        return result
    }

    fun compareSegmentation(netSegment: ImageSegment, maskSegments: List<ImageSegment>): List<OverlappingSegment> {
        val resultList = mutableListOf<OverlappingSegment>()

        for (maskSegment in maskSegments) {
            val intersection = maskSegment.dimension.intersection(netSegment.dimension)

            if (!intersection.isEmpty) {

                val result = OverlappingSegment()
                result.maskSegment = maskSegment
                result.netSegment = netSegment
                result.overlappingRectangle = intersection
                result.overLappingPixelMap = Array(intersection.width) { BooleanArray(intersection.height) }

                for (x in intersection.x until intersection.x + intersection.width) {
                    for (y in intersection.y until intersection.y + intersection.height) {
                        val segX = intersection.x - netSegment.dimension.x + x
                        val segY = intersection.y - netSegment.dimension.y + y
                        val sPoint = netSegment.pixelMap[segX][segY]
                        val refX = intersection.x - maskSegment.dimension.x + x
                        val refY = intersection.y - maskSegment.dimension.y + y

                        val rPoint = maskSegment.pixelMap[refX][refY]

                        if (rPoint && sPoint) {
                            result.overlappingCount++;
                            result.overLappingPixelMap[x][y] = true
                        }
                    }
                }
                resultList.add(result)
            }
        }
        return resultList
    }
}
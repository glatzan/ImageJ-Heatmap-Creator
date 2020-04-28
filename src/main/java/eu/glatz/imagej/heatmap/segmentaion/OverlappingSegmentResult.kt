package eu.glatz.imagej.heatmap.segmentaion

class OverlappingSegmentResult {

    lateinit var overlappingSegments: List<OverlappingSegment>

    lateinit var nonOverlappingMaskSegments: List<ImageSegment>

    lateinit var nonOverlappingNetSegments: List<ImageSegment>
}
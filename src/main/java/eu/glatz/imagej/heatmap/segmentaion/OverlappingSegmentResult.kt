package eu.glatz.imagej.heatmap.segmentaion

class OverlappingSegmentResult {

    lateinit var overlappingSegments: List<OverlappingSegment>

    lateinit var nonOverlappingMaskSegments: List<SegmentedImage>

    lateinit var nonOverlappingNetSegments: List<SegmentedImage>
}

package eu.glatz.imagej.heatmap.segmentaion

import java.awt.Rectangle

class OverlappingSegment {

    lateinit var maskSegment : ImageSegment

    lateinit var  netSegment : ImageSegment

    lateinit var overLappingPixelMap: Array<BooleanArray>

    lateinit var overlappingRectangle: Rectangle

    var overlappingCount : Int = 0
}
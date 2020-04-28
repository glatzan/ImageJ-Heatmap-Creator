package eu.glatz.imagej.heatmap.segmentaion

import java.awt.Point
import java.awt.Rectangle

class ImageSegment(var number: Int) {

    lateinit var dimension: Rectangle

    lateinit var pixels: Array<Point>

    lateinit var pixelMap: Array<BooleanArray>

    val positivePixelCount
        get() = pixels.size

    val totalPixelCount
        get() = dimension.width * dimension.height
}
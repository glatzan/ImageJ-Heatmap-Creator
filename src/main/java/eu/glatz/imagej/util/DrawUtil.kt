package eu.glatz.imagej.util

import eu.glatz.imagej.model.Line
import eu.glatz.imagej.model.Point
import ij.process.ImageProcessor
import java.awt.Color

object DrawUtil {

    fun drawLine(line: Line, color: Color, ip: ImageProcessor) {
        ip.setColor(color)
        val points = line.getAllPoints()
        for ((index, point) in points.withIndex()) {
            if (index + 1 < points.size) {
                val nextPoint = points[index + 1]
                ip.drawLine(point.x, point.y, nextPoint.x, nextPoint.y)
            }
        }
    }

    fun drawText(point: Point, color: Color, text: String, ip: ImageProcessor) {
        ip.setColor(color)
        ip.drawString(text, point.x, point.y)
    }

    fun fillRect(point: Point, width: Int, height: Int, color: Color, ip: ImageProcessor) {
        ip.setColor(color)
        ip.fillRect(point.x, point.y, width, height)
    }
}
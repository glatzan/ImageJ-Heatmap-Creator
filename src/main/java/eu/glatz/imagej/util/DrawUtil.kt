package eu.glatz.imagej.util

import eu.glatz.imagej.model.Line
import ij.process.ImageProcessor
import java.awt.Color

object DrawUtil {

    fun drawLine(line: Line, color: Color, ip: ImageProcessor) {
        ip.setColor(color)
        for (point in line.points) {
            ip.drawDot(point.x, point.y)
        }
    }
}
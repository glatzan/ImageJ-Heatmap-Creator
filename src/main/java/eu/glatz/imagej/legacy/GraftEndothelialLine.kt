package eu.glatz.imagej.legacy

import eu.glatz.imagej.heatmap.ray.Parabola
import eu.glatz.imagej.model.*
import eu.glatz.imagej.util.DrawUtil
import eu.glatz.imagej.util.LineFinder
import eu.glatz.imagej.util.VectorUtils
import ij.IJ
import ij.ImagePlus
import ij.plugin.filter.PlugInFilter
import ij.process.ImageProcessor
import java.awt.Color

class GraftEndothelialLine : PlugInFilter {

    var imp: ImagePlus? = null
    var debug = false

    companion object {
        @JvmStatic
        lateinit var hostParabola: Parabola

        @JvmStatic
        lateinit var resultGraftLines: List<Line>
    }

    var lineRemoveThreshold = 50

    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        return PlugInFilter.DOES_ALL
    }

    override fun run(ip: ImageProcessor) {
        IJ.run(imp, "8-bit", "");
        IJ.run(imp, "Skeletonize (2D/3D)", "");

        val lineImage = IJ.createImage("Graft_AcceptedLines", "RGB", ip.width, ip.height, 1)
        val graftLines = IJ.createImage("Graft_Lines", "RGB", ip.width, ip.height, 1)

        // find lines
        val lines = LineFinder.findConnectedRegion(imp!!.processor, 5)

        // remove short lines
        val tLines = LineFinder.removeShortLines(lines, 10.0)

        lines.forEach { line ->
            if (line.length < 10) {
                DrawUtil.drawLine(line, Color.RED, lineImage.processor)
            } else {
                DrawUtil.drawLine(line, Color.GREEN, lineImage.processor)
            }
        }

        val linesWithinParabola = tLines.filter { isLineWithinParabola(hostParabola, it, 300, lineImage.processor) }

        val linesOutOfParabola = tLines.toMutableList()
        linesOutOfParabola.removeAll(linesWithinParabola)
        linesOutOfParabola.forEach {
            DrawUtil.drawLine(it, Color.ORANGE, lineImage.processor)
        }

        val linesInValidArea = linesWithinParabola.filter { !isLineInInRestrictedArea(it, Square(Point(0, 425), 1300, 650 - 425)) }

        val linesInRestrictedArea = linesWithinParabola.toMutableList()
        linesInRestrictedArea.removeAll(linesInValidArea)
        linesInRestrictedArea.forEach {
            DrawUtil.drawLine(it, Color.YELLOW, lineImage.processor)
        }

        // calculating overlapping steps to parabola
        val lineClassification = linesInValidArea.map { LineClassification(it, hostParabola) }.toMutableList()
        lineClassification.forEach { it.calculateStepsToParabola(lineClassification) }

        val resultLines = listOf<Line>()

        val mergedLines = mutableListOf<JoinedLine>()
        mergedLines.add(JoinedLine(0))
        resultGraftLines = mergedLines

        if (lineClassification.isEmpty()) {
            return
        }


        var currentLine = lineClassification.first()
        mergedLines.last().addLine(currentLine.line)

        while (lineClassification.isNotEmpty()) {
            val nextLine = lineClassification.removeAt(0)

            val distance = VectorUtils.distance(currentLine.line.lastPoint(), nextLine.line.firstPoint())
            val direction = VectorUtils.directionVector(currentLine.line.lastPoint(), nextLine.line.firstPoint())

            if (distance > 300) {
                mergedLines.add(JoinedLine(mergedLines.size))
            }

            mergedLines.last().addLine(nextLine.line)
            currentLine = nextLine
        }

        mergedLines.forEach {
            DrawUtil.drawLine(it, Color.GREEN, graftLines.processor)
        }

        hostParabola.draw(lineImage.processor)

        if (debug) {
            lineImage.show()
            graftLines.show()
        }
    }


    private fun isLineWithinParabola(parabola: Parabola, line: Line, thresholdDistance: Int, ip: ImageProcessor): Boolean {
//        val startParabolaPoint = parabola.calculatePointOfNormalOnParabola(line.firstPoint())
//        val endParabolaPoint = parabola.calculatePointOfNormalOnParabola(line.lastPoint())
//
//        DrawUtil.drawLine(SimpleLine(0).addPoint(startParabolaPoint, line.firstPoint()), Color.CYAN, ip)
//        DrawUtil.drawLine(SimpleLine(0).addPoint(endParabolaPoint, line.lastPoint()), Color.CYAN, ip)
//
//        val dirV1 = VectorUtils.directionVector(startParabolaPoint, line.firstPoint())
//        val dirV2 = VectorUtils.directionVector(endParabolaPoint, line.lastPoint())
//
//        val meanDirX = (dirV1.x + dirV2.x) / 2
//        val meanDirY = (dirV1.y + dirV2.y) / 2
//
//        val distV1 = VectorUtils.distance(line.firstPoint(), startParabolaPoint)
//        val distV2 = VectorUtils.distance(line.lastPoint(), endParabolaPoint)
//
//        val distance = (distV1 / distV2) / 2
//
//        if (meanDirY > 0)
//            return false;
//
//        if (distance > thresholdDistance)
//            return false

        return true
    }

    /**
     * Checks if line is in restricted area
     */
    private fun isLineInInRestrictedArea(line: Line, square: Square): Boolean {
        for (p in line.getAllPoints()) {
            if (p.x in square.start.x..square.start.x + square.width && p.y in square.start.y..square.start.y + square.heigth)
                return true
        }
        return false
    }
}
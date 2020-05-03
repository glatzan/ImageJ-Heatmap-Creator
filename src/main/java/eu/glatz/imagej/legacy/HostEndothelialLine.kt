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
import java.lang.Math.random
import kotlin.math.roundToInt

class HostEndothelialLine : PlugInFilter {

    var imp: ImagePlus? = null

    var debug = false

    companion object {
        @JvmStatic
        lateinit var hostParabola: Parabola

        @JvmStatic
        lateinit var resultHostLine: Line
    }

    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        println(arg)
        return PlugInFilter.DOES_ALL
    }

    override fun run(ip: ImageProcessor) {
        IJ.run(imp, "8-bit", "");
        IJ.run(imp, "Skeletonize (2D/3D)", "");

        val lineImage = IJ.createImage("Host_AcceptedLines", "RGB", ip.width, ip.height, 1)
        val finalImage = IJ.createImage("Host_FinalLine", "RGB", ip.width, ip.height, 1)

        // find lines
        val lines = LineFinder.findConnectedRegion(imp!!.processor, 5)

        // remove short lines
        val tLines = LineFinder.removeShortLines(lines, 50.0)

        lines.forEach { line ->
            if (line.length < 50) {
                DrawUtil.drawLine(line, Color.RED, lineImage.processor)
            }
        }

        // calculating overlapping steps to parabola
        val classificationLines = tLines.map { LineClassification(it, hostParabola) }
        classificationLines.forEach { it.calculateStepsToParabola(classificationLines) }

        val acceptedLines = mutableListOf<LineClassification>()
        val declinedLines = mutableListOf<LineClassification>()
        val iterLines = classificationLines.toMutableList()

        var currentLine = iterLines.removeAt(0)
        acceptedLines.add(currentLine)

        while (iterLines.isNotEmpty()) {
            val lineRanking = calculateLineRankings(currentLine, iterLines)

            var maxProbability = Int.MIN_VALUE;
            var nextLine: LineClassification? = null

            // rank all remaining lines
            for (ranking in lineRanking) {
                val rank = ranking.calculateProbability()

                // decline line if line start is left of current line
                if (rank <= -100) {
                    declinedLines.add(ranking.second)
                    iterLines.remove(ranking.second)
                } else if (maxProbability < rank) {
                    // check if line rank is better the nextLine
                    nextLine = ranking.second
                    maxProbability = rank
                }
            }

            if (nextLine != null) {
                currentLine = nextLine
                acceptedLines.add(nextLine)
                iterLines.remove(nextLine)
            }
        }

        acceptedLines.forEachIndexed { index, line ->
            val color = Color((255 * random()).toInt(), (255 * random()).toInt(), (255 * random()).toInt())
            DrawUtil.drawLine(line.line, color, lineImage.processor)
            DrawUtil.drawText(Point(line.line.firstPoint().x, line.line.firstPoint().y + 10), color, index.toString(), lineImage.processor)
        }

        declinedLines.forEachIndexed { index, line ->
            DrawUtil.drawLine(line.line, Color.CYAN, lineImage.processor)
            DrawUtil.drawText(Point(line.line.firstPoint().x, line.line.firstPoint().y + 10), Color.CYAN, index.toString(), lineImage.processor)
        }

        hostParabola.draw(lineImage.processor)
        hostParabola.draw(finalImage.processor)

        val resultLine = JoinedLine(0)
        resultLine.addLines(acceptedLines.map { it.line })

        DrawUtil.drawLine(resultLine, Color.GREEN, finalImage.processor)

        if (debug) {
            lineImage.show()
            finalImage.show()
        }

        resultHostLine = resultLine
    }

    /**
     * Calculates line ranking
     */
    private fun calculateLineRankings(line: LineClassification, lines: List<LineClassification>): List<LineRanking> {
        val lineRanking = lines.map { LineRanking(line, it) }
        // adds distance ranking by sorting by distance
        val lineDistanceRanking = lineRanking.toList().sortedBy { it.distanceToSecondLine }
        lineRanking.forEach {
            it.distancePositionInList = lineDistanceRanking.indexOf(it)
        }
        return lineRanking
    }

    /**
     * Ranking, composed of distance to first line and overlapping steps to parabola
     */
    class LineRanking {
        var first: LineClassification
        var second: LineClassification

        var distanceToSecondLine: Int = 0
        var distancePositionInList = 0;
        var ranking: Int = 0

        constructor(first: LineClassification, second: LineClassification) {
            this.first = first
            this.second = second
            this.distanceToSecondLine = VectorUtils.distance(first.line.lastPoint(), second.line.firstPoint()).roundToInt()
        }


        fun calculateProbability(): Int {
            var prob = 0

            prob -= second.stepsToParabola
            prob -= ranking

            if (first.line.lastPoint().x > second.line.lastPoint().x || this.first.line.lastPoint().x - 5 > second.line.firstPoint().x)
                prob -= 100

            return prob
        }
    }
}
package eu.glatz.imagej

import com.google.gson.Gson
import eu.glatz.imagej.model.Line
import eu.glatz.imagej.model.Point
import eu.glatz.imagej.util.DrawUtil
import ij.IJ
import ij.ImagePlus
import ij.plugin.filter.PlugInFilter
import ij.plugin.filter.PlugInFilter.DOES_ALL
import ij.process.ImageProcessor
import java.awt.Color
import java.util.*

class LineDetector : PlugInFilter {

    var imp: ImagePlus? = null
    var minLengthThreshold = 10

    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        return DOES_ALL
    }

    override fun run(ip: ImageProcessor) {
        IJ.run(imp, "8-bit", "");
        IJ.run("Skeletonize (2D/3D)");

        val lines = mutableListOf<Line>();
        val activeFront = Stack<Point>()
        val visitedPoints = Array(ip.width) { BooleanArray(ip.height) }

        val out = ip.duplicate().convertToRGB()


        var idCounter = 0;
        for (x in 0 until ip.width) {
            for (y in 0 until ip.height) {
                val pix = ip.get(x, y)
                if (pix == 255 && !visitedPoints[x][y]) {
                    activeFront.push(Point(x, y, pix))
                    visitedPoints[x][y] = true
                    val line = Line(idCounter++)
                    searchConnectedRegion(line, activeFront, visitedPoints, ip)
                    line.updateLength()

                    if(line.length < minLengthThreshold)
                        DrawUtil.drawLine(line, Color.RED,out)
                    else{
                        DrawUtil.drawLine(line,Color((255 * Math.random()).toInt(), (255 * Math.random()).toInt(), (255 * Math.random()).toInt()),out)
                        lines.add(line)
                    }
                }
            }
        }

        imp!!.updateAndDraw()
        ImagePlus("Detected", out).show()
        IJ.log("icon double-clicked");
        IJ.saveString(Gson().toJson(lines), "src/main/resources/test.json")
    }

    private fun searchConnectedRegion(line: Line, activeFront: Stack<Point>, visitedPoints: Array<BooleanArray>, ip: ImageProcessor): Line {
        while (!activeFront.empty()) {
            val point = activeFront.pop();
            if (point.value == 255) {
                line.addPoint(point)
                // 8x8 neighborhood
                for (x in -1..1) {
                    for (y in -1..1) {
                        val searchPoint = Point(point.x + x, point.y + y)
                        if (searchPoint.x in 0 until ip.width && searchPoint.y in 0 until ip.height && !visitedPoints[point.x + x][point.y + y]) {
                            searchPoint.value = ip.get(searchPoint.x, searchPoint.y)
                            activeFront.push(searchPoint)
                        }
                        visitedPoints[point.x][point.y] = true
                    }
                }
            }
        }

        return line
    }

}



package eu.glatz.imagej.legacy

import com.google.gson.Gson
import eu.glatz.imagej.model.SimpleLine
import eu.glatz.imagej.util.DrawUtil
import eu.glatz.imagej.util.LineFinder
import ij.IJ
import ij.ImagePlus
import ij.plugin.filter.PlugInFilter
import ij.plugin.filter.PlugInFilter.DOES_ALL
import ij.process.ImageProcessor
import java.awt.Color

class LineDetector : PlugInFilter {

    var imp: ImagePlus? = null
    var minLengthThreshold = 10

    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        return DOES_ALL
    }

    override fun run(ip: ImageProcessor) {
        IJ.run(imp, "8-bit", "")
        val processor = imp!!.processor
        IJ.run("Skeletonize (2D/3D)")

        val out = processor.duplicate().convertToRGB()

        val lines = LineFinder.findConnectedRegion(processor, 5)
        val result = mutableListOf<SimpleLine>()
        lines.forEach { line ->
            if (line.length < minLengthThreshold)
                DrawUtil.drawLine(line, Color.RED, out)
            else {
                DrawUtil.drawLine(line, Color((255 * Math.random()).toInt(), (255 * Math.random()).toInt(), (255 * Math.random()).toInt()), out)
                result.add(line)
            }

        }

        imp!!.updateAndDraw()
        ImagePlus("Detected", out).show()
        IJ.log("icon double-clicked")
        IJ.saveString(Gson().toJson(result), "src/main/resources/test.json")
    }
}



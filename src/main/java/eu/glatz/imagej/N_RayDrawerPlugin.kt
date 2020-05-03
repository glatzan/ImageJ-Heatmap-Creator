package eu.glatz.imagej

import eu.glatz.imagej.heatmap.mask.ConnectedLineCalculator
import eu.glatz.imagej.heatmap.mask.MaskCreator
import eu.glatz.imagej.heatmap.ray.RayCalculator
import ij.IJ
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.awt.Color
import java.io.File

class N_RayDrawerPlugin : PlugIn {
    override fun run(args: String) {
        val resultImage = IJ.createImage("Parabola", "RGB", 512, 512, 1)
        val resultProcessor = resultImage.processor

        val rays = RayCalculator().calcRaysStartAndEndPoint()
        val connectedLineCalculator = ConnectedLineCalculator()

        resultProcessor.setColor(Color.RED)
        resultImage.show()

        for (ray in rays) {
            val points = connectedLineCalculator.getIntersectionPixels(ray.first, ray.second)

            for (point in points) {
                resultProcessor.drawPixel(point.x, point.y)
            }
            Thread.sleep(10)
            resultImage.updateAndDraw()
        }
    }
}
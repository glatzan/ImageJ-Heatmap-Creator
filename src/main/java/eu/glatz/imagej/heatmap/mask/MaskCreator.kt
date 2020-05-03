package eu.glatz.imagej.heatmap.mask

import eu.glatz.imagej.heatmap.ray.RayList
import ij.IJ
import ij.ImagePlus
import ij.process.ImageProcessor
import java.awt.Color
import java.awt.Point


class MaskCreator {

    /**
     * Creates a simple mask with top down pixel search
     */
    fun createMaskTopDown(rawImageProcessor: ImageProcessor): ImagePlus {

        val resultImage = IJ.createImage("Mask", 512, 512, 1, 8)
        val resultImageProcessor = resultImage.processor
        resultImageProcessor.setColor(Color.white)

        for (x in 0 until rawImageProcessor.width) {

            var foundHost: Point? = null
            var foundGraft: Point? = null

            for (y in 0 until rawImageProcessor.height) {

                val value = rawImageProcessor.get(x, y).toUInt()

                if (value == 0xFF000000u)
                    continue

                if (foundHost == null && value.and(0xFF0000u) == 0xFF0000u) {
                    foundHost = Point(x, y)
                } else if (value.and(0xFF00u) == 0xFF00u) {
                    foundGraft = Point(x, y)
                }
            }

            if (foundHost != null && foundGraft != null) {
                resultImageProcessor.drawLine(foundHost.x, foundHost.y, foundGraft.x, foundGraft.y)
            }
        }

        resultImage.updateAndDraw()
        return resultImage
    }


    /**
     * Creates a Mask using connected Rays
     */
    fun createMaskRay(rawImageProcessor: ImageProcessor, rays: RayList): ImagePlus {
        val resultImage = IJ.createImage("Mask", 512, 512, 1, 8)
        val resultImageProcessor = resultImage.processor
        resultImageProcessor.setColor(Color.white)

        val connectedLineCalculator = ConnectedLineCalculator()

        for (ray in rays) {
            val points = connectedLineCalculator.getIntersectionPixels(ray.first, ray.second)

            var foundHost: Point? = null
            var foundGraft: Point? = null

            var switchedGraftHost = false

            for (point in points) {

                if (point.x !in 0..rawImageProcessor.width - 1 || point.y !in 0..rawImageProcessor.height - 1)
                    continue

                val value1 = rawImageProcessor.get(point.x, point.y)

                val value = rawImageProcessor.get(point.x, point.y).toUInt()

                if (value == 0xFF000000u)
                    continue
                else {
                    println(value1)
                }

                if (foundHost == null && value.and(0xFF0000u) == 0xFF0000u) {
                    foundHost = point
                } else if (value.and(0xFF00u) == 0xFF00u) {

                    if(foundGraft == null)
                        foundGraft = point
                    else{

                        if(foundGraft.distance(point) < 2)
                            continue

                        if(!switchedGraftHost){
                            foundHost = foundGraft
                            switchedGraftHost = true
                        }
                        foundGraft = point
                    }
                }
            }

            if (foundHost != null && foundGraft != null) {
                resultImageProcessor.drawLine(foundHost.x, foundHost.y, foundGraft.x, foundGraft.y)
            }
        }

        resultImage.updateAndDraw()
        return resultImage
    }

}
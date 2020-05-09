package eu.glatz.imagej.heatmap.mask

import eu.glatz.imagej.heatmap.ray.RayList
import ij.IJ
import ij.ImagePlus
import ij.process.ImageProcessor
import java.awt.Color
import java.awt.Point


class MaskDetector {

    /**
     * Creates a simple mask with top down pixel search
     */
    fun detectMaskTopDown(rawImageProcessor: ImageProcessor): ImagePlus {

        val resultImage = IJ.createImage("Mask", 512, 512, 1, 8)
        return resultImage
    }


    /**
     * Creates a Mask using connected Rays
     */
    fun detectMaskRay(rawImageProcessor: ImageProcessor, rays: RayList): ImagePlus {
        val resultImage = IJ.createImage("Mask", 512, 512, 1, 8)
        return resultImage

    }

}

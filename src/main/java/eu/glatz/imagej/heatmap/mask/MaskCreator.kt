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

        val connectedLineCalculator = ConnectedLineCalculator()

        val resultLine = mutableListOf<Pair<Point, Point>>()

        for ((index, ray) in rays.withIndex()) {
            val points = connectedLineCalculator.getIntersectionPixels(ray.first, ray.second)

            if (ray.first.x > ray.second.x)
                points.reverse()

            var host: Point? = null
            var graft: Point? = null

            for (point in points) {
                if (point.x !in 0..rawImageProcessor.width - 1 || point.y !in 0..rawImageProcessor.height - 1)
                    continue

                val value = rawImageProcessor.get(point.x, point.y).toUInt()

                if (value == 0xFF000000u)
                    continue

                if (value.and(0xFF0000u) == 0xFF0000u) {
                    if (host != null) {
                        continue;
                    }
                    host = point
                } else if (value.and(0xFF00u) == 0xFF00u) {
                    if (graft == null) {
                        graft = point
                    } else {
                        if (graft.distance(point) > 2) {
                            host = graft
                            graft = point
                            break
                        }
                    }
                }
            }

            if (host !== null && graft !== null) {
                resultLine.add(Pair(host, graft))
            }
        }

        return resultImage
    }

    fun createMaskLine(rawImageProcessor: ImageProcessor): ImagePlus {
        val resultImage = IJ.createImage("Mask", 512, 512, 1, 8)
        val resultImageProcessor = resultImage.processor
        resultImageProcessor.setColor(Color.WHITE)

        val lineDetector = LineDetector()
        val lines = lineDetector.findLines(rawImageProcessor, 5)

        val host = lines.firstOrNull { it.type == MatchType.Host }

        if (host != null) {
            for (line in lines) {
                if (line.type == MatchType.Graft) {
                    for (p in line.points) {
                        val sp = lineDetector.shortestPointOnMask(host, p);
                        if (sp != null) {
                            resultImageProcessor.drawLine(p.x, p.y, sp.x, sp.y)
                        }
                    }

                }
            }
        }

        return resultImage
    }

    fun createMaskParabola(rawImageProcessor: ImageProcessor): ImagePlus {
        return ParabolaLineDetector().floodWithParabola(rawImageProcessor)
    }
}

enum class MatchType {
    Host,
    Graft
}

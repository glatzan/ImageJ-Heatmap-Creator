package eu.glatz.imagej.heatmap.ray

import com.google.gson.Gson
import java.awt.Point
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import kotlin.math.pow

/**
 * Calculates start and endpoint ov 512 normals for a parabola
 */
class RayCalculator {

    fun calcRaysStartAndEndPoint(): RayList {
        val parabola = Parabola(Point(256, 20), 0.004F)

        val result = RayList()

        val startX = 256
        val startY = 20
        val factor = 0.004F

        for (x0 in 0 until 255) {
            val sPoint = Point()
            val endPoint = Point()

            var start = false
            for (x in 0 until 512) {
                val y = (1 / (-factor * 2 * (x0 - startX))) * (x - x0) + (factor * (x0 - startX).toDouble().pow(2) + startY)
                if (y >= 0 && !start) {
                    sPoint.x = x
                    sPoint.y = y.toInt()
                    start = true
                } else if (y > 511 && start) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                    break
                } else if (x == 511) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                }
            }
            result.add(Pair(sPoint, endPoint))
        }

        result.add(Pair(Point(255, 0), Point(255, 511)))

        for (x0 in 256 until 512) {
            val sPoint = Point()
            val endPoint = Point()

            var start = false

            for (x in 0 until 512) {
                val y = (1 / (-factor * 2 * (x0 - startX))) * (x - x0) + (factor * (x0 - startX).toDouble().pow(2) + startY)
                if (y <= 511 && !start) {
                    sPoint.x = x
                    sPoint.y = y.toInt()
                    start = true
                } else if (y < 0 && start) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                    break
                } else if (x == 511) {
                    endPoint.x = x
                    endPoint.y = y.toInt()
                }
            }
            result.add(Pair(sPoint, endPoint))
        }

        return result
    }

    fun saveRays(rays: MutableList<Pair<Point, Point>>, file: File) {
        Files.write(file.toPath(), Gson().toJson(rays).toByteArray(Charsets.UTF_8))
    }

    fun loadRays(file: File): RayList {
        return Gson().fromJson(FileReader(file), RayList::class.java)
    }
}
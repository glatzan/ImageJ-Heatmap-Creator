package eu.glatz.imagej

import com.google.gson.Gson
import eu.glatz.imagej.model.ray.RayContainer
import ij.IJ
import ij.io.Opener
import ij.plugin.PlugIn
import ij.process.ImageProcessor
import java.awt.Color
import java.awt.Point
import java.io.FileReader
import kotlin.math.abs

class ParabolaDrawer : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        val opener = Opener()
        opener.setSilentMode(true);
        val image = opener.openImage(args[0]);
        image.show()

        val pro = image.processor

        val lineImage = IJ.createImage("Parabola", "RGB", 512, 512, 1)
        val resultPro = lineImage.processor

        val rayContainer = Gson().fromJson(FileReader("D:\\test.json"), RayContainer::class.java)

        lineImage.setColor(Color.BLUE)
        rayContainer.rays.forEach {
            var foundHost: Point? = null
            var foundGraft: Point? = null

            for (i in 0 until it.x.size) {

                val value = pro.get(it.x[i], it.y[i])

                if (value == -1)
                    continue

                if (value.and(0xFF0000) == 0xFF0000) {
                    foundHost = Point(it.x[i], it.y[i])
                } else if (value.and(0xFF00) == 0xFF00) {
                    foundGraft = Point(it.x[i], it.y[i])
                }

//                if (i + 1 < it.x.size) {
//                    if (abs((it.x[i] + it.y[i]) - (it.x[i + 1] + it.y[i + 1])) != 1) {
//                        if (it.x[i] - it.x[i + 1] != 0) {
////                            if(it.x[i] < it.x[i+1]){
////                                val value2 = pro.get(it.x[i+1], it.y[i])
////                                if(value2 == -1)
////
////                            }else{
////
////                            }
//                        }
//                    }
//
//                }
            }

            if (foundHost != null && foundGraft != null) {
                resultPro.drawLine(foundHost.x, foundHost.y, foundGraft.x, foundGraft.y)
            }
        }


        lineImage.setColor(Color.ORANGE)
        for (x in 400 until 500) {
            val ray = rayContainer.rays[x]
            val startX = ray.x.first()
            val startY = ray.y.first()

            val endX = ray.x.last()
            val endY = ray.y.last()

            resultPro.drawLine(startX, startY, endX, endY)
        }

        plotLine(resultPro, Point(0,10), Point(100,0))

        lineImage.show()
    }

    fun plotLine(ip: ImageProcessor, x0: Point, x1: Point) {

        var dx = abs(x1.x - x0.x)
        var dy = abs(x1.y - x0.y)

        var sx = if (x0.x < x1.x) 1 else -1;
        var sy = if (x0.y < x1.y) 1 else -1;

        var err = dx + dy
        var e2 = 0

        while (true) {
            ip.drawPixel(x0.x, x0.y)

            if (x0.x == x1.x && x0.y == x1.y) break;

            e2 = err * 2

            if (e2 >= dy) {
                err += dy;
                x0.x += sx
            }

            if (e2 <= dx) {
                err += dx
                x0.y += sy;
            }
        }
    }
}

package eu.glatz.imagej

import com.google.gson.Gson
import eu.glatz.imagej.heatmap.mask.Parabola
import eu.glatz.imagej.model.Point
import eu.glatz.imagej.model.ray.Ray
import eu.glatz.imagej.model.ray.RayContainer
import ij.IJ
import ij.io.Opener
import ij.plugin.PlugIn
import ij.process.ImageProcessor
import java.awt.Color
import java.io.File
import java.nio.file.Files
import kotlin.math.pow

class ParabolaFillerRayCreator : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        val opener = Opener()
        opener.setSilentMode(true);
        val imgHost = opener.openImage(args[0]);
        imgHost.show()

        IJ.run("RGB Color");

        //val lineImage = IJ.createImage("Parabola", "RGB", 512, 512, 1)

        val parabola = Parabola(Point(256, 20), 0.004F)

        parabola.draw(imgHost.processor, Color.RED)

        val pro = imgHost.processor

        val startX = 256
        val startY = 20
        val factor = 0.004F

        val rayContainer = RayContainer()

        for (x0 in 0 until 255) {
            pro.setColor(Color.BLACK)
            pro.fillRect(0, 0, 512, 512)
            var startValueX = 0
            var startValueY = 0

            var endValueX = 0
            var endValueY = 0
            var start = false
            pro.setColor(Color.GREEN)
            for (x in 0 until 512) {
                val y = (1 / (-factor * 2 * (x0 - startX))) * (x - x0) + (factor * (x0 - startX).toDouble().pow(2) + startY)
                if (y >= 0 && !start) {
                    startValueX = x
                    startValueY = y.toInt()
                    start = true
                } else if (y > 511 && start) {
                    endValueX = x
                    endValueY = y.toInt()
                    break
                } else if (x == 511) {
                    endValueX = x
                    endValueY = y.toInt()
                }

                //pro.drawPixel(x, y.toInt())
            }
            pro.setColor(Color.RED)
            pro.drawLine(startValueX, startValueY, endValueX, endValueY)
            imgHost.updateAndDraw()

            println("Line $x0 = start($startValueX/$startValueY), end($endValueX,$endValueY)")

            imgHost.updateAndDraw()

            rayContainer.rays.add(getPoints(x0, pro))
            Thread.sleep(20)
        }

        pro.setColor(Color.BLACK)
        pro.fillRect(0, 0, 512, 512)
        pro.setColor(Color.GREEN)
        pro.drawLine(255, 0, 255, 511)
        imgHost.updateAndDraw()
        rayContainer.rays.add(getPoints(255, pro))
        Thread.sleep(20)

        for (x0 in 256 until 512) {
            pro.setColor(Color.BLACK)
            pro.fillRect(0, 0, 512, 512)
            var startValueX = 0
            var startValueY = 0

            var endValueX = 0
            var endValueY = 0
            var start = false
            pro.setColor(Color.BLUE)
            for (x in 0 until 512) {
                val y = (1 / (-factor * 2 * (x0 - startX))) * (x - x0) + (factor * (x0 - startX).toDouble().pow(2) + startY)
                if (y <= 511 && !start) {
                    startValueX = x
                    startValueY = y.toInt()
                    start = true
                } else if (y < 0 && start) {
                    endValueX = x
                    endValueY = y.toInt()
                    break
                } else if (x == 511) {
                    endValueX = x
                    endValueY = y.toInt()
                }

            }
            pro.setColor(Color.ORANGE)
            pro.drawLine(startValueX, startValueY, endValueX, endValueY)
            imgHost.updateAndDraw()

            println("Line $x0 = start($startValueX/$startValueY), end($endValueX,$endValueY)")
            imgHost.updateAndDraw()
            rayContainer.rays.add(getPoints(x0, pro, true))
            Thread.sleep(20)
        }

        Files.write(File("D:/test.json").toPath(), Gson().toJson(rayContainer).toByteArray(Charsets.UTF_8))
    }

    fun getPoints(num: Int, ip: ImageProcessor, inverse: Boolean = false): Ray {
        val xx = mutableListOf<Int>()
        val yy = mutableListOf<Int>()

        for (x in 0 until ip.width) {
            for (y in 0 until ip.height) {
                val t = ip.get(x, y)
                if (ip.get(x, y) != -16777216) {
                    xx.add(x)
                    yy.add(y)
                }
            }
        }

        val xres = xx.toIntArray()
        val yres = yy.toIntArray()

        if (inverse) {
            xres.reverse()
            yres.reverse()
        }


        return Ray(num, xres, yres)
    }
}
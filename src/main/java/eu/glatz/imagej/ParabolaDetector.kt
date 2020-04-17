package eu.glatz.imagej

import com.google.gson.Gson
import eu.glatz.imagej.model.ray.RayContainer
import ij.IJ
import ij.io.Opener
import ij.plugin.PlugIn
import java.awt.Color
import java.io.FileReader

class ParabolaDetector : PlugIn {
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
            for (i in 0 until it.x.size) {
                if (pro.get(it.x[i], it.y[i]) != 0) {
                    resultPro.drawPixel(it.x[i], it.y[i])
                }
            }
        }

        lineImage.show()
    }
}
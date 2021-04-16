package heatmap

import eu.glatz.imagej.N_RayDrawerPlugin
import eu.glatz.imagej.heatmap.ray.RayCalculator
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test
import java.io.File

class N1_RayCreatorTest {


    /**
     * Shows an image containing all lines
     */
    @Test
    fun showConnectedLines() {
        val ij = ImageJ()
        IJ.runPlugIn(N_RayDrawerPlugin::class.qualifiedName, "")

        Thread.sleep(10000)
    }

    /**
     * Saves rays to file
     */
    @Test
    fun calculateRays() {
        val targetCVS = "rays.json"

        val calculator = RayCalculator()
        val rays = calculator.calcRaysStartAndEndPoint()
        calculator.saveRays(rays, File(targetCVS))

        println("Saving rays to ${File(targetCVS).absolutePath}")
    }

}

package heatmap

import eu.glatz.imagej.N_HeatMapCreatorPlugin
import eu.glatz.imagej.N_ProbabilityMapCreatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N5_HeatMapCreatorTest {

    @Test
    fun createHeatMap() {
        val sourceDir = "D:\\Projekte\\vaa_vali_new_test\\50_0.001"
        val saveImageTO = "D:\\Projekte\\vaa_vali_new_test\\out_heatmap"

        // run from net folder
        // IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode toProbabilityMap $sourceDir $saveImageTO")
        // run for ground truth or probability map folder
        IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode $sourceDir $saveImageTO")
    }
}

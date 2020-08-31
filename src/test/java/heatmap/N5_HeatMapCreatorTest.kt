package heatmap

import eu.glatz.imagej.N_HeatMapCreatorPlugin
import eu.glatz.imagej.N_ProbabilityMapCreatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N5_HeatMapCreatorTest {

    @Test
    fun createHeatMap() {
        val sourceDir = "D:\\Projekte\\vaa_export_test_learn_set\\out_250_0.5"
        val saveImageTO = "D:\\Projekte\\vaa_export_test_learn_set\\out_250_0.5_heatmap_raw"

        // run from net folder
        // IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode toProbabilityMap $sourceDir $saveImageTO")
        // run for ground truth or probability map folder
        IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode $sourceDir $saveImageTO")
    }
}

package heatmap

import eu.glatz.imagej.N_HeatMapCreatorPlugin
import eu.glatz.imagej.N_ProbabilityMapCreatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N5_HeatMapCreatorTest {

    @Test
    fun createHeatMap() {
        val sourceDir = "D:\\Projekte\\vaa_vali_compare\\8_net_prediction_pms"
        val saveImageTO = "D:\\Projekte\\vaa_vali_compare\\10_net_pms_heatmaps"

        // run from net folder
        // IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode toProbabilityMap $sourceDir $saveImageTO")
        // run for ground truth or probability map folder
        IJ.runPlugIn(N_HeatMapCreatorPlugin::class.qualifiedName, "folder_mode $sourceDir $saveImageTO")
    }
}

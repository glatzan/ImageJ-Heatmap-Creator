package heatmap

import eu.glatz.imagej.N_MaskCreatorPlugin
import eu.glatz.imagej.N_ProbabilityMapCreatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N3_ProbabilityMapCreatorTest {

    @Test
    fun createProbabilityMaps() {
        val sourceDir = "D:\\Projekte\\vaa_vali_compare\\7_net_prediction_folders"
        val targetDir = "D:\\Projekte\\vaa_vali_compare\\8_net_prediction_pms"
        val targetFolderSuffix = "_pm"

        IJ.runPlugIn(N_ProbabilityMapCreatorPlugin::class.qualifiedName, "folder_mode $sourceDir $targetDir $targetFolderSuffix")
    }
}


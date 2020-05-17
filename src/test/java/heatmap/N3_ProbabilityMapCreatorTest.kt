package heatmap

import eu.glatz.imagej.N_MaskCreatorPlugin
import eu.glatz.imagej.N_ProbabilityMapCreatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N3_ProbabilityMapCreatorTest {

    @Test
    fun createProbabilityMaps() {
        val sourceDir = "D:\\Projekte\\vaa_export_test_learn_set\\out"
        val targetDir = "D:\\Projekte\\vaa_export_test_learn_set\\out2"
        val targetFolderSuffix = "_pm"

        IJ.runPlugIn(N_ProbabilityMapCreatorPlugin::class.qualifiedName, "folder_mode $sourceDir $targetDir $targetFolderSuffix")
    }
}
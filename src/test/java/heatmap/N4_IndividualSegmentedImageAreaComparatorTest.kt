package heatmap

import eu.glatz.imagej.N_IndividualSegmentedImageAreaComparatorPlugin
import eu.glatz.imagej.N_SegmentedAreaComparatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N4_IndividualSegmentedImageAreaComparatorTest {

    /**
     * Compares some ground turth data with many image folders. Matches areas.
     */
    @Test
    fun testSegmentedAreaComparatorForBatches() {
        val groundTruth = "D:\\Projekte\\vaa_vali_compare\\5_vali_mask_optimized_folders"
        val netImages = "D:\\Projekte\\vaa_vali_compare\\9_net_winners"
        val saveCSVTO = "D:\\Projekte\\vaa_vali_compare\\compare_individual.csv"

        IJ.runPlugIn(N_IndividualSegmentedImageAreaComparatorPlugin::class.qualifiedName, "$groundTruth $netImages $saveCSVTO")

        Thread.sleep(100000)
    }
}

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
        val groundTruth = "D:\\Projekte\\vaa_export_test_learn_set\\ground_truth_ray"
        val netImages = "D:\\Projekte\\vaa_export_test_learn_set\\out_post"
        val saveCSVTO = "D:\\Projekte\\vaa_export_test_learn_set\\compare_img.csv"

        IJ.runPlugIn(N_IndividualSegmentedImageAreaComparatorPlugin::class.qualifiedName, "$groundTruth $netImages $saveCSVTO")

        Thread.sleep(100000)
    }
}

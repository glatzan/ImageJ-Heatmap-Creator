package heatmap

import eu.glatz.imagej.N_SegmentedAreaComparatorPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N4_SegmentedAreaComparatorTest {

    /**
     * Compares ground truth with a folder net image folder. Matches areas.
     */
    @Test
    fun testSegmentedAreaComparator(){
        val groundTruth = "D:\\Projekte\\export\\vali\\18894_mask"
        val netImages  = "D:\\Projekte\\export\\vali\\18894_big_150_post"

        IJ.runPlugIn(N_SegmentedAreaComparatorPlugin::class.qualifiedName, "$groundTruth $netImages ")

        Thread.sleep(10000)
    }

    /**
     * Compares some ground truth data with many image folders. Matches areas.
     */
    @Test
    fun testSegmentedAreaComparatorForBatches(){
        val groundTruth = "D:\\Projekte\\vaa_vali_compare\\5_vali_mask_optimized_folders"
        val netImages = "D:\\Projekte\\vaa_vali_compare\\9_net_winners"
        val saveCSVTO = "D:\\Projekte\\vaa_vali_compare\\compare_image_sequence.csv"
        val saveImageTO = "D:\\Projekte\\vaa_vali_compare\\12_compre_ground_net"

        IJ.runPlugIn(N_SegmentedAreaComparatorPlugin::class.qualifiedName, "folder_mode $groundTruth $netImages $saveImageTO $saveCSVTO")

        Thread.sleep(100000)
    }

}

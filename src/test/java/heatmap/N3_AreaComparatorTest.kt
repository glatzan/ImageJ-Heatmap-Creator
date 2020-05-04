package heatmap

import eu.glatz.imagej.N_MaskCreatorPlugin
import eu.glatz.imagej.N_SegmentedAreaComparator
import ij.IJ
import org.junit.jupiter.api.Test

class N3_AreaComparatorTest {

    @Test
    fun testSegmentedAreaComparator(){
        val masks = "D:\\Projekte\\export\\vali\\18894_mask"
        val net  = "D:\\Projekte\\export\\vali\\18894_big_150_post"

        IJ.runPlugIn(N_SegmentedAreaComparator::class.qualifiedName, "$masks $net")

        Thread.sleep(10000)
    }
}

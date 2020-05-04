package heatmap

import eu.glatz.imagej.N_MaskCreatorPlugin
import eu.glatz.imagej.N_SegmentedAreaComparator
import ij.IJ
import org.junit.jupiter.api.Test

class N3_AreaComparatorTest {

    @Test
    fun testSegmentedAreaComparator(){
        val masks = "D:\\Projekte\\test_img\\orig"
        val net  = "D:\\Projekte\\test_img\\net"

        IJ.runPlugIn(N_SegmentedAreaComparator::class.qualifiedName, "$masks $net")
    }
}
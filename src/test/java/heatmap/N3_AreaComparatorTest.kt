package heatmap

import eu.glatz.imagej.N_MaskCreatorPlugin
import eu.glatz.imagej.N_SegmentedAreaComparator
import ij.IJ
import org.junit.jupiter.api.Test

class N3_AreaComparatorTest {

    @Test
    fun testSegmentedAreaComparator(){
        val masks = "D:\\Projekte\\vaa_export_calculated\\new_comp\\mask"
        val net  = "D:\\Projekte\\vaa_export_calculated\\new_comp\\net"

        IJ.runPlugIn(N_SegmentedAreaComparator::class.qualifiedName, "$masks $net")
    }
}

package heatmap

import eu.glatz.imagej.N_MaskCreatorPlugin
import eu.glatz.imagej.N_RayDrawerPlugin
import ij.IJ
import org.junit.jupiter.api.Test

class N2_MaskCreatorTest {

//    @Test
//    fun createMasksTopDown(){
//        val sourceDir = "D:\\Projekte\\vaa_export_calculated\\raw_rg"
//        val targetDir =  "D:\\Projekte\\vaa_export_calculated\\mask_rg"
//
//        IJ.runPlugIn(N_MaskCreatorPlugin::class.qualifiedName, "$sourceDir $targetDir topdown")
//    }
//
//    @Test
//    fun createMasksRay(){
//        val sourceDir = "D:\\Projekte\\export\\optimzed_masks\\21052"
//        val targetDir =  "D:\\Projekte\\export\\optimzed_masks\\21052_export"
//
//        IJ.runPlugIn(N_MaskCreatorPlugin::class.qualifiedName, "$sourceDir $targetDir")
//    }
//
//
//    @Test
//    fun createMasksLine(){
//        val sourceDir = "D:\\Projekte\\vaa_export_calculated\\raw_rg"
//        val targetDir =  "D:\\Projekte\\vaa_export_calculated\\mask_rg"
//
//        IJ.runPlugIn(N_MaskCreatorPlugin::class.qualifiedName, "$sourceDir $targetDir line")
//    }

    /**
     * Best results
     */
    @Test
    fun createMasksParabola(){
        val sourceDir = "D:\\Projekte\\_VAA\\set_validation\\1_mask_images\\0_red_green"
        val targetDir =  "D:\\Projekte\\_VAA\\tmp"

        IJ.runPlugIn(N_MaskCreatorPlugin::class.qualifiedName, "$sourceDir $targetDir parabola")
    }
}

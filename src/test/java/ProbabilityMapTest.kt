import eu.glatz.imagej.HeatMapRunner
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test

class ProbabilityMapTest {

    @Test
    fun test() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")

        val img100 = "D:/Projekte/export/21408_flood_100_results"
        val img200 = "D:/Projekte/export/net_flood_tiny_a_6000_200/21408_flood_a_200_conv"
        val img150big = "D:/Projekte/export/net_flood_big_a_7000_150/21408_big_7000_topdown_conv"
        val origs = "D:/Projekte/export/21408_6000_plain"


        val orig18894 = "D:\\Projekte\\export\\vali\\18894_orig_7000"
        val imgBig18894 = "D:\\Projekte\\export\\vali\\18894_big_150_no_post"

        val orig21659 = "D:\\Projekte\\export\\vali\\21659_orig_7000"
        val imgBig21659 = "D:\\Projekte\\export\\vali\\21659_big_150_no_post"

        val orig21660 = "D:\\Projekte\\export\\vali\\21660_orig_7000"
        val imgBig21660 = "D:\\Projekte\\export\\vali\\21660_big_150_no_post"



        val new = "D:\\Projekte\\export\\21408_grey_flood_big_512_a_50"

        val ij = ImageJ()
        //IJ.runPlugIn(ProbabilityMap::class.qualifiedName, "$img100 $img200 $origs")
        IJ.runPlugIn(HeatMapRunner::class.qualifiedName, "$imgBig21660 $orig21660")

        Thread.sleep(3000000)
    }
}
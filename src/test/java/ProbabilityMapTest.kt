import eu.glatz.imagej.HeatMapRunner
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test

class ProbabilityMapTest {

    @Test
    fun test() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")

        val img100 = "D:/Projekte/export/21408_flood_100_results"
        val img200 = "D:/Projekte/export/21408_flood_200_results"
        val origs = "D:/Projekte/export/21408_flood_100_orig"

        val new = "D:\\Projekte\\export\\21408_grey_flood_big_512_a_50"

        val ij = ImageJ()
        //IJ.runPlugIn(ProbabilityMap::class.qualifiedName, "$img100 $img200 $origs")
        IJ.runPlugIn(HeatMapRunner::class.qualifiedName, "$img200 $origs")

        Thread.sleep(3000000)
    }
}
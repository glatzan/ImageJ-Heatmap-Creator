import eu.glatz.imagej.ProbabilityMap
import eu.glatz.imagej.ProcessGraft
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

        val ij = ImageJ()
        IJ.runPlugIn(ProbabilityMap::class.qualifiedName, "$img100 $img200 $origs")

        Thread.sleep(3000000)
    }
}
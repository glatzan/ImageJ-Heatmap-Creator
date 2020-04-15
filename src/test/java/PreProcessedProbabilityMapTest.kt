import eu.glatz.imagej.PreProcessedHeatMapRunner
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test

class PreProcessedProbabilityMapTest {

    @Test
    fun test() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")

        val mask_21052 = "D:\\Projekte\\vaa_export_calculated\\mask_21052"
        val mask_18896 = "D:\\Projekte\\vaa_export_calculated\\mask_18894"

        val ij = ImageJ()
        IJ.runPlugIn(PreProcessedHeatMapRunner::class.qualifiedName, "$mask_21052 $mask_18896")

        Thread.sleep(3000000)
    }
}
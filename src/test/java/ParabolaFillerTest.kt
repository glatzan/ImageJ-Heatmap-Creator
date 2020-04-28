import eu.glatz.imagej.legacy.ParabolaDetector
import eu.glatz.imagej.ParabolaDrawer
import eu.glatz.imagej.ParabolaFillerRayCreator
import ij.IJ
import ij.ImageJ
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class ParabolaFillerTest {
    @Test
    fun test() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")

        val testImg = "D:\\Projekte\\vaa_export_calculated\\example\\21879-130.png"

        val ij = ImageJ()

        IJ.runPlugIn(ParabolaFillerRayCreator::class.qualifiedName, "$testImg")

        Thread.sleep(300000)
        assertThat("message").isEqualTo("message")

    }


    @Test
    fun test2() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")

        val testImg = "D:\\Projekte\\vaa_export_calculated\\mask_21052\\21052-82_mask.png"

        val ij = ImageJ()

        IJ.runPlugIn(ParabolaDetector::class.qualifiedName, "$testImg")

        Thread.sleep(300000)
        assertThat("message").isEqualTo("message")

    }

    @Test
    fun test3() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")

        val testImg = "D:\\Projekte\\vaa_export_calculated\\test_mask_two_color.png"

        val ij = ImageJ()

        IJ.runPlugIn(ParabolaDrawer::class.qualifiedName, "$testImg")

        Thread.sleep(300000)
        assertThat("message").isEqualTo("message")

    }
}
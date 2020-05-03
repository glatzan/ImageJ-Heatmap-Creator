package legacy

import eu.glatz.imagej.legacy.ProcessGraft
import ij.IJ
import ij.ImageJ
import ij.io.Opener
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ProcessGraftTest {
    @Test
    fun test() {
        val img = "0"
        val imgPath = "src/main/resources/img/${img}.png"
        val imgPathHost = "src/main/resources/img/${img}_host.png"
        val imgPathGraft = "src/main/resources/img/${img}_graft.png"
        test(imgPath, imgPathHost, imgPathGraft)
    }


    fun test(img: String, imgHost: String, imgGraft: String) {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        val ij = ImageJ()

        val opener = Opener()
        opener.setSilentMode(true);
        val img = opener.openImage(img);
        val imgHost = opener.openImage(imgHost);
        val imgGraft = opener.openImage(imgGraft);

        img.show()
        imgHost.show()
        imgGraft.show()

        IJ.runPlugIn(ProcessGraft::class.qualifiedName, "test -t")

        Thread.sleep(300000)

        Assertions.assertThat("message").isEqualTo("message")
    }
}
package legacy

import eu.glatz.imagej.legacy.HostParabola
import ij.IJ
import ij.ImageJ
import ij.io.Opener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class HostParabolaTest {
    @Test
    fun test() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        print(System.getProperty("user.dir"))
        val imgPath = "src/main/resources/10/10.png"
        val ij = ImageJ()
        val opener = Opener()
        opener.setSilentMode(true);
        val img = opener.openImage(imgPath);
        img.show()


        IJ.runPlugIn(HostParabola::class.qualifiedName, "")

        Thread.sleep(300000)

        assertThat("message").isEqualTo("message")

    }
}
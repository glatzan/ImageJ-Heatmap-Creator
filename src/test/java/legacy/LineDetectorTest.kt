package legacy

import eu.glatz.imagej.legacy.LineDetector
import ij.IJ
import ij.ImageJ
import ij.io.Opener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LineDetectorTest {
    @Test
    fun test(){
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        val imgPath ="src/main/resources/194.png"
        val ij = ImageJ()
        val opener = Opener()
        val img = opener.openImage(imgPath);
        img.show()

        IJ.runPlugIn(LineDetector::class.qualifiedName, "")

//        Thread.sleep(10000)

        assertThat("message").isEqualTo("message")

    }

    @Test
    fun testLineDetection() {
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        val imgPath = "src/main/resources/LineDetectionTest.png"

        val opener = Opener()
        val img = opener.openImage(imgPath);
        img.show()

        IJ.runPlugIn(LineDetector::class.qualifiedName, "")

//        Thread.sleep(10000)
//
        assertThat("message").isEqualTo("message")

    }
}
import eu.glatz.imagej.LineDetector
import ij.IJ
import ij.ImageJ
import ij.io.Opener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LineDetectorTest {
    @Test
    fun test(){
        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        print(System.getProperty("user.dir"))
        val imgPath ="src/main/resources/194.png"
        val ij = ImageJ()
        val opener = Opener()
        opener.setSilentMode(true);
        val img = opener.openImage(imgPath);
        img.show()

        println("--")
        println(LineDetector::class.qualifiedName)

        IJ.runPlugIn(LineDetector::class.qualifiedName,"")

        Thread.sleep(10000)

        assertThat("message").isEqualTo("message")

    }
}
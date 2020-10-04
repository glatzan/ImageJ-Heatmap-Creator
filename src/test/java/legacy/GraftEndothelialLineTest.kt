package legacy

import eu.glatz.imagej.legacy.GraftEndothelialLine
import eu.glatz.imagej.legacy.HostParabola
import ij.IJ
import ij.ImageJ
import ij.io.Opener
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GraftEndothelialLineTest {

//    @Test
//    fun test() {
//        val imgPath = "src/main/resources/img/10_.png"
//        val imgPathGraft = "src/main/resources/img/10__graft.png"
//        test(imgPath, imgPathGraft)
//    }
//
//
//    fun test(img: String, imgHost: String) {
//        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
//        val ij = ImageJ()
//
//        val opener = Opener()
//        opener.setSilentMode(true);
//        val img = opener.openImage(img);
//        val imgHost = opener.openImage(imgHost);
//
//        img.show()
//        IJ.runPlugIn(HostParabola::class.qualifiedName, "")
//        GraftEndothelialLine.hostParabola = HostParabola.hostParabola
//
//        imgHost.show()
//        IJ.runPlugIn(imgHost, GraftEndothelialLine::class.qualifiedName, "test -t")
//
//        Thread.sleep(300000)
//
//        Assertions.assertThat("message").isEqualTo("message")
//    }
}

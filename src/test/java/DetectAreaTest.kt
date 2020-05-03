import eu.glatz.imagej.DetectedAreaComparator
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test

class DetectAreaTest {

    @Test
    fun test(){

        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        val imgGround = "D:\\Projekte\\export\\vali\\18894_mask"
        val imgBig150NoPost = "D:\\Projekte\\export\\vali\\18894_big_150_no_post"
        val imgBig150Post = "D:\\Projekte\\export\\vali\\18894_big_150_post"


        val ij = ImageJ()
        IJ.runPlugIn(DetectedAreaComparator::class.qualifiedName, "$imgGround $imgBig150NoPost $imgBig150Post")

        Thread.sleep(3000000)
    }
}
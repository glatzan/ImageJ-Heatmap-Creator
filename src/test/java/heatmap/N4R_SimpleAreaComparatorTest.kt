package heatmap

import eu.glatz.imagej.N_SimpleAreaComparatorPlugin
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test

class N4R_SimpleAreaComparatorTest {

    //TODO refactor
    @Test
    fun test(){

        System.setProperty("plugins.dir", "D:\\Projekte\\Fiji.app\\plugins")
        val imgGround = "D:\\Projekte\\export\\vali\\18894_mask"
        val imgBig150NoPost = "D:\\Projekte\\export\\vali\\18894_big_150_no_post"
        val imgBig150Post = "D:\\Projekte\\export\\vali\\18894_big_150_post"


        val ij = ImageJ()
        IJ.runPlugIn(N_SimpleAreaComparatorPlugin::class.qualifiedName, "$imgGround $imgBig150NoPost $imgBig150Post")

        Thread.sleep(3000000)
    }
}
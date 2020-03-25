package eu.glatz.imagej

import eu.glatz.imagej.model.Point
import eu.glatz.imagej.util.DrawUtil
import ij.IJ
import ij.WindowManager
import ij.plugin.PlugIn
import java.awt.Color

class ProcessGraft : PlugIn {
    override fun run(p0: String?) {
        println()
        val titles = WindowManager.getImageTitles()

        if (WindowManager.getImageCount() != 3) {
            IJ.error("3 Images have to be opened!")
            return
        }

        val host = titles.firstOrNull { it.matches(Regex(".*_host.*")) }
        val graft = titles.firstOrNull { it.matches(Regex(".*_graft.*")) }
        val img = titles.firstOrNull { !it.matches(Regex(".*_graft.*")) && !it.matches(Regex(".*_host.*")) }

        if (host == null || graft == null || img == null) {
            IJ.error("Original, _graft and _host")
            return
        }

        val orig_image = WindowManager.getImage(img)
        val host_image = WindowManager.getImage(host)
        val graft_image = WindowManager.getImage(graft)

        val resultOrig = orig_image.duplicate()
        val host_image_orig = host_image.duplicate()
        val graft_image_orig = graft_image.duplicate()
        val orig2 = orig_image.duplicate()


        IJ.runPlugIn(orig_image, HostParabola::class.qualifiedName, "debug=false")
        val parabola = HostParabola.hostParabola

        HostEndothelialLine.hostParabola = parabola
        IJ.runPlugIn(host_image, HostEndothelialLine::class.qualifiedName, "")

        GraftEndothelialLine.hostParabola = parabola
        IJ.runPlugIn(graft_image, GraftEndothelialLine::class.qualifiedName, "test -t")

        val result = IJ.createImage("Graft__AcceptedLines", "RGB", orig_image.width, orig_image.height, 1)

        DrawUtil.fillRect(Point(0, 0), result.width, result.height, Color(0, 0, 0), result.processor)

        DrawUtil.drawLine(HostEndothelialLine.resultHostLine, Color(255, 0, 0), result.processor)
        GraftEndothelialLine.resultGraftLines.forEach { DrawUtil.drawLine(it, Color(0, 255, 0), result.processor) }

//        IJ.runPlugIn(result, CalculateDetachmentVolume::class.qualifiedName, "test -t")
//
//        CalculateDetachmentVolume.result.forEach {
//            DrawUtil.drawLine(it, Color(0, 0, 255), result.processor)
//        }

        result.show()
        IJ.run(resultOrig,"Add Image...", "image=Graft__AcceptedLines x=0 y=0 opacity=100 zero");
        orig2.show()
        resultOrig.show()
        host_image_orig.show()
        graft_image_orig.show()

    }
}
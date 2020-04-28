package heatmap

import eu.glatz.imagej.heatmap.LineCalculator
import ij.IJ
import ij.ImageJ
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.Point

class LineCalculatorTest {

    @Test
    fun testLine(){
        val p1 = Point(0,1)
        val p2 = Point(190,10)


        val p11 = Point(30,100)
        val p22 = Point(190,50)


        val p1y = Point(30,0)
        val p2y = Point(60,150)

        val p1y2 = Point(90,150)
        val p2y2 = Point(120,5)

        val ij = ImageJ()

        val resultImage = IJ.createImage("Segmentation Result", "RGB", 200, 200,1)
        val resultProcessor = resultImage.processor

        resultImage.show()

        val line = LineCalculator(p1,p2)
        val points = line.getIntersectionPixels()

        resultProcessor.setColor(Color.RED)
        resultProcessor.drawPixel(p1.x,p1.y)

        resultProcessor.setColor(Color.BLUE)
        resultProcessor.drawPixel(p2.x,p2.y)

        Thread.sleep(10)

        for(p in points){

            if(p == p1 ||p == p2)
                resultProcessor.setColor(Color.YELLOW)
            else
                resultProcessor.setColor(Color.BLACK)

            resultProcessor.drawPixel(p.x,p.y)
            resultImage.updateAndDraw()

            Thread.sleep(10)
        }

        resultProcessor.setColor(Color.RED)
        resultProcessor.drawPixel(p11.x,p11.y)

        resultProcessor.setColor(Color.BLUE)
        resultProcessor.drawPixel(p22.x,p22.y)

        val line2 = LineCalculator(p11,p22)
        val points2 = line2.getIntersectionPixels()

        for(p in points2){

            if(p == p11 ||p == p22)
                resultProcessor.setColor(Color.YELLOW)
            else
                resultProcessor.setColor(Color.BLACK)

            resultProcessor.drawPixel(p.x,p.y)
            resultImage.updateAndDraw()

            Thread.sleep(10)
        }

        val lineY = LineCalculator(p1y,p2y)
        val pointsY = lineY.getIntersectionPixels()

        for(p in pointsY){

            if(p == p11 ||p == p22)
                resultProcessor.setColor(Color.YELLOW)
            else
                resultProcessor.setColor(Color.BLACK)

            resultProcessor.drawPixel(p.x,p.y)
            resultImage.updateAndDraw()

            Thread.sleep(10)
        }


        val lineY2 = LineCalculator(p1y2,p2y2)
        val pointsY2 = lineY2.getIntersectionPixels()

        for(p in pointsY2){

            if(p == p11 ||p == p22)
                resultProcessor.setColor(Color.YELLOW)
            else
                resultProcessor.setColor(Color.BLACK)

            resultProcessor.drawPixel(p.x,p.y)
            resultImage.updateAndDraw()

            Thread.sleep(10)
        }

        Thread.sleep(50000)
    }
}
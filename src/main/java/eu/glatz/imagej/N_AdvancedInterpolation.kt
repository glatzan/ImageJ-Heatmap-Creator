package eu.glatz.imagej

import eu.glatz.imagej.heatmap.segmentaion.ImageSegmentation
import eu.glatz.imagej.heatmap.segmentaion.SegmentedImage
import ij.IJ
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import ij.process.ImageProcessor
import java.awt.Color
import java.util.*
import kotlin.collections.HashMap

class N_AdvancedInterpolation : PlugIn {
    override fun run(args: String) {
        val netFolder = "D:\\Projekte\\vaa_vali_new_test\\512_n2_23355_om-t-200_0.45"

        val opener = FolderOpener()
        val images = opener.openFolder(netFolder)

        val results = mutableListOf<ImageAndSegmentContainer>()

        for (i in 0 until images.stackSize) {
            val netSegments = ImageSegmentation.imageSegmentation(images.imageStack.getProcessor(i + 1)).map { AdvancedImageSegment(it) }
            results.add(ImageAndSegmentContainer(i, images.imageStack.getProcessor(i + 1), netSegments))
        }

        connectImageSegments(results)

        drawImageSegments(results)
    }

    fun connectImageSegments(imageContainer: List<ImageAndSegmentContainer>) {

        val searchForNImages = 5
        var id_counter = 0;

        val activeFront = mutableListOf<AdvanceSegmentImageFinder>()
        val newActiveFront = mutableListOf<AdvanceSegmentImageFinder>()

        for (container in imageContainer) {
            for (imageFinder in activeFront) {
                var found = false
                for (segment in container.segments) {
                    if (imageFinder.imageSegment.dimension.intersects(segment.dimension)) {

                        val activeSegment = imageFinder.imageSegment
                        if (activeSegment.segmentID.isBlank()) {
                            activeSegment.segmentID = "n-" + id_counter.toString()
                            id_counter++
                        }

                        var connectionID = activeSegment.segmentID.replace("n-", "")

//                        if (activeSegment.nextSegment.size > 0) {
//                            connectionID = connectionID + ":" + activeSegment.nextSegment.size.toString() + ":"
//                        }

                        if (segment.previousSegments.size > 0) {
                            connectionID = segment.segmentID + "_" + connectionID

                            if (!connectionID.startsWith("n-"))
                                connectionID = "n-" + connectionID
                        }

                        segment.segmentID = connectionID
                        imageFinder.imageSegment.nextSegment.add(segment)
                        segment.previousSegments.add(imageFinder.imageSegment)
                        found = true
                    }
                }
                if (!found) {
                    if (imageFinder.distanceWithoutAssociatedImage <= searchForNImages) {
                        imageFinder.distanceWithoutAssociatedImage++
                        newActiveFront.add(imageFinder)
                    }
                }
            }

            activeFront.clear()
            activeFront.addAll(newActiveFront)
            activeFront.addAll(container.segments.map { AdvanceSegmentImageFinder(it) })
            newActiveFront.clear()
        }

    }

    fun drawImageSegments(imageSegments: List<ImageAndSegmentContainer>) {
        val resultImage = IJ.createImage("Parabola", "RGB", 1024, 1024, imageSegments.size)

        val colorMap = hashMapOf<String, Color>()

        fun getColor(id: String): Color {
            if (id.startsWith("n-")) {
                val color = getRandomColor()
                colorMap.put(id.replace("n-", ""), color)
                return color
            } else {
                val nid = id.replace(Regex(":(.*?):"), "")
                val color = colorMap[nid]
                if (color == null) {
                    val color2 = getRandomColor()
                    colorMap.put(nid, color2)
                    return color2
                } else {
                    return color
                }
            }
        }

        for (i in 0 until imageSegments.size) {
            val colorProcessor = resultImage.imageStack.getProcessor(i + 1)
            val sourceProcessor = imageSegments.get(i).imageProcessor
            colorProcessor.setColor(Color.BLACK)
            for (x in 0 until sourceProcessor.width) {
                for (y in 0 until sourceProcessor.height) {
                    if (sourceProcessor.get(x, y) != 0)
                        colorProcessor.drawDot(256+x, 256+y)
                }
            }

            val imageSegment = imageSegments[i]

            for (segment in imageSegment.segments) {
                if(segment.segmentID.isBlank()){
                    colorProcessor.setColor(Color.RED)
                    colorProcessor.fillRect(256 + segment.dimension.x, 256 + segment.dimension.y, segment.dimension.width, segment.dimension.height)
                }else {
                    colorProcessor.setColor(getColor(segment.segmentID))
                    colorProcessor.drawRect(256 + segment.dimension.x, 256 + segment.dimension.y, segment.dimension.width, segment.dimension.height)
                }
                colorProcessor.drawString(segment.segmentID, 256+segment.dimension.x + segment.dimension.width + 5,256+ segment.dimension.y)
            }
        }

        resultImage.updateAndDraw()
        resultImage.show()
    }


    class ImageAndSegmentContainer {
        var imageNumber = 0
        var imageProcessor: ImageProcessor
        var segments = mutableListOf<AdvancedImageSegment>();


        constructor(imageNumber: Int, imageProcessor: ImageProcessor, list: List<AdvancedImageSegment>) {
            this.imageNumber = imageNumber
            this.segments.addAll(list)
            this.imageProcessor = imageProcessor
        }
    }

    class AdvanceSegmentImageFinder(val imageSegment: AdvancedImageSegment, var distanceWithoutAssociatedImage: Int = 0)

    class AdvancedImageSegment(segmentedImage: SegmentedImage) : SegmentedImage(segmentedImage.number) {
        init {
            this.dimension = segmentedImage.dimension
            this.pixelMap = segmentedImage.pixelMap
            this.pixels = segmentedImage.pixels
        }

        var segmentID = ""

        val nextSegment = mutableListOf<AdvancedImageSegment>()
        val previousSegments = mutableListOf<AdvancedImageSegment>()
    }

    fun getRandomColor(): Color {
        val rand = Random()
        val r = rand.nextFloat()
        val g = rand.nextFloat()
        val b = rand.nextFloat()
        return Color(r, g, b)
    }

}


fun main(vararg args: String) {
    IJ.runPlugIn(N_AdvancedInterpolation::class.qualifiedName, "")
    Thread.sleep(10000)
}

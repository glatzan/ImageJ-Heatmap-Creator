package eu.glatz.imagej

import ij.IJ
import ij.ImagePlus
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.awt.Color
import java.io.File

class DetectedAreaComparator : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        if (args.size < 2)
            throw IllegalArgumentException("2 Arguments")

        val opener = FolderOpener()

        var fileCount = -1;

        for (f in args) {
            val ff = File(f)
            if (!ff.isDirectory)
                throw IllegalArgumentException("Argument must be folder")

            if (fileCount == -1)
                fileCount = ff.listFiles().size
            else if (fileCount != ff.listFiles().size)
                throw IllegalArgumentException("every folder hast to contain the same amount of images")
        }

        val groundTruth = opener.openFolder(File(args[0]).path)
        val stackComparators = mutableListOf<StackComparator>()

        for (i in 1 until args.size) {
            val comparator = StackComparator(groundTruth, opener.openFolder(File(args[i]).path))
            stackComparators.add(comparator)
        }

        for (comparator in stackComparators) {
            val lineImage = IJ.createImage("Result ${comparator.img1.title} - ${comparator.img2.title}", "RGB", 512, 512, 1)
            val resultPro = lineImage.processor

            for (i in 0 until comparator.img1.imageStackSize) {
                val gProcessor = comparator.img1.imageStack.getProcessor(i + 1)
                val cProcessor = comparator.img2.imageStack.getProcessor(i + 1)
                for (x in 0 until gProcessor.width) {
                    for (y in 0 until gProcessor.height) {
                        val t = gProcessor.get(x, y)
                        if (gProcessor.get(x, y) != 0) {
                            comparator.countImg1[i]++
                        }

                        if (cProcessor.get(x, y) != 0) {
                            comparator.countImg2[i]++
                        }

                        if (gProcessor.get(x, y) != 0 && gProcessor.get(x, y) == cProcessor.get(x, y)) {
                            comparator.countMatching[i]++
                        }
                    }
                }
            }

            val maxOverlapping = (comparator.countMatching.max() ?: 0) + 20
            var arr1 = 0
            var arr2 = 0

            for (i in 0 until comparator.countMatching.size) {
                arr1 += comparator.countImg1[i]
                arr2 += comparator.countImg2[i]

                if (comparator.countImg1[i] == comparator.countImg2[i]) {
                    if (comparator.countImg1[i] != 0) {
                        resultPro.setColor(Color.ORANGE)
                        resultPro.drawRect((i * 2), 512 - (comparator.countImg1[i] * 512 / maxOverlapping).toInt(), 2, (comparator.countImg1[i] * 512 / maxOverlapping).toInt())
                    }
                } else if (comparator.countImg1[i] > comparator.countImg2[i]) {
                    resultPro.setColor(Color.ORANGE)
                    resultPro.drawRect((i * 2), 512 - (comparator.countImg2[i] * 512 / maxOverlapping).toInt(), 2, (comparator.countImg2[i] * 512 / maxOverlapping).toInt())
                    resultPro.setColor(Color.GREEN)
                    resultPro.drawRect((i * 2), 512 - (comparator.countImg1[i] * 512 / maxOverlapping).toInt(), 2, ((comparator.countImg1[i] * 512 / maxOverlapping).toInt() - (comparator.countImg2[i] * 512 / maxOverlapping).toInt()))
                } else {
                    resultPro.setColor(Color.ORANGE)
                    resultPro.drawRect((i * 2), 512 - (comparator.countImg1[i] * 512 / maxOverlapping).toInt(), 2, (comparator.countImg1[i] * 512 / maxOverlapping).toInt())
                    resultPro.setColor(Color.GREEN)
                    resultPro.drawRect((i * 2), 512 - (comparator.countImg2[i] * 512 / maxOverlapping).toInt(), 2, (((comparator.countImg2[i] * 512 / maxOverlapping).toInt() - comparator.countImg1[i] * 512 / maxOverlapping).toInt()))
                }
                resultPro.setColor(Color.BLACK)
                resultPro.drawLine(i * 2, 512 - (comparator.countMatching[i] * 512 / maxOverlapping).toInt(), i * 2 + 1, 512 - (comparator.countMatching[i] * 512 / maxOverlapping).toInt())
            }

            resultPro.setColor(Color.BLACK)
            resultPro.drawString("Name: Result ${comparator.img1.title} - ${comparator.img2.title}", 10, 30)
            resultPro.drawString("MAX: ${maxOverlapping - 20} ", 10, 50)
            resultPro.drawString("Orig Arr: ${arr1} = 1.0", 10, 70)
            resultPro.drawString("Comapre Arr: ${arr2} = ${arr2.toDouble()/arr1.toDouble()} ", 10, 90)

            lineImage.updateAndDraw()
            lineImage.show()
        }
    }

    class StackComparator {

        var img1: ImagePlus
        var img2: ImagePlus
        var countMatching: IntArray
        var countImg1: IntArray
        var countImg2: IntArray

        constructor(img1: ImagePlus, img2: ImagePlus) {
            this.img1 = img1
            this.img2 = img2
            this.countMatching = IntArray(img1.width)
            this.countImg1 = IntArray(img1.width)
            this.countImg2 = IntArray(img1.width)
        }

    }
}
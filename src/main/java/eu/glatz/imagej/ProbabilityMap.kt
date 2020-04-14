package eu.glatz.imagej

import ij.IJ
import ij.ImagePlus
import ij.ImageStack
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import ij.process.ColorProcessor
import java.awt.Color
import java.io.File
import kotlin.math.floor

class ProbabilityMap : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        if (args.size == 0) {
            IJ.error("Provide Arguments")
            return
        }

        val weights = floatArrayOf(0.05F, 0.1F, 0.1F, 0.2F, 0.5F, 0.2F, 0.1F, 0.1F, 0.05F)
        val to = if (args.size == 1) 1 else args.size - 1

        for (i in 0 until to) {
            val imageFolder = File(args[i])
            if (!imageFolder.isDirectory) {
                IJ.error("Argument 1 is not a Folder!")
                return
            }

            calcProbabilityMap(imageFolder, weights, true)

        }

        if (args.size > 1) {
            val origFolder = File(args[args.size - 1])

            if (!origFolder.isDirectory) {
                IJ.error("Argument 2 is not a Folder!")
                return
            }

            val origStack = FolderOpener.open(origFolder.path)

            origStack.title = "Originale"
            origStack.show()
        }
    }

    fun calcProbabilityMap(imageFolder: File, weights: FloatArray, showOriginal: Boolean = false) {

        val origStack = FolderOpener.open(imageFolder.path)

        val imageCount = origStack.imageStackSize

        if (imageCount <= 1)
            return

        val probabiltieArray = array3D(imageCount, 512, 512)

        val weightsLoop = floor(weights.size / 2F).toInt()

        val probabilityStack = ImageStack(512, 512)

        for (i in 0 until imageCount) {
            val processor = origStack.imageStack.getProcessor(i + 1)
            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    for (n in -weightsLoop..weightsLoop) {
                        if (i + n >= imageCount)
                            break
                        else if (i + n < 0) {
                        } else
                            probabiltieArray[(i + n)][x][y] += if (processor.get(x, y) > 0) 1 * weights[n + weightsLoop] else 0F
                    }
                }

            }
        }

        for (i in 1..origStack.imageStackSize) {
            val imageProcessor = ColorProcessor(512, 512)

            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    val prob = probabiltieArray[i - 1][x][y]
                    if (prob > 0) {
                        if (prob > 1)
                            imageProcessor.setColor(Color(255, 255, 0))
                        else
                            imageProcessor.setColor(Color((1 - prob), prob, 0F))
                    } else {
                        imageProcessor.setColor(0)
                    }
                    imageProcessor.drawPixel(x, y)
                }
            }

            probabilityStack.addSlice(imageProcessor)
        }

        ImagePlus(imageFolder.name, probabilityStack).show()

        if (showOriginal) {
            origStack.title = "${imageFolder.name} Original"
            origStack.show()
        }
    }


    fun array3D(dim1: Int, dim2: Int, dim3: Int, def: (Int, Int, Int) -> Float = { _, _, _ -> 0.0F })
            : Array<Array<Array<Float>>> {
        return Array(dim1) { i ->
            Array(dim2) { j ->
                Array(dim3) { k -> def(i, j, k) }
            }
        }
    }
}
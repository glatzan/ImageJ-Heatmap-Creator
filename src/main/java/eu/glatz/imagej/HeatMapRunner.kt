package eu.glatz.imagej

import eu.glatz.imagej.heatmap.HeatMapProcessor
import ij.IJ
import ij.ImagePlus
import ij.ImageStack
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import ij.process.ColorProcessor
import java.awt.Color
import java.io.File
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin

class HeatMapRunner : PlugIn {
    override fun run(p0: String?) {

        val args = p0?.split(" ") ?: return

        if (args.size == 0) {
            IJ.error("Provide Arguments")
            return
        }

        val weights = floatArrayOf(0.05F, 0.1F, 0.2F, 0.2F, 0.5F, 0.2F, 0.2F, 0.1F, 0.05F)
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

        val processor = HeatMapProcessor()
        processor.loadProbabilityMap(imageFolder)

        val origStack = FolderOpener.open(imageFolder.path)

        val imageCount = origStack.imageStackSize

        if (imageCount <= 1)
            return

        val probabiltieArray = array3D(imageCount, 512, 512)

        val weightsLoop = floor(weights.size / 2F).toInt()

        val probabilityStack = ImageStack(512, 512)

        for (i in 0 until imageCount) {
            val processor = origStack.imageStack.getProcessor(i + 1)
            if (i - weightsLoop < 0)
                for (x in i - weightsLoop until 0)
                    println("Adding image " + i + " to " + (imageCount + (x)))

            if (i + weightsLoop >= imageCount)
                for (x in 0 until (i + weightsLoop) - imageCount)
                    println("Adding image " + i + " to " + ((i + weightsLoop) - imageCount))

            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    for (n in -weightsLoop..weightsLoop) {
                        if (i + n >= imageCount)
                            probabiltieArray[(i + n) - imageCount][processor.width - 1 - x][y] += if (processor.get(x, y) > 0) 1 * weights[n + weightsLoop] else 0F
                        else if (i + n < 0) {
//                            println("Adding Image " + (i) + " to "+ (imageCount + (i + n)) + " x " + x + " new X "+ (processor.width - 1 -x) + " y " + y)
                            probabiltieArray[imageCount + (i + n)][processor.width - 1 - x][y] += if (processor.get(x, y) > 0) 1 * weights[n + weightsLoop] else 0F
                        } else
                            probabiltieArray[(i + n)][x][y] += if (processor.get(x, y) > 0) 1 * weights[n + weightsLoop] else 0F
                    }
                }

            }
        }


        val heatMapValues = array2D(512, 512)
        var heatMapMax = Int.MIN_VALUE

        for (i in 1..origStack.imageStackSize) {
            val imageProcessor = ColorProcessor(512, 512)

            for (x in 0 until 512) {

                var found = 0
                for (y in 0 until 512) {
                    val prob = probabiltieArray[i - 1][x][y]
                    if (prob > 0) {
                        if (prob >= 10)
                            imageProcessor.setColor(Color(255, 255, 255))
                        else if (prob >= 1) {
                            imageProcessor.setColor(Color(255, 255, 0))
                            found++
                        } else
                            imageProcessor.setColor(Color((1 - prob), prob, 0F))
                    } else {
                        imageProcessor.setColor(0)
                    }
                    imageProcessor.drawPixel(x, y)
                }

                if (found > 0) {
                    heatMapMax = updateHeatmapValues(i - 1, x, found, heatMapValues, heatMapMax)
                }
            }

            probabilityStack.addSlice(imageProcessor)
        }

        interpolateHeatmap(heatMapValues, heatMapMax)
        drawHeatmap(heatMapValues, heatMapMax)

        ImagePlus(imageFolder.name, probabilityStack).show()

        //HeatMapProcessor().createHeatMap(heatMapValues).show()


        if (showOriginal) {
            origStack.title = "${imageFolder.name} Original"
            origStack.show()
        }
    }

    fun updateHeatmapValues(image: Int, r: Int, value: Int, heatMapValues: Array<Array<Int>>, maxValue: Int): Int {
        val x = ((r - 256) * cos(2 * Math.PI - Math.toRadians(image * 0.706))).toInt() + 256
        val y = ((r - 256) * sin(2 * Math.PI - Math.toRadians(image * 0.706))).toInt() + 256

        heatMapValues[x][y] = value

        if (value > maxValue)
            return value
        else
            return maxValue
    }

    fun interpolateHeatmap(heatMapValues: Array<Array<Int>>, maxValue: Int) {

//        val image = IJ.createImage("my t image", "RGB", 1024, 1024, 1)
//        val pB = image.processor as ColorProcessor
//        println(pB.bitDepth)
//        pB.setColor(Color(0, 0, 0))
//        pB.fillRect(0, 0, 1024, 1024)
//
//        var value = 0
//        var count = 0
//
//        for (x in 2 until 512 - 3 step 1) {
//            for (y in 2 until 521 - 3 step 1) {
//                for (xx in -2..2) {
//                    for (yy in -2..2) {
//                        if (heatMapValues[x + xx][y + yy] != 0) {
//                            value += heatMapValues[x + xx][y + yy]
//                            count++
//                        }
//                    }
//                }
//
//                if (count > 0) {
//                    val calc = (255 * (value / count) / maxValue).toInt()
//                    pB.setColor(Color(calc, calc, 0, calc))
//                    pB.drawPixel(x, y)
//                }
//
//                value = 0
//                count = 0
//
//
//            }
//        }

//        for (x in 0 until 1024 step 1) {
//            for (y in 0 until 1024 step 1) {
//                if(heatMapValues[x][y] > 0){
//                    var calc = (255 * heatMapValues[x][y] / maxValue).toInt()
//                    calc = if (calc > 255) 255 else calc
//                    pB.setColor(Color(0, 0, calc,calc))
//                    pB.drawPixel(x, y)
//                }else {
//                    var maxScore = -Double.MAX_VALUE
//                    for (p in points.keys) {
//                        var score = points[p] ?: continue
//                        score /= p.distance(x.toDouble(), y.toDouble()) //Inverse distance times point weight
//                        maxScore = Math.max(maxScore, score)
//                    }
//
//                    var calc = (255 * maxScore / maxValue).toInt()
//                    if(calc == 0){
//                        pB.setColor(Color(0, 0, 0))
//                    }else {
//                        calc = if (calc > 255) 255 else calc
//                        pB.setColor(Color(0, 0, calc,calc))
//                        pB.drawPixel(x, y)
//                    }
//                }
//            }
//        }

//        image.show()

//        var mean = 0
//        var count = 0
//
//
//        for (x in 0 until 1024 step 2) {
//            for (y in 0 until 1024 step 2) {
//                for(zx in 0 until 2){
//                    for(zy in 0 until 2){
//                        if(heatMapValues[x+zx][y +zy] > 0){
//                            mean += heatMapValues[x+zx][y +zy]
//                            count++
//                        }
//                    }
//                }
//
//                if(count != 4 && count != 0){
//                    mean /= count
//                    for(zx in 0 until 2){
//                        for(zy in 0 until 2){
//                            if(heatMapValues[x+zx][y +zy]  == 0){
//                                heatMapValues[x+zx][y +zy] = mean
//                            }
//                        }
//                    }
//                }
//
//                mean = 0
//                count = 0
//            }
//        }
    }

    fun drawHeatmap(heatMapValues: Array<Array<Int>>, maxValue: Int) {
        val image = IJ.createImage("my image", "RGB", 512, 512, 1)
        val pB = image.processor as ColorProcessor

        for (x in 0 until 512) {
            for (y in 0 until 512) {
                if (heatMapValues[x][y] > 0) {
                    if ((255 * heatMapValues[x][y] / maxValue) > 255)
                        println((255 * heatMapValues[x][y] / maxValue))

                    val calc = 255 * heatMapValues[x][y] / maxValue
                    pB.setColor(Color(calc, calc, 255 - calc, calc))
                    pB.drawPixel(x, y)
                }
            }
        }

        image.show()
    }

    fun draw(p: ColorProcessor, number: Int, r: Int, color: Int) {
        val x = ((r - 256) * cos(2 * Math.PI - Math.toRadians(number * 0.706))).toInt() + 512
        val y = ((r - 256) * sin(2 * Math.PI - Math.toRadians(number * 0.706))).toInt() + 512
        println(color)
        p.setColor(Color(round(number * 0.706).toInt(), 0, if (100 + color >= 255) color else 100 + color))
        p.drawPixel(x, y)
    }


    fun array2D(dim1: Int, dim2: Int, def: (Int, Int) -> Int = { _, _ -> 0 }): Array<Array<Int>> {
        return Array(dim1) { i ->
            Array(dim2) { j -> def(i, j) }
        }
    }

    fun array2DD(dim1: Int, dim2: Int, def: (Int, Int) -> Double = { _, _ -> 0.0 }): Array<DoubleArray> {
        return Array(dim1) { i ->
            DoubleArray(dim2) { j -> def(i, j) }
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
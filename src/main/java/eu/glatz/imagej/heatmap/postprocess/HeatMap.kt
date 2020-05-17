package eu.glatz.imagej.heatmap.postprocess

class HeatMap(val width: Int, val height: Int) {

    var data: Array<IntArray>

    init {
        this.data = intArray2D(width, height)
    }

    private fun intArray2D(dim1: Int, dim2: Int, def: (Int, Int) -> Int = { _, _ -> 0 }): Array<IntArray> {
        return Array(dim1) { i ->
            IntArray(dim2) { j -> def(i, j) }
        }
    }

    fun findMaxValue(): Int {
        var max = Int.MIN_VALUE
        for (x in 0 until width)
            for (y in 0 until height)
                if (max < data[x][y])
                    max = data[x][y]
        return max
    }
}
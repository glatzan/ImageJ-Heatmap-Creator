package eu.glatz.imagej.heatmap

class ProbabilityMap(val count: Int, val width: Int, val height: Int) {

    var data: Array<Array<FloatArray>>

    init {
        this.data = floatArray3D(count, width, height)
    }

    private fun floatArray3D(dim1: Int, dim2: Int, dim3: Int, def: (Int, Int, Int) -> Float = { _, _, _ -> 0.0F })
            : Array<Array<FloatArray>> {
        return Array(dim1) { i ->
            Array(dim2) { j ->
                FloatArray(dim3) { k -> def(i, j, k) }
            }
        }
    }
}
package net.kurobako.gemstone.isa.spirv.graphics

private case class Fragment(
    x: Int,
    y: Int,
    z: Float,
    reciprocalW: Float,
    front: Boolean,
    coverage: Int,
    depth: IArray[Float],
    varying: Map[Int, Array[Float]],
    pointCoord: IArray[Float],
    sample: Int
)

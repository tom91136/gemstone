package net.kurobako.gemstone.isa.spirv.v1_6.graphics

private case class Fragment(
    x: Int,
    y: Int,
    z: Float,
    reciprocalW: Float,
    front: Boolean,
    coverage: Int,
    depth: Array[Float],
    varying: Map[Int, Array[Float]],
    pointCoord: Array[Float],
    sample: Int
)

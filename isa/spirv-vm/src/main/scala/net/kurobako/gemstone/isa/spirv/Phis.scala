package net.kurobako.gemstone.isa.spirv

/** The consecutive OpPhi instructions at a block entry, which all read their values before any is written: their
  * results, result types and the labels and values of their incoming edges, whether each has only scalar slots, and the
  * position of the instruction after them.
  */
private class Phis(
    val destinations: Array[Int],
    val types: Array[Int],
    val parents: Array[Array[Int]],
    val sources: Array[Array[Int]],
    val scalar: Array[Boolean],
    val next: Int
)

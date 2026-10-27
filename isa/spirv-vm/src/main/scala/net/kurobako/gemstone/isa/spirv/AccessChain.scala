package net.kurobako.gemstone.isa.spirv

import VMMemory.*

/** The access chain of an instruction: the program whose layout it walks, the type its base is walked as where the
  * chain is untyped, and the type its result points at. A rendering walks from `sample` and takes the value of each
  * index from `literals` where it is fixed.
  */
private class AccessChain(
    val program: VMProgram,
    val baseType: Option[Int],
    val pointee: Int,
    val baseStride: Option[Long],
    val sample: Pointer = Pointer(0L, 0, StorageClass.CrossWorkgroup),
    val literals: Array[Long] = Array.emptyLongArray
) {

  /** An untyped chain walks its base type, and its result points at the type it reaches. */
  val walked: Boolean = program.untyped(pointee)
}

package net.kurobako.gemstone.isa.spirv

private object Indirect {

  /** The op that calls a function, once the id of the function is known. */
  trait Lowering { def apply(function: Int): Op }
}

/** A call to the function `pointer` holds, which `callee` lowers once the function is known. */
private class Indirect(val pointer: Int, val arguments: Array[Int], val callee: Indirect.Lowering)

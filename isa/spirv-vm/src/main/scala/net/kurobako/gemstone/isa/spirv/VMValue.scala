package net.kurobako.gemstone.isa.spirv

import VMMemory.*

private object VMValue {

  /** Whether the value is a scalar with a non-zero lane. */
  def isTrue(value: VMValue): Boolean = value match {
    case VMValue.Bits(_, Array(lane)) => lane != 0L
    case _                            => false
  }
}

private enum VMValue {
  def typeId: Int

  case Bits(typeId: Int, lanes: Array[Long])
  case Address(typeId: Int, pointer: Pointer)
}

package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*

private object VMValue {
  def fromConstant(constant: Constant): VMValue = constant match {
    case Constant.Bits(typeId, lanes)                        => VMValue.Bits(typeId, Vec.toArray(lanes))
    case Constant.NullPointer(typeId, pointee, storageClass) =>
      VMValue.Address(typeId, Pointer(0L, pointee, storageClass))
    // Global addresses are only known per dispatch, which replaces these placeholders.
    case Constant.GlobalAddress(typeId, _, _, _) => VMValue.Address(typeId, Pointer(0L, typeId, 0))
    case Constant.Composite(typeId, lanes, _)    => VMValue.Bits(typeId, Vec.toArray(lanes))
    case Constant.Sampler(typeId, _)             => VMValue.Address(typeId, Pointer(0L, typeId, 0))
  }
}

private enum VMValue {
  case Bits(typeId: Int, lanes: Array[Long])
  case Address(typeId: Int, pointer: Pointer)
}

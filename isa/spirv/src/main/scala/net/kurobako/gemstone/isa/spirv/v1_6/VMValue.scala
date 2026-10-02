package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
import VMMemory.*
import net.kurobako.gemstone.collection.*

private object VMValue {
  def fromConstant(constant: Constant): VMValue = constant match {
    case Constant.Bits(typeId, lanes)                        => VMValue.Bits(typeId, Vec.toArray(lanes))
    case Constant.NullPointer(typeId, pointee, storageClass) =>
      VMValue.Address(typeId, Pointer(Region.Null, 0L, pointee, storageClass))
  }
}

private enum VMValue {
  case Bits(typeId: Int, lanes: Array[Long])
  case Address(typeId: Int, pointer: Pointer)
}

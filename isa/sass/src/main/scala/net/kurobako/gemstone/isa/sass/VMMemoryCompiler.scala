package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*

/** Select scalar or vector memory instructions after validating the SASS register group. */
private object VMMemoryCompiler {
  private type Width = Int where (Between[8, 64] & MultipleOf[8])

  def load(
      destinations: Vec[Value.Register],
      address: Value,
      width: Width,
      signed: Boolean,
      access: MemoryAccess
  ): Instruction = destinations.size match {
    case 1     => Load(destinations.head.name, address, width, signed, access)
    case 2 | 4 => LoadVector(destinations.map(_.name), address, width, signed, access)
    case size  => throw IllegalArgumentException(s"load destination count is unsupported: $size")
  }

  def store(address: Value, values: Vec[Value], width: Width, access: MemoryAccess): Instruction = values.size match {
    case 1     => Store(address, values.head, width, access)
    case 2 | 4 => StoreVector(address, values, width, access)
    case size  => throw IllegalArgumentException(s"store value count is unsupported: $size")
  }

}

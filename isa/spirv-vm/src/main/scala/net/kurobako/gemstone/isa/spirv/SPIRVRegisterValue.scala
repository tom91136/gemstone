package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

object SPIRVRegisterValue {

  enum Payload derives CanEqual {
    case Bits(lanes: Vec[Long])
    case Address(address: Long)
  }
}

/** A typed SPIR-V result value. `typeId` is the id of the declared result type, so a consumer can tell a 32-bit integer
  * from a pointer without consulting the instruction that produced it.
  */
case class SPIRVRegisterValue(typeId: Int, payload: SPIRVRegisterValue.Payload) derives CanEqual

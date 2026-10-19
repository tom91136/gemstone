package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object AddressMode {
  private val known                            = Vec(`64`, U32, X4, X8, X16)
  def fromSpelling(value: String): AddressMode = known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))
  def scale(value: AddressMode): Option[Int in (1, 2, 4, 8, 16)] = value match {
    case X4            => Some(4)
    case X8            => Some(8)
    case X16           => Some(16)
    case `64` | U32    => Some(1)
    case Unknown("X1") => Some(1)
    case Unknown("X2") => Some(2)
    case Unknown(_)    => None
  }
}

enum AddressMode(val spellingValue: NonEmpty[String]) derives CanEqual {
  inline def spelling: String = spellingValue
  case `64`                                      extends AddressMode("64")
  case U32                                       extends AddressMode("U32")
  case X4                                        extends AddressMode("X4")
  case X8                                        extends AddressMode("X8")
  case X16                                       extends AddressMode("X16")
  case Unknown(vendorSpelling: NonEmpty[String]) extends AddressMode(vendorSpelling)
}

package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object FloatingConstant {
  private val known = Vec(INF, QNAN, SNAN, NAN, `+INF`, `-INF`, `+QNAN`, `-QNAN`, `+SNAN`, `-SNAN`, `-NAN`)
  def fromSpelling(value: String): FloatingConstant =
    known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))
  def isNaN(value: FloatingConstant): Boolean = value match {
    case QNAN | SNAN | NAN | `+QNAN` | `-QNAN` | `+SNAN` | `-SNAN` | `-NAN` => true
    case _                                                                  => false
  }
}

enum FloatingConstant(val spellingValue: NonEmpty[String]) {
  inline def spelling: String = spellingValue
  def isNaN: Boolean          = FloatingConstant.isNaN(this)
  case INF                                       extends FloatingConstant("INF")
  case QNAN                                      extends FloatingConstant("QNAN")
  case SNAN                                      extends FloatingConstant("SNAN")
  case NAN                                       extends FloatingConstant("NAN")
  case `+INF`                                    extends FloatingConstant("+INF")
  case `-INF`                                    extends FloatingConstant("-INF")
  case `+QNAN`                                   extends FloatingConstant("+QNAN")
  case `-QNAN`                                   extends FloatingConstant("-QNAN")
  case `+SNAN`                                   extends FloatingConstant("+SNAN")
  case `-SNAN`                                   extends FloatingConstant("-SNAN")
  case `-NAN`                                    extends FloatingConstant("-NAN")
  case Unknown(vendorSpelling: NonEmpty[String]) extends FloatingConstant(vendorSpelling)
}

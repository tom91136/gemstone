package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object FloatingConstant {
  private val known                                 = Vec(INF, QNAN, SNAN, NAN)
  def fromSpelling(value: String): FloatingConstant =
    known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))
}

/** Symbolic floating immediates; a sign is a `Unary` negation of the operand. */
enum FloatingConstant(val spellingValue: NonEmpty[String]) {
  inline def spelling: String = spellingValue
  def isNaN: Boolean          = this == QNAN || this == SNAN || this == NAN
  case INF                                       extends FloatingConstant("INF")
  case QNAN                                      extends FloatingConstant("QNAN")
  case SNAN                                      extends FloatingConstant("SNAN")
  case NAN                                       extends FloatingConstant("NAN")
  case Unknown(vendorSpelling: NonEmpty[String]) extends FloatingConstant(vendorSpelling)
}

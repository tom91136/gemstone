package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object OperandDecoration {
  private val known                                  = Vec(Not, Negate, Invert, Absolute)
  def fromSpelling(value: String): OperandDecoration =
    known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))
}

enum OperandDecoration(val spellingValue: NonEmpty[String]) {
  inline def spelling: String = spellingValue
  case Not                                       extends OperandDecoration("not")
  case Negate                                    extends OperandDecoration("negate")
  case Invert                                    extends OperandDecoration("invert")
  case Absolute                                  extends OperandDecoration("absolute")
  case Unknown(vendorSpelling: NonEmpty[String]) extends OperandDecoration(vendorSpelling)
}

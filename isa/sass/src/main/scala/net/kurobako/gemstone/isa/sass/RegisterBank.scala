package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object RegisterBank {
  private val known                              = Vec(R, UR, B, SB)
  def fromSpelling(value: String): RegisterBank  = known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))
  def capacity(value: RegisterBank): Option[Int] = value match {
    case R          => Some(256)
    case UR         => Some(64)
    case B          => Some(16)
    case SB         => Some(6)
    case _: Unknown => None
  }
}

enum RegisterBank(val spellingValue: NonEmpty[String]) {
  inline def spelling: String = spellingValue
  case R                                         extends RegisterBank("R")
  case UR                                        extends RegisterBank("UR")
  case B                                         extends RegisterBank("B")
  case SB                                        extends RegisterBank("SB")
  case Unknown(vendorSpelling: NonEmpty[String]) extends RegisterBank(vendorSpelling)
}

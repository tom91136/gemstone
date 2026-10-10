package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.constraint.*

/** Closed semantic vocabularies for the portions of PTX operands that have known meaning. */
object OperandDomains {
  object RegisterPart {
    def fromSpelling(value: NonEmpty[String]): RegisterPart = value.raw match {
      case "lo" => RegisterPart.Lo
      case "hi" => RegisterPart.Hi
      case "b0" => RegisterPart.B0
      case "b1" => RegisterPart.B1
      case "b2" => RegisterPart.B2
      case "b3" => RegisterPart.B3
      case _    => RegisterPart.Unknown(value)
    }
  }

  enum RegisterPart(val spelling: NonEmpty[String]) {
    case Lo                               extends RegisterPart("lo")
    case Hi                               extends RegisterPart("hi")
    case B0                               extends RegisterPart("b0")
    case B1                               extends RegisterPart("b1")
    case B2                               extends RegisterPart("b2")
    case B3                               extends RegisterPart("b3")
    case Unknown(value: NonEmpty[String]) extends RegisterPart(value)
  }

  object QualifiedOperand {
    def fromSpelling(value: NonEmpty[String]): QualifiedOperand = value.raw match {
      case "x" => QualifiedOperand.X
      case "y" => QualifiedOperand.Y
      case "z" => QualifiedOperand.Z
      case "w" => QualifiedOperand.W
      case _   => QualifiedOperand.Unknown(value)
    }
  }

  enum QualifiedOperand(val spelling: NonEmpty[String]) {
    case X                                extends QualifiedOperand("x")
    case Y                                extends QualifiedOperand("y")
    case Z                                extends QualifiedOperand("z")
    case W                                extends QualifiedOperand("w")
    case Unknown(value: NonEmpty[String]) extends QualifiedOperand(value)
  }
}

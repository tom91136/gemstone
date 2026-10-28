package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Closed semantic vocabularies for the portions of PTX operands that have known meaning. */
object OperandDomains {
  object RegisterPart {
    def fromSpelling(value: NonEmpty[String]): RegisterPart = value.raw match {
      case "lo"  => RegisterPart.Lo
      case "hi"  => RegisterPart.Hi
      case "b0"  => RegisterPart.B0
      case "b1"  => RegisterPart.B1
      case "b2"  => RegisterPart.B2
      case "b3"  => RegisterPart.B3
      case other =>
        elements(other).fold(RegisterPart.Unknown(value))(RegisterPart.Elements(_, _))
    }

    /** The elements of the register pair a packed video source names: `h01` the 16-bit elements 0 and 1, `b3210` the
      * 8-bit elements 3, 2, 1 and 0. The indices are the ones the selector writes, most significant first.
      */
    def elements(value: String): Option[(Vec[Int], Int in (8, 16))] =
      if (value.length == 3 && value.head == 'h' && value.tail.forall(_.isDigit))
        elementsOf(value, count = 4, bits = 16)
      else if (value.length == 5 && value.head == 'b' && value.tail.forall(_.isDigit))
        elementsOf(value, count = 8, bits = 8)
      else None

    /** The elements the selector names, when each of them is below `count` and the pair holds them in `bits` bits. */
    private def elementsOf(value: String, count: Int, bits: Int in (8, 16)): Option[(Vec[Int], Int in (8, 16))] = {
      val indices = value.tail.toVector.map(_.asDigit)
      if (indices.forall(_ < count)) Some((Vec.from(indices), bits)) else None
    }

    /** The text an element selector writes. */
    def spelling(indices: Vec[Int], bits: Int): NonEmpty[String] =
      ((if (bits == 16) "h" else "b") + indices.map(_.toString).mkString).unsafe[NonEmpty[String]]
  }

  enum RegisterPart(val spelling: NonEmpty[String]) {
    case Lo extends RegisterPart("lo")
    case Hi extends RegisterPart("hi")
    case B0 extends RegisterPart("b0")
    case B1 extends RegisterPart("b1")
    case B2 extends RegisterPart("b2")
    case B3 extends RegisterPart("b3")

    /** The elements of the register pair a packed video source names, in the order the selector writes them. */
    case Elements(indices: Vec[Int], bits: Int in (8, 16)) extends RegisterPart(RegisterPart.spelling(indices, bits))
    case Unknown(value: NonEmpty[String])                  extends RegisterPart(value)
  }

  object QualifiedOperand {
    private val bySpelling: Map[String, QualifiedOperand] =
      Vector(X, Y, Z, W).view.map(operand => operand.spelling.raw -> operand).toMap

    def fromSpelling(value: NonEmpty[String]): QualifiedOperand = bySpelling.getOrElse(value.raw, Unknown(value))
  }

  enum QualifiedOperand(val spelling: NonEmpty[String]) {
    case X                                extends QualifiedOperand("x")
    case Y                                extends QualifiedOperand("y")
    case Z                                extends QualifiedOperand("z")
    case W                                extends QualifiedOperand("w")
    case Unknown(value: NonEmpty[String]) extends QualifiedOperand(value)
  }
}

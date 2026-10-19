package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

object OperandSelector {
  private val known = Vec(
    CC,
    reuse,
    H0,
    H1,
    H0_H0,
    H0_H1,
    H1_H0,
    H1_H1,
    H0_NH1,
    EQU,
    NEU,
    LTU,
    LEU,
    GTU,
    GEU,
    F32,
    F32x2,
    HI_LO,
    LO_HI,
    B0,
    B1,
    B2,
    B3,
    ROW,
    COL,
    X4,
    X8,
    SIGN,
    negA,
    negB,
    tnspA,
    tnspB
  )
  def fromSpelling(value: String): OperandSelector = known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))

  def fromSpellings(values: Vec[String]): Vec[OperandSelector] = {
    val result = mutable.ArrayBuffer.empty[OperandSelector]
    values.flatMap(value => Vec.from(value.split('.'))).map(fromSpelling(_)).foreach {
      case HI_LO if result.lastOption.contains(F32x2) => ()
      case selector                                   => result.append(selector)
    }
    Vec.from(result)
  }
}

enum OperandSelector(val spellingValue: NonEmpty[String]) derives CanEqual {
  inline def spelling: String = spellingValue
  case CC    extends OperandSelector("CC")
  case reuse extends OperandSelector("reuse")
  case H0    extends OperandSelector("H0")
  case H1    extends OperandSelector("H1")
  case H0_H0 extends OperandSelector("H0_H0")
  case H0_H1 extends OperandSelector("H0_H1")
  case H1_H0 extends OperandSelector("H1_H0")
  case H1_H1 extends OperandSelector("H1_H1")

  /** The low half above the negated high half. */
  case H0_NH1 extends OperandSelector("H0_NH1")
  case EQU    extends OperandSelector("EQU")
  case NEU    extends OperandSelector("NEU")
  case LTU    extends OperandSelector("LTU")
  case LEU    extends OperandSelector("LEU")
  case GTU    extends OperandSelector("GTU")
  case GEU    extends OperandSelector("GEU")
  case F32    extends OperandSelector("F32")
  case F32x2  extends OperandSelector("F32x2")
  case HI_LO  extends OperandSelector("HI_LO")
  case LO_HI  extends OperandSelector("LO_HI")
  case B0     extends OperandSelector("B0")
  case B1     extends OperandSelector("B1")
  case B2     extends OperandSelector("B2")
  case B3     extends OperandSelector("B3")
  case ROW    extends OperandSelector("ROW")
  case COL    extends OperandSelector("COL")
  case X4     extends OperandSelector("X4")
  case X8     extends OperandSelector("X8")

  /** A register or constant read as a predicate of its sign bit. */
  case SIGN extends OperandSelector("SIGN")

  /** A GMMA's descriptors with A or B negated, or MN-major rather than K-major. */
  case negA                                      extends OperandSelector("negA")
  case negB                                      extends OperandSelector("negB")
  case tnspA                                     extends OperandSelector("tnspA")
  case tnspB                                     extends OperandSelector("tnspB")
  case Unknown(vendorSpelling: NonEmpty[String]) extends OperandSelector(vendorSpelling)
}

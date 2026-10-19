package net.kurobako.gemstone.isa.catalogue

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import upickle.default.ReadWriter
import upickle.default.readwriter

object FloatEncoding {
  private val known = Vec(F16Imm, F32Imm, F64Imm, E8M7Imm, E6M9Imm, TF32Imm, BF16Imm)

  def fromSpelling(value: String): FloatEncoding = known.find(_.spelling == value).getOrElse(
    Unknown(value.refine[NonEmpty[String]].fold(error => throw IllegalArgumentException(error.raw), identity))
  )
  given ReadWriter[FloatEncoding] = readwriter[String].bimap(_.spelling, fromSpelling(_))
}

enum FloatEncoding(val spellingValue: NonEmpty[String], val width: Option[Int in (16, 32, 64)]) derives CanEqual {
  inline def spelling: String = spellingValue
  case F16Imm                                    extends FloatEncoding("F16Imm", Some(16))
  case F32Imm                                    extends FloatEncoding("F32Imm", Some(32))
  case F64Imm                                    extends FloatEncoding("F64Imm", Some(64))
  case E8M7Imm                                   extends FloatEncoding("E8M7Imm", Some(32))
  case E6M9Imm                                   extends FloatEncoding("E6M9Imm", Some(16))
  case TF32Imm                                   extends FloatEncoding("TF32Imm", Some(32))
  case BF16Imm                                   extends FloatEncoding("BF16Imm", Some(16))
  case Unknown(vendorSpelling: NonEmpty[String]) extends FloatEncoding(vendorSpelling, None)
}

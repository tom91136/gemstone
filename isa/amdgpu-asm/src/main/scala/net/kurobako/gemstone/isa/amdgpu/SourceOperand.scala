package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import net.kurobako.gemstone.constraint.*

object SourceOperand {

  /** The hardware's binary64 1/(2*pi); the narrower widths round it to their own nearest value. */
  private val InverseTwoPi = java.lang.Double.longBitsToDouble(0x3fc45f306dc9c882L)

  private def encoding(isa: ISA): OperandEncoding =
    OperandEncoding.Catalogue.forKind(isa, OperandKind.SRC)
      .getOrElse(throw IllegalStateException(s"missing generated source selectors for $isa"))

  def literalSize(code: Int, isa: ISA): Option[Int in (4, 8)] = encoding(isa).literals.get(code)

  def decode(code: Int, isa: ISA): Option[SourceOperand] = {
    val table = encoding(isa)
    if (code >= Source.SGPR_MIN && code < Source.INLINE_INTEGER_C_MIN)
      code.refine[Int in 0 ~ 127].toOption.map(Scalar(_))
    else if (code >= Source.VGPR_MIN && code <= Source.VGPR_MAX)
      (code - Source.VGPR_MIN).refine[Int in 0 ~ 255].toOption.map(VectorRegister(_))
    else table.integers.get(code).map(Integer(_))
      .orElse(table.floating.get(code).map(value =>
        Floating(if (code == Source.INLINE_FLOATING_C_MAX) InverseTwoPi else value)
      ))
      .orElse(table.special.get(code).map(Special(_)))
      .orElse(table.literals.get(code).map(Literal(_)))
  }
}

enum SourceOperand {
  case Scalar(index: Int in 0 ~ 127)
  case VectorRegister(index: Int in 0 ~ 255)
  case Integer(value: Long)
  case Floating(value: Double)
  case Special(value: SpecialSource)
  case Literal(bytes: Int in (4, 8))
}

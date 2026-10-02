package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*

object SourceOperand {
  private def encoding(isa: ISA): OperandEncoding =
    OperandEncoding.Catalogue.forKind(isa, OperandKind.SRC)
      .getOrElse(throw IllegalStateException(s"missing generated source selectors for $isa"))

  def literalSize(code: Int, isa: ISA): Int = encoding(isa).literals.getOrElse(code, 0)

  def decode(code: Int, isa: ISA): Option[SourceOperand] = {
    val table = encoding(isa)
    if (code >= Source.SGPR_MIN && code < Source.INLINE_INTEGER_C_MIN) Some(Scalar(code))
    else if (code >= Source.VGPR_MIN && code <= Source.VGPR_MAX) Some(VectorRegister(code - Source.VGPR_MIN))
    else table.integers.get(code).map(Integer(_))
      .orElse(table.floating.get(code).map(Floating(_)))
      .orElse(table.special.get(code).map(Special(_)))
      .orElse(table.literals.get(code).map(Literal(_)))
  }
}

enum SourceOperand {
  case Scalar(index: Int)
  case VectorRegister(index: Int)
  case Integer(value: Long)
  case Floating(value: Double)
  case Special(value: SpecialSource)
  case Literal(bytes: Int)
}

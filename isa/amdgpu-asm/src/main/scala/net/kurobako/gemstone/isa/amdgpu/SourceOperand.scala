package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*
import net.kurobako.gemstone.isa.amdgpu.domain.*

object SourceOperand {

  /** The hardware's binary64 1/(2*pi); the narrower widths round it to their own nearest value. */
  val InverseTwoPi: Double = java.lang.Double.longBitsToDouble(0x3fc45f306dc9c882L)

  def literalSize(code: Int, isa: ISA): Option[Int in (4, 8)] = Src.decode(isa, code).collect {
    case Src.Literal   => 4.unsafe[Int in (4, 8)]
    case Src.Literal64 => 8.unsafe[Int in (4, 8)]
  }

  def decode(code: Int, isa: ISA): Option[SourceOperand] =
    if (code >= Source.SGPR_MIN && code < Source.INLINE_INTEGER_C_MIN)
      code.refine[Int in 0 ~ 127].toOption.map(Scalar(_))
    else if (code >= Source.VGPR_MIN && code <= Source.VGPR_MAX)
      (code - Source.VGPR_MIN).refine[Int in 0 ~ 255].toOption.map(VectorRegister(_))
    else Src.decode(isa, code).flatMap {
      case Src.Integer(value)  => Some(Integer(value.raw))
      case Src.Floating(value) => Some(Floating(if (code == Source.INLINE_FLOATING_C_MAX) InverseTwoPi else value))
      case Src.Literal         => Some(Literal(4.unsafe[Int in (4, 8)]))
      case Src.Literal64       => Some(Literal(8.unsafe[Int in (4, 8)]))
      case other               => Some(other.slot).collect { case Slot.Named(name) => name }
          .flatMap(SpecialSource.fromName).map(Special(_))
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

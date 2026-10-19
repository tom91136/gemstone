package net.kurobako.gemstone.isa.spirv

import FloatingSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** Cooperative matrix multiply-accumulate, whose internal precision SPV_KHR_cooperative_matrix leaves to the client
  * API: floating-point products are exact in binary64 and sum there, and the third operand joins the sum before one
  * rounding to the result; integer products extend each operand by its signedness and sum in 64 bits, then add the
  * third operand at the result width, saturating where asked.
  */
object CooperativeMatrixSemantics {

  /** How the components of an operand read: IEEE floats, Intel's bfloat16 and TF32 views of integer and binary32
    * components, or integers of a width and signedness.
    */
  enum Element derives CanEqual {
    case Half, Single, Double, BFloat16, TensorFloat32
    case Signed(width: Int in 1 ~ 64)
    case Unsigned(width: Int in 1 ~ 64)

    def floating: Boolean = this match {
      case Signed(_) | Unsigned(_) => false
      case _                       => true
    }
  }

  /** The Cooperative Matrix Operands bits that give an operand's signedness and Intel's bfloat16 and TF32 readings; a
    * zero bit is never set.
    */
  case class Flags(signed: Int, bfloat16: Int, tf32: Int)

  /** The element of components of `typeId`, as the Cooperative Matrix Operands `mask` reads it. */
  def element[V, R](sinks: SPIRVSinks[V, R], typeId: Int, mask: Int, flags: Flags): Element =
    if (sinks.floating(typeId)) sinks.floatingWidth(typeId).raw match {
      case 16                             => Element.Half
      case 32 if (mask & flags.tf32) != 0 => Element.TensorFloat32
      case 32                             => Element.Single
      case _                              => Element.Double
    }
    else if ((mask & flags.bfloat16) != 0) Element.BFloat16
    else if ((mask & flags.signed) != 0) Element.Signed(sinks.width(typeId))
    else Element.Unsigned(sinks.width(typeId))

  inline def mulAdd[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      a: Element,
      b: Element,
      c: Element,
      result: Element,
      saturating: Boolean
  )(ids: OperandIds): R =
    if (result.floating)
      sinks.matrixAccumulate(ids)(
        algebra.literal(0L),
        (sum, x, y) =>
          algebra.primitive(
            sum,
            algebra.primitive(widen(algebra, x, a), widen(algebra, y, b))(multiply(_: Float64, _: Float64))
          )(
            add(_: Float64, _: Float64)
          ),
        (sum, z) => narrow(algebra, algebra.primitive(widen(algebra, z, c), sum)(add(_: Float64, _: Float64)), result)
      )
    else {
      val (width, signed) = result match {
        case Element.Signed(bits)   => (bits, true)
        case Element.Unsigned(bits) => (bits, false)
        case _                      => (64: Int in 1 ~ 64, false)
      }
      if (saturating)
        sinks.matrixAccumulate(ids)(
          algebra.literal(0L),
          (sum, x, y) => algebra.add(sum, algebra.multiply(extend(algebra, x, a), extend(algebra, y, b))),
          (sum, z) =>
            algebra.truncate(algebra.addSaturated(sum, extend(algebra, z, c), width, signed), width)
        )
      else
        sinks.matrixAccumulate(ids)(
          algebra.literal(0L),
          (sum, x, y) => algebra.add(sum, algebra.multiply(extend(algebra, x, a), extend(algebra, y, b))),
          (sum, z) => algebra.truncate(algebra.add(sum, extend(algebra, z, c)), width)
        )
    }

  /** A floating-point component as binary64, exactly. */
  inline def widen[V](inline algebra: Algebra[V], value: V, element: Element): V = element match {
    case Element.Half          => algebra.primitive(value)(Elementary.widen16(_))
    case Element.Single        => algebra.primitive(value)(Elementary.widen32(_))
    case Element.BFloat16      => algebra.primitive(value)(widenBFloat16(_))
    case Element.TensorFloat32 => algebra.primitive(value)(widenTensorFloat32(_))
    case _                     => value
  }

  /** A binary64 value rounded to nearest even at the result's element. */
  inline def narrow[V](inline algebra: Algebra[V], value: V, element: Element): V = element match {
    case Element.Half     => algebra.primitive(value)(Elementary.narrow16(_))
    case Element.Single   => algebra.primitive(value)(Elementary.narrow32(_))
    case Element.BFloat16 => algebra.primitive(value)(narrowBFloat16(_))
    case _                => value
  }

  /** An integer component extended to 64 bits by its signedness. */
  inline def extend[V](inline algebra: Algebra[V], value: V, element: Element): V = element match {
    case Element.Signed(width)   => algebra.extend(value, width, signed = true)
    case Element.Unsigned(width) => algebra.extend(value, width, signed = false)
    case _                       => value
  }

  def widenBFloat16(x: UInt[16]): Float64 = Float64(Minifloat.BFloat16.toDouble(x.raw))

  def narrowBFloat16(x: Float64): UInt[16] = UInt[16](Minifloat.BFloat16.fromDouble(x.toDouble))

  /** A binary32 component read as TF32, as the matrix engines read it: the low 13 fraction bits are dropped. */
  def widenTensorFloat32(x: Float32): Float64 = Float64(FloatingPoint[32](x.bits.raw & ~0x1fffL).toDouble)
}

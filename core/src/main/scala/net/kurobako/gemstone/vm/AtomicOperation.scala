package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Read-modify-write operations shared by the ISA atomics. */
enum AtomicOperation {
  case Add, Subtract, Exchange
  case MinimumSigned, MaximumSigned, MinimumUnsigned, MaximumUnsigned
  case And, Or, Xor

  /** Wrapping increment and decrement by one; the operand is ignored. */
  case Increment, Decrement

  /** `old >= operand ? 0 : old + 1`, comparing unsigned. */
  case IncrementWrap

  /** `old == 0 || old > operand ? operand : old - 1`, comparing unsigned. */
  case DecrementWrap

  /** Binary16, binary32 or binary64 addition; minimum and maximum ignore a NaN operand and order signed zeros. */
  case FloatAdd, FloatMinimum, FloatMaximum

  /** The value stored back for `old` and `operand`, as the low `width` bits. */
  def apply(old: Long, operand: Long, width: ScalarBitWidth): Long = {
    val a      = Bits.truncate(old, width)
    val b      = Bits.truncate(operand, width)
    val result = this match {
      case Add             => a + b
      case Subtract        => a - b
      case Exchange        => b
      case MinimumSigned   => math.min(Bits.signExtend(a, width), Bits.signExtend(b, width))
      case MaximumSigned   => math.max(Bits.signExtend(a, width), Bits.signExtend(b, width))
      case MinimumUnsigned => if (java.lang.Long.compareUnsigned(a, b) <= 0) a else b
      case MaximumUnsigned => if (java.lang.Long.compareUnsigned(a, b) >= 0) a else b
      case And             => a & b
      case Or              => a | b
      case Xor             => a ^ b
      case Increment       => a + 1L
      case Decrement       => a - 1L
      case IncrementWrap   => if (java.lang.Long.compareUnsigned(a, b) >= 0) 0L else a + 1L
      case DecrementWrap   => if (a == 0L || java.lang.Long.compareUnsigned(a, b) > 0) b else a - 1L
      case FloatAdd        => IEEE754.add(a, b, floating(width))
      case FloatMinimum    => IEEE754.minNum(a, b, floating(width))
      case FloatMaximum    => IEEE754.maxNum(a, b, floating(width))
    }
    Bits.truncate(result, width)
  }

  private def floating(width: ScalarBitWidth): Int in (16, 32, 64) = width.raw match {
    case 16    => 16
    case 32    => 32
    case 64    => 64
    case other => throw IllegalArgumentException(s"$this has no $other-bit floating-point format")
  }
}

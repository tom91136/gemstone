package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

object AtomicOperation {

  extension [V](inline algebra: Algebra[V]) {

    /** `function` of the low `bits` of the old value and the operand. */
    private inline def combine[A, R](bits: Int in 1 ~ 64, old: V, operand: V)(inline function: (A, A) => R): V =
      algebra.primitive(algebra.truncate(old, bits), algebra.truncate(operand, bits))(function)

    inline def incrementWrap(bits: Int in 1 ~ 64, old: V, operand: V): V =
      algebra.let("a", algebra.truncate(old, bits))(a =>
        algebra.select(
          algebra.lessOrEqual(algebra.truncate(operand, bits), a, signed = false),
          algebra.literal(0L),
          algebra.add(a, algebra.literal(1L))
        )
      )

    inline def decrementWrap(bits: Int in 1 ~ 64, old: V, operand: V): V =
      algebra.let("a", algebra.truncate(old, bits))(a =>
        algebra.let("b", algebra.truncate(operand, bits))(b =>
          algebra.select(
            algebra.or(algebra.equal(a, algebra.literal(0L)), algebra.lessThan(b, a, signed = false)),
            b,
            algebra.subtract(a, algebra.literal(1L))
          )
        )
      )

    inline def barrierArrive(old: V): V =
      algebra.let("low", algebra.truncate(old, 32))(low =>
        algebra.let(
          "high",
          algebra.truncate(algebra.add(algebra.shiftRightLogical(old, algebra.literal(32L)), algebra.literal(1L)), 32)
        )(high =>
          algebra.or(
            algebra.shiftLeft(
              algebra.truncate(
                algebra.select(
                  algebra.equal(algebra.and(high, algebra.literal(0x7fffffffL)), algebra.literal(0L)),
                  algebra.add(algebra.add(high, low), algebra.literal(0x40000000L)),
                  high
                ),
                32
              ),
              algebra.literal(32L)
            ),
            low
          )
        )
      )

    /** The value a compare-exchange stores over `old`: `value` where the low `bits` of `old` and `comparator` are
      * equal, and `old` otherwise.
      */
    inline def exchanged(bits: Int in 1 ~ 64, old: V, comparator: V, value: V): V =
      algebra.select(algebra.equal(algebra.truncate(old, bits), algebra.truncate(comparator, bits)), value, old)
  }

  private def floating(operation: AtomicOperation, width: ScalarBitWidth): Int in (16, 32, 64) = width.raw match {
    case 16    => 16
    case 32    => 32
    case 64    => 64
    case other => throw IllegalArgumentException(s"$operation has no $other-bit floating-point format")
  }
}

/** Read-modify-write operations shared by the ISA atomics. */
enum AtomicOperation derives CanEqual {
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

  /** Bfloat16 addition, rounding to nearest even without flushing subnormals. */
  case BFloat16Add

  /** Addition of each 16-bit half of a 32-bit word as binary16 or as bfloat16. */
  case PackedHalfAdd, PackedBFloat16Add

  /** Bfloat16 minimum and maximum, and those of each 16-bit half as binary16 or bfloat16, as [[FloatMinimum]] orders.
    */
  case BFloat16Minimum, BFloat16Maximum
  case PackedHalfMinimum, PackedHalfMaximum, PackedBFloat16Minimum, PackedBFloat16Maximum

  /** One arrival on an NVIDIA sm_80 to sm_89 mbarrier: the high word counts arrivals up to its phase bit 31 and, once
    * its low 31 bits wrap to zero, re-arms by adding the low word, `2^30` less the expected arrivals, plus `2^30`.
    */
  case BarrierArrive

  /** The value this operation stores over the old value for an operand, as the low `width` bits, from a kernel over the
    * old value and the operand that `sink` consumes. The operation is matched once, so each case expands into its own
    * kernel.
    */
  inline def staged[V, R](inline algebra: Algebra[V], width: ScalarBitWidth)(
      inline sink: Sink.Binary[V, R]
  ): R = {
    import AtomicOperation.*
    val bits = width.widen[Int in 1 ~ 64]
    this match {
      case Add           => sink((old, operand) => algebra.truncate(algebra.add(old, operand), bits))
      case Subtract      => sink((old, operand) => algebra.truncate(algebra.subtract(old, operand), bits))
      case Exchange      => sink((_, operand) => algebra.truncate(operand, bits))
      case MinimumSigned => sink((old, operand) =>
          algebra.truncate(
            algebra.minimum(algebra.signExtend(old, bits), algebra.signExtend(operand, bits), signed = true),
            bits
          )
        )
      case MaximumSigned => sink((old, operand) =>
          algebra.truncate(
            algebra.maximum(algebra.signExtend(old, bits), algebra.signExtend(operand, bits), signed = true),
            bits
          )
        )
      case MinimumUnsigned => sink((old, operand) =>
          algebra.minimum(algebra.truncate(old, bits), algebra.truncate(operand, bits), signed = false)
        )
      case MaximumUnsigned => sink((old, operand) =>
          algebra.maximum(algebra.truncate(old, bits), algebra.truncate(operand, bits), signed = false)
        )
      case And           => sink((old, operand) => algebra.truncate(algebra.and(old, operand), bits))
      case Or            => sink((old, operand) => algebra.truncate(algebra.or(old, operand), bits))
      case Xor           => sink((old, operand) => algebra.truncate(algebra.xor(old, operand), bits))
      case Increment     => sink((old, _) => algebra.truncate(algebra.add(old, algebra.literal(1L)), bits))
      case Decrement     => sink((old, _) => algebra.truncate(algebra.subtract(old, algebra.literal(1L)), bits))
      case IncrementWrap => sink((old, operand) => algebra.incrementWrap(bits, old, operand))
      case DecrementWrap => sink((old, operand) => algebra.decrementWrap(bits, old, operand))
      case FloatAdd      =>
        floating(this, width).raw match {
          case 16 => sink((old, operand) => algebra.combine(bits, old, operand)(Float16.add(_, _)))
          case 32 => sink((old, operand) => algebra.combine(bits, old, operand)(Float32.add(_, _)))
          case _  => sink((old, operand) => algebra.combine(bits, old, operand)(Float64.add(_, _)))
        }
      case FloatMinimum =>
        floating(this, width).raw match {
          case 16 => sink((old, operand) => algebra.combine(bits, old, operand)(Float16.minNum(_, _)))
          case 32 => sink((old, operand) => algebra.combine(bits, old, operand)(Float32.minNum(_, _)))
          case _  => sink((old, operand) => algebra.combine(bits, old, operand)(Float64.minNum(_, _)))
        }
      case FloatMaximum =>
        floating(this, width).raw match {
          case 16 => sink((old, operand) => algebra.combine(bits, old, operand)(Float16.maxNum(_, _)))
          case 32 => sink((old, operand) => algebra.combine(bits, old, operand)(Float32.maxNum(_, _)))
          case _  => sink((old, operand) => algebra.combine(bits, old, operand)(Float64.maxNum(_, _)))
        }
      case BFloat16Add   => sink((old, operand) => algebra.combine(16, old, operand)(AtomicFloats.addBFloat16(_, _)))
      case PackedHalfAdd =>
        sink((old, operand) => algebra.combine(32, old, operand)(AtomicFloats.addPackedHalf(_, _)))
      case PackedBFloat16Add =>
        sink((old, operand) => algebra.combine(32, old, operand)(AtomicFloats.addPackedBFloat16(_, _)))
      case BFloat16Minimum =>
        sink((old, operand) => algebra.combine(16, old, operand)(AtomicFloats.minBFloat16(_, _)))
      case BFloat16Maximum =>
        sink((old, operand) => algebra.combine(16, old, operand)(AtomicFloats.maxBFloat16(_, _)))
      case PackedHalfMinimum =>
        sink((old, operand) => algebra.combine(32, old, operand)(AtomicFloats.minPackedHalf(_, _)))
      case PackedHalfMaximum =>
        sink((old, operand) => algebra.combine(32, old, operand)(AtomicFloats.maxPackedHalf(_, _)))
      case PackedBFloat16Minimum =>
        sink((old, operand) => algebra.combine(32, old, operand)(AtomicFloats.minPackedBFloat16(_, _)))
      case PackedBFloat16Maximum =>
        sink((old, operand) => algebra.combine(32, old, operand)(AtomicFloats.maxPackedBFloat16(_, _)))
      case BarrierArrive => sink((old, _) => algebra.barrierArrive(old))
    }
  }

  /** The value stored back for `old` and `operand`, as the low `width` bits. */
  def apply(old: Long, operand: Long, width: ScalarBitWidth): Long = {
    import AtomicOperation.*
    val bits = width.widen[Int in 1 ~ 64]
    this match {
      case Add           => Execute.truncate(Execute.add(old, operand), bits)
      case Subtract      => Execute.truncate(Execute.subtract(old, operand), bits)
      case Exchange      => Execute.truncate(operand, bits)
      case MinimumSigned => Execute.truncate(
          Execute.minimum(Execute.signExtend(old, bits), Execute.signExtend(operand, bits), signed = true),
          bits
        )
      case MaximumSigned => Execute.truncate(
          Execute.maximum(Execute.signExtend(old, bits), Execute.signExtend(operand, bits), signed = true),
          bits
        )
      case MinimumUnsigned =>
        Execute.minimum(Execute.truncate(old, bits), Execute.truncate(operand, bits), signed = false)
      case MaximumUnsigned =>
        Execute.maximum(Execute.truncate(old, bits), Execute.truncate(operand, bits), signed = false)
      case And           => Execute.truncate(Execute.and(old, operand), bits)
      case Or            => Execute.truncate(Execute.or(old, operand), bits)
      case Xor           => Execute.truncate(Execute.xor(old, operand), bits)
      case Increment     => Execute.truncate(Execute.add(old, Execute.literal(1L)), bits)
      case Decrement     => Execute.truncate(Execute.subtract(old, Execute.literal(1L)), bits)
      case IncrementWrap => Execute.incrementWrap(bits, old, operand)
      case DecrementWrap => Execute.decrementWrap(bits, old, operand)
      case FloatAdd      =>
        floating(this, width).raw match {
          case 16 => Execute.combine(bits, old, operand)(Float16.add(_, _))
          case 32 => Execute.combine(bits, old, operand)(Float32.add(_, _))
          case _  => Execute.combine(bits, old, operand)(Float64.add(_, _))
        }
      case FloatMinimum =>
        floating(this, width).raw match {
          case 16 => Execute.combine(bits, old, operand)(Float16.minNum(_, _))
          case 32 => Execute.combine(bits, old, operand)(Float32.minNum(_, _))
          case _  => Execute.combine(bits, old, operand)(Float64.minNum(_, _))
        }
      case FloatMaximum =>
        floating(this, width).raw match {
          case 16 => Execute.combine(bits, old, operand)(Float16.maxNum(_, _))
          case 32 => Execute.combine(bits, old, operand)(Float32.maxNum(_, _))
          case _  => Execute.combine(bits, old, operand)(Float64.maxNum(_, _))
        }
      case BFloat16Add           => Execute.combine(16, old, operand)(AtomicFloats.addBFloat16(_, _))
      case PackedHalfAdd         => Execute.combine(32, old, operand)(AtomicFloats.addPackedHalf(_, _))
      case PackedBFloat16Add     => Execute.combine(32, old, operand)(AtomicFloats.addPackedBFloat16(_, _))
      case BFloat16Minimum       => Execute.combine(16, old, operand)(AtomicFloats.minBFloat16(_, _))
      case BFloat16Maximum       => Execute.combine(16, old, operand)(AtomicFloats.maxBFloat16(_, _))
      case PackedHalfMinimum     => Execute.combine(32, old, operand)(AtomicFloats.minPackedHalf(_, _))
      case PackedHalfMaximum     => Execute.combine(32, old, operand)(AtomicFloats.maxPackedHalf(_, _))
      case PackedBFloat16Minimum => Execute.combine(32, old, operand)(AtomicFloats.minPackedBFloat16(_, _))
      case PackedBFloat16Maximum => Execute.combine(32, old, operand)(AtomicFloats.maxPackedBFloat16(_, _))
      case BarrierArrive         => Execute.barrierArrive(old)
    }
  }
}

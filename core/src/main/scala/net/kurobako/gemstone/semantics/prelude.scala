package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.targetName
import scala.compiletime.constValue
import scala.compiletime.erasedValue

/** The types rendered semantics are written in, so a rendered definition runs as Scala. A `UInt[W]` holds a value in
  * `0 until 2^W` and an `SInt[W]` one in `-2^(W-1) until 2^(W-1)`, each as the `Long` execution computes with. `+`,
  * `-`, `*`, `<<`, `~` and unary `-` wrap at `W` bits, `Long` operands are literals of the same type, and `to`
  * truncates or extends the 64-bit value to another type. Floats are their IEEE bit patterns, so NaN payloads survive
  * as they do in execution. The arithmetic primitives of the three widths sit in the companions of `Float16`, `Float32`
  * and `Float64`, each rounding once; a sum or product propagates the left of two NaN operands. The integer types'
  * [[net.kurobako.gemstone.constraint.IntegerView]]s let them take refinements such as `UInt[5] in 0 ~ 17`.
  */
object prelude {
  object UInt {
    inline def apply[W <: Int](value: Long): UInt[W] = wrap(value, constValue[W])

    given view[W <: Int]: IntegerView[UInt[W], W, false] = value => value

    given [W <: Int]: CanEqual[UInt[W], UInt[W]] = CanEqual.derived
  }

  object SInt {
    inline def apply[W <: Int](value: Long): SInt[W] = extend(value, constValue[W])

    given view[W <: Int]: IntegerView[SInt[W], W, true] = value => value

    given [W <: Int]: CanEqual[SInt[W], SInt[W]] = CanEqual.derived
  }

  object FloatingPoint {

    /** The named type of a width that has one, so a constructed value of that width needs no conversion. */
    type Named[W <: Int] <: FloatingPoint[W] = W match {
      case 16 => Float16
      case 32 => Float32
      case 64 => Float64
      case _  => FloatingPoint[W]
    }

    inline def apply[W <: Int](bits: Long): Named[W] = inline erasedValue[W] match {
      case _: 16 => wrap(bits, 16)
      case _: 32 => wrap(bits, 32)
      case _: 64 => wrap(bits, 64)
      case _     => wrap(bits, constValue[W])
    }
  }

  object BFloat16 {
    inline def apply(bits: UInt[16]): BFloat16 = wrap(bits, 16)
  }

  object TensorFloat32 {
    inline def apply(bits: UInt[19]): TensorFloat32 = wrap(bits, 19)
  }

  object Float8E4M3 {
    inline def apply(bits: UInt[8]): Float8E4M3 = wrap(bits, 8)
  }

  object Float8E5M2 {
    inline def apply(bits: UInt[8]): Float8E5M2 = wrap(bits, 8)
  }

  object Float8E4M3FNUZ {
    inline def apply(bits: UInt[8]): Float8E4M3FNUZ = wrap(bits, 8)
  }

  object Float8E5M2FNUZ {
    inline def apply(bits: UInt[8]): Float8E5M2FNUZ = wrap(bits, 8)
  }

  object Float6E2M3 {
    inline def apply(bits: UInt[6]): Float6E2M3 = wrap(bits, 6)
  }

  object Float6E3M2 {
    inline def apply(bits: UInt[6]): Float6E3M2 = wrap(bits, 6)
  }

  object Float4E2M1 {
    inline def apply(bits: UInt[4]): Float4E2M1 = wrap(bits, 4)
  }

  object ScaleE8M0 {
    inline def apply(bits: UInt[8]): ScaleE8M0 = wrap(bits, 8)
  }

  object Float16 {
    inline def apply(value: Float): Float16  = IEEE754.floatToBinary16(value).toLong
    inline def apply(value: Double): Float16 = IEEE754.doubleToBinary16(value).toLong

    def add(left: Float16, right: Float16): Float16 =
      FloatingPoint[16](IEEE754.add(left, right, 16, IEEE754.NaNOperand.Left))
    def multiply(left: Float16, right: Float16): Float16 =
      FloatingPoint[16](IEEE754.multiply(left, right, 16, IEEE754.NaNOperand.Left))

    /** `left * right + addend` rounded once to the width. */
    def fma(left: Float16, right: Float16, addend: Float16): Float16 =
      FloatingPoint[16](IEEE754.fusedMultiplyAdd(left, right, addend, 16))

    /** IEEE 754-2008 minNum and maxNum: a NaN operand yields the other. */
    def minNum(left: Float16, right: Float16): Float16 = FloatingPoint[16](IEEE754.minNum(left, right, 16))
    def maxNum(left: Float16, right: Float16): Float16 = FloatingPoint[16](IEEE754.maxNum(left, right, 16))

    /** IEEE 754-2019 minimum and maximum: any NaN operand yields a quiet NaN. */
    def minimum(left: Float16, right: Float16): Float16 = FloatingPoint[16](IEEE754.minimum(left, right, 16))
    def maximum(left: Float16, right: Float16): Float16 = FloatingPoint[16](IEEE754.maximum(left, right, 16))
  }

  object Float32 {
    inline def apply(value: Float): Float32  = IEEE754.floatToBinary32(value).toLong
    inline def apply(value: Double): Float32 = IEEE754.floatToBinary32(value.toFloat).toLong

    def add(left: Float32, right: Float32): Float32 =
      FloatingPoint[32](IEEE754.add(left, right, 32, IEEE754.NaNOperand.Left))
    def multiply(left: Float32, right: Float32): Float32 =
      FloatingPoint[32](IEEE754.multiply(left, right, 32, IEEE754.NaNOperand.Left))

    /** `left * right + addend` rounded once to the width. */
    def fma(left: Float32, right: Float32, addend: Float32): Float32 =
      FloatingPoint[32](IEEE754.fusedMultiplyAdd(left, right, addend, 32))

    /** IEEE 754-2008 minNum and maxNum: a NaN operand yields the other. */
    def minNum(left: Float32, right: Float32): Float32 = FloatingPoint[32](IEEE754.minNum(left, right, 32))
    def maxNum(left: Float32, right: Float32): Float32 = FloatingPoint[32](IEEE754.maxNum(left, right, 32))

    /** IEEE 754-2019 minimum and maximum: any NaN operand yields a quiet NaN. */
    def minimum(left: Float32, right: Float32): Float32 = FloatingPoint[32](IEEE754.minimum(left, right, 32))
    def maximum(left: Float32, right: Float32): Float32 = FloatingPoint[32](IEEE754.maximum(left, right, 32))
  }

  object Float64 {
    inline def apply(value: Double): Float64 = IEEE754.doubleToBinary64(value)

    def add(left: Float64, right: Float64): Float64 =
      FloatingPoint[64](IEEE754.add(left, right, 64, IEEE754.NaNOperand.Left))
    def multiply(left: Float64, right: Float64): Float64 =
      FloatingPoint[64](IEEE754.multiply(left, right, 64, IEEE754.NaNOperand.Left))

    /** `left * right + addend` rounded once to the width. */
    def fma(left: Float64, right: Float64, addend: Float64): Float64 =
      FloatingPoint[64](IEEE754.fusedMultiplyAdd(left, right, addend, 64))

    /** IEEE 754-2008 minNum and maxNum: a NaN operand yields the other. */
    def minNum(left: Float64, right: Float64): Float64 = FloatingPoint[64](IEEE754.minNum(left, right, 64))
    def maxNum(left: Float64, right: Float64): Float64 = FloatingPoint[64](IEEE754.maxNum(left, right, 64))

    /** IEEE 754-2019 minimum and maximum: any NaN operand yields a quiet NaN. */
    def minimum(left: Float64, right: Float64): Float64 = FloatingPoint[64](IEEE754.minimum(left, right, 64))
    def maximum(left: Float64, right: Float64): Float64 = FloatingPoint[64](IEEE754.maximum(left, right, 64))
  }

  /** Narrow encodings, as [[net.kurobako.gemstone.binary.Minifloat]] defines them, held as their bits. */
  opaque type BFloat16       = Long
  opaque type TensorFloat32  = Long
  opaque type Float8E4M3     = Long
  opaque type Float8E5M2     = Long
  opaque type Float8E4M3FNUZ = Long
  opaque type Float8E5M2FNUZ = Long
  opaque type Float6E2M3     = Long
  opaque type Float6E3M2     = Long
  opaque type Float4E2M1     = Long
  opaque type ScaleE8M0      = Long

  opaque type UInt[W <: Int]          = Long
  opaque type SInt[W <: Int]          = Long
  opaque type FloatingPoint[W <: Int] = Long

  /** The widths with arithmetic of their own, each a `FloatingPoint` of its width. */
  opaque type Float16 <: FloatingPoint[16] = Long
  opaque type Float32 <: FloatingPoint[32] = Long
  opaque type Float64 <: FloatingPoint[64] = Long

  type Bool = Boolean

  private inline def wrap(value: Long, width: Int): Long =
    if (width >= 64) value else value & ((1L << width) - 1L)

  private inline def extend(value: Long, width: Int): Long =
    if (width >= 64) value else (value << (64 - width)) >> (64 - width)

  /** `value` as `T`, a prelude type, without wrapping: execution holds each as a `Long`, a truth value as 1 or 0. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  inline def fromRaw[T](value: Long): T = inline erasedValue[T] match {
    case _: Boolean => (value != 0L).asInstanceOf[T]
    case _          => value.asInstanceOf[T]
  }

  /** The `Long` execution holds `value` as: a truth value is 1 or 0. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  inline def toRaw[T](value: T): Long = inline erasedValue[T] match {
    case _: Boolean => if (value.asInstanceOf[Boolean]) 1L else 0L
    case _          => value.asInstanceOf[Long]
  }

  object Convert {
    class Unsigned[W <: Int](width: Int) extends Convert[UInt[W]] {
      def apply(value: Long): UInt[W] = wrap(value, width)
    }

    class Signed[W <: Int](width: Int) extends Convert[SInt[W]] {
      def apply(value: Long): SInt[W] = extend(value, width)
    }

    inline given unsigned[W <: Int]: Convert[UInt[W]] = Unsigned[W](constValue[W])
    inline given signed[W <: Int]: Convert[SInt[W]]   = Signed[W](constValue[W])
  }

  /** What a machine definition requests effects, checks and control from, as [[MachineAlgebra]] names them. */
  trait Machine {
    def pc(): UInt[64]
    def jump(target: Int): Unit
    def jump(target: UInt[64]): Unit
    def jumpTo(target: UInt[64]): Unit
    def branch(condition: Bool, taken: Int): Unit
    def branch(condition: Bool, taken: UInt[64]): Unit
    def branch(condition: Bool, taken: Int, otherwise: Int): Unit
    def branch(condition: Bool, taken: UInt[64], otherwise: UInt[64]): Unit
    def exit(): Unit
    def park(barrier: UInt[64], count: UInt[64], blocking: Boolean): Unit
    def require(condition: Bool, label: String): Unit
    def load[T](space: Space, address: UInt[64]): T
    def store[T](space: Space, address: UInt[64], value: T): Unit
    def loadBytes(space: Space, address: UInt[64], length: UInt[64]): Array[Byte]
    def storeBytes(space: Space, address: UInt[64], data: Array[Byte]): Unit
  }

  /** What a definition across the lanes of a unit reads from, as [[UnitAlgebra]] names them. */
  trait Lanes {
    type View

    def converged(): Bool
    def index(): UInt[64]
    def size(): UInt[64]
    def gather(operand: Any): View
    def gatherLoad[T](lanes: Int, space: Space, address: UInt[64], offset: Long): View
    def peer(view: View, index: UInt[64]): UInt[64]
    def active(index: UInt[64]): UInt[64]
  }

  /** The value of type `T` a 64-bit value truncates or extends to. */
  trait Convert[T] {
    def apply(value: Long): T
  }

  extension [W <: Int](value: UInt[W]) {
    @targetName("unsignedPlus")
    inline def +(other: UInt[W]): UInt[W] = wrap(value + other, constValue[W])
    @targetName("unsignedPlusLiteral")
    inline def +(other: Long): UInt[W] = wrap(value + other, constValue[W])
    @targetName("unsignedMinus")
    inline def -(other: UInt[W]): UInt[W] = wrap(value - other, constValue[W])
    @targetName("unsignedMinusLiteral")
    inline def -(other: Long): UInt[W] = wrap(value - other, constValue[W])
    @targetName("unsignedTimes")
    inline def *(other: UInt[W]): UInt[W] = wrap(value * other, constValue[W])
    @targetName("unsignedTimesLiteral")
    inline def *(other: Long): UInt[W] = wrap(value * other, constValue[W])
    @targetName("unsignedDivide")
    inline def /(other: UInt[W]): UInt[W] = java.lang.Long.divideUnsigned(value, other)
    @targetName("unsignedDivideLiteral")
    inline def /(other: Long): UInt[W] = java.lang.Long.divideUnsigned(value, other)
    @targetName("unsignedRemainder")
    inline def %(other: UInt[W]): UInt[W] = java.lang.Long.remainderUnsigned(value, other)
    @targetName("unsignedRemainderLiteral")
    inline def %(other: Long): UInt[W] = java.lang.Long.remainderUnsigned(value, other)
    @targetName("unsignedAnd")
    inline def &(other: UInt[W]): UInt[W] = value & other
    @targetName("unsignedAndLiteral")
    inline def &(other: Long): UInt[W] = value & other
    @targetName("unsignedOr")
    inline def |(other: UInt[W]): UInt[W] = value | other
    @targetName("unsignedOrLiteral")
    inline def |(other: Long): UInt[W] = wrap(value | other, constValue[W])
    @targetName("unsignedXor")
    inline def ^(other: UInt[W]): UInt[W] = value ^ other
    @targetName("unsignedXorLiteral")
    inline def ^(other: Long): UInt[W] = wrap(value ^ other, constValue[W])
    @targetName("unsignedNot")
    inline def unary_~ : UInt[W] = wrap(~value, constValue[W])
    @targetName("unsignedNegate")
    inline def unary_- : UInt[W] = wrap(-value, constValue[W])
    @targetName("unsignedShiftLeft")
    inline def <<(amount: Long): UInt[W] = wrap(value << amount, constValue[W])
    @targetName("unsignedShiftLeftBy")
    inline def <<[V <: Int](amount: UInt[V]): UInt[W] = wrap(value << amount, constValue[W])
    @targetName("unsignedShiftRightBy")
    inline def >>[V <: Int](amount: UInt[V]): UInt[W] = value >>> amount
    @targetName("unsignedShiftRight")
    inline def >>(amount: Long): UInt[W] = value >>> amount
    @targetName("unsignedLess")
    inline def <(other: UInt[W]): Boolean = java.lang.Long.compareUnsigned(value, other) < 0
    @targetName("unsignedLessLiteral")
    inline def <(other: Long): Boolean = java.lang.Long.compareUnsigned(value, other) < 0
    @targetName("unsignedLessEqual")
    inline def <=(other: UInt[W]): Boolean = java.lang.Long.compareUnsigned(value, other) <= 0
    @targetName("unsignedLessEqualLiteral")
    inline def <=(other: Long): Boolean = java.lang.Long.compareUnsigned(value, other) <= 0
    @targetName("unsignedGreater")
    inline def >(other: UInt[W]): Boolean = java.lang.Long.compareUnsigned(value, other) > 0
    @targetName("unsignedGreaterLiteral")
    inline def >(other: Long): Boolean = java.lang.Long.compareUnsigned(value, other) > 0
    @targetName("unsignedGreaterEqual")
    inline def >=(other: UInt[W]): Boolean = java.lang.Long.compareUnsigned(value, other) >= 0
    @targetName("unsignedGreaterEqualLiteral")
    inline def >=(other: Long): Boolean = java.lang.Long.compareUnsigned(value, other) >= 0
    inline def bitCount: UInt[W]        = java.lang.Long.bitCount(value).toLong
    inline def leadingZeros: UInt[W]    = (java.lang.Long.numberOfLeadingZeros(value) - (64 - constValue[W])).toLong
    inline def reverseBits: UInt[W]     = java.lang.Long.reverse(value) >>> (64 - constValue[W])
    @targetName("unsignedTrailingZeros")
    inline def trailingZeros: UInt[W] = Math.min(java.lang.Long.numberOfTrailingZeros(value), constValue[W]).toLong
    @targetName("unsignedTo")
    inline def to[T](using inline convert: Convert[T]): T = convert(value)

    /** The value as execution holds it. */
    @targetName("unsignedRaw")
    inline def raw: Long = value
  }

  extension [W <: Int](value: SInt[W]) {
    @targetName("signedPlus")
    inline def +(other: SInt[W]): SInt[W] = extend(value + other, constValue[W])
    @targetName("signedPlusLiteral")
    inline def +(other: Long): SInt[W] = extend(value + other, constValue[W])
    @targetName("signedMinus")
    inline def -(other: SInt[W]): SInt[W] = extend(value - other, constValue[W])
    @targetName("signedMinusLiteral")
    inline def -(other: Long): SInt[W] = extend(value - other, constValue[W])
    @targetName("signedTimes")
    inline def *(other: SInt[W]): SInt[W] = extend(value * other, constValue[W])
    @targetName("signedTimesLiteral")
    inline def *(other: Long): SInt[W] = extend(value * other, constValue[W])
    @targetName("signedDivide")
    inline def /(other: SInt[W]): SInt[W] = extend(value / other, constValue[W])
    @targetName("signedDivideLiteral")
    inline def /(other: Long): SInt[W] = extend(value / other, constValue[W])
    @targetName("signedRemainder")
    inline def %(other: SInt[W]): SInt[W] = value % other
    @targetName("signedRemainderLiteral")
    inline def %(other: Long): SInt[W] = value % other
    @targetName("signedAnd")
    inline def &(other: SInt[W]): SInt[W] = value & other
    @targetName("signedAndLiteral")
    inline def &(other: Long): SInt[W] = extend(value & other, constValue[W])
    @targetName("signedOr")
    inline def |(other: SInt[W]): SInt[W] = value | other
    @targetName("signedOrLiteral")
    inline def |(other: Long): SInt[W] = extend(value | other, constValue[W])
    @targetName("signedXor")
    inline def ^(other: SInt[W]): SInt[W] = value ^ other
    @targetName("signedXorLiteral")
    inline def ^(other: Long): SInt[W] = extend(value ^ other, constValue[W])
    @targetName("signedNot")
    inline def unary_~ : SInt[W] = ~value
    @targetName("signedNegate")
    inline def unary_- : SInt[W] = extend(-value, constValue[W])
    @targetName("signedShiftLeft")
    inline def <<(amount: Long): SInt[W] = extend(value << amount, constValue[W])
    @targetName("signedShiftLeftBy")
    inline def <<[V <: Int](amount: UInt[V]): SInt[W] = extend(value << amount, constValue[W])
    @targetName("signedShiftRightBy")
    inline def >>[V <: Int](amount: UInt[V]): SInt[W] = value >> amount
    @targetName("signedShiftRight")
    inline def >>(amount: Long): SInt[W] = value >> amount
    @targetName("signedLess")
    inline def <(other: SInt[W]): Boolean = value < other
    @targetName("signedLessLiteral")
    inline def <(other: Long): Boolean = value < other
    @targetName("signedLessEqual")
    inline def <=(other: SInt[W]): Boolean = value <= other
    @targetName("signedLessEqualLiteral")
    inline def <=(other: Long): Boolean = value <= other
    @targetName("signedGreater")
    inline def >(other: SInt[W]): Boolean = value > other
    @targetName("signedGreaterLiteral")
    inline def >(other: Long): Boolean = value > other
    @targetName("signedGreaterEqual")
    inline def >=(other: SInt[W]): Boolean = value >= other
    @targetName("signedGreaterEqualLiteral")
    inline def >=(other: Long): Boolean = value >= other
    @targetName("signedTo")
    inline def to[T](using inline convert: Convert[T]): T = convert(value)
    @targetName("signedRaw")
    inline def raw: Long = value
  }

  extension [W <: Int](value: FloatingPoint[W]) {
    inline def bits: UInt[W] = value
  }

  extension (value: BFloat16) {
    @targetName("bFloat16Bits")
    inline def bits: UInt[16] = value
  }

  extension (value: TensorFloat32) {
    @targetName("tensorFloat32Bits")
    inline def bits: UInt[19] = value
  }

  extension (value: Float8E4M3) {
    @targetName("float8E4M3Bits")
    inline def bits: UInt[8] = value
  }

  extension (value: Float8E5M2) {
    @targetName("float8E5M2Bits")
    inline def bits: UInt[8] = value
  }

  extension (value: Float8E4M3FNUZ) {
    @targetName("float8E4M3FNUZBits")
    inline def bits: UInt[8] = value
  }

  extension (value: Float8E5M2FNUZ) {
    @targetName("float8E5M2FNUZBits")
    inline def bits: UInt[8] = value
  }

  extension (value: Float6E2M3) {
    @targetName("float6E2M3Bits")
    inline def bits: UInt[6] = value
  }

  extension (value: Float6E3M2) {
    @targetName("float6E3M2Bits")
    inline def bits: UInt[6] = value
  }

  extension (value: Float4E2M1) {
    @targetName("float4E2M1Bits")
    inline def bits: UInt[4] = value
  }

  extension (value: ScaleE8M0) {
    @targetName("scaleE8M0Bits")
    inline def bits: UInt[8] = value
  }

  extension [W <: Int](value: UInt[W]) {
    transparent inline def asFloat: FloatingPoint[W] = FloatingPoint[W](value)
  }

  extension (value: Float16) {
    @targetName("float16ToFloat")
    inline def toFloat: Float = IEEE754.binary16ToFloat(Binary16.low(value))
    @targetName("float16ToDouble")
    inline def toDouble: Double = IEEE754.binary16ToFloat(Binary16.low(value)).toDouble
  }

  extension (value: Float32) {
    @targetName("float32ToFloat")
    inline def toFloat: Float = java.lang.Float.intBitsToFloat(value.toInt)
    @targetName("float32ToDouble")
    inline def toDouble: Double = java.lang.Float.intBitsToFloat(value.toInt).toDouble
  }

  extension (value: Float64) {
    @targetName("float64ToDouble")
    inline def toDouble: Double = java.lang.Double.longBitsToDouble(value)
  }

  extension (value: BFloat16) {
    @targetName("bFloat16ToDouble")
    inline def toDouble: Double = Minifloat.BFloat16.toDouble(value)
  }

  extension (value: TensorFloat32) {
    @targetName("tensorFloat32ToDouble")
    inline def toDouble: Double = Minifloat.TensorFloat32.toDouble(value)
  }

  extension (value: Float8E4M3) {
    @targetName("float8E4M3ToDouble")
    inline def toDouble: Double = Minifloat.E4M3.toDouble(value)
  }

  extension (value: Float8E5M2) {
    @targetName("float8E5M2ToDouble")
    inline def toDouble: Double = Minifloat.E5M2.toDouble(value)
  }

  extension (value: Float8E4M3FNUZ) {
    @targetName("float8E4M3FNUZToDouble")
    inline def toDouble: Double = Minifloat.E4M3FNUZ.toDouble(value)
  }

  extension (value: Float8E5M2FNUZ) {
    @targetName("float8E5M2FNUZToDouble")
    inline def toDouble: Double = Minifloat.E5M2FNUZ.toDouble(value)
  }

  extension (value: Float6E2M3) {
    @targetName("float6E2M3ToDouble")
    inline def toDouble: Double = Minifloat.E2M3.toDouble(value)
  }

  extension (value: Float6E3M2) {
    @targetName("float6E3M2ToDouble")
    inline def toDouble: Double = Minifloat.E3M2.toDouble(value)
  }

  extension (value: Float4E2M1) {
    @targetName("float4E2M1ToDouble")
    inline def toDouble: Double = Minifloat.E2M1.toDouble(value)
  }

  extension (value: ScaleE8M0) {
    @targetName("scaleE8M0ToDouble")
    inline def toDouble: Double = Minifloat.E8M0.toDouble(value)
  }

  extension (value: Boolean) {
    inline def to[T](using inline convert: Convert[T]): T = convert(if (value) 1L else 0L)
  }

  inline def min[W <: Int](left: UInt[W], right: UInt[W]): UInt[W] =
    if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
  inline def max[W <: Int](left: UInt[W], right: UInt[W]): UInt[W] =
    if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
  @targetName("signedMin")
  inline def min[W <: Int](left: SInt[W], right: SInt[W]): SInt[W] = Math.min(left, right)
  @targetName("signedMax")
  inline def max[W <: Int](left: SInt[W], right: SInt[W]): SInt[W] = Math.max(left, right)

  /** The low `count` bits of `value` shifted right by `offset` modulo 64; an empty field is 0. */
  inline def bitFieldExtract(value: UInt[64], offset: UInt[64], count: UInt[64]): UInt[64] =
    if (count == 0L) 0L else ((value >>> offset) << (64 - count)) >>> (64 - count)

  /** The field sign-extended, with bits shifted in past the top repeating the sign. */
  @targetName("signedBitFieldExtract")
  inline def bitFieldExtract(value: SInt[64], offset: UInt[64], count: UInt[64]): SInt[64] =
    if (count == 0L) 0L else ((value >> offset) << (64 - count)) >> (64 - count)

  /** The bits of `whenSet` where `mask` is set, and of `whenClear` elsewhere. */
  inline def blend[W <: Int](mask: UInt[W], whenSet: UInt[W], whenClear: UInt[W]): UInt[W] =
    (mask & whenSet) | (~mask & whenClear)

  inline def uniform[T](value: T): T = value

  /** The high `W` bits of the `2W`-bit product. */
  inline def multiplyHigh[W <: Int](left: UInt[W], right: UInt[W]): UInt[W] = {
    val width = constValue[W]
    if (width >= 64) Math.unsignedMultiplyHigh(left, right)
    else if (width <= 32) (left * right) >>> width
    else (Math.unsignedMultiplyHigh(left, right) << (64 - width)) | ((left * right) >>> width)
  }

  @targetName("signedMultiplyHigh")
  inline def multiplyHigh[W <: Int](left: SInt[W], right: SInt[W]): SInt[W] = {
    val width = constValue[W]
    if (width >= 64) Math.multiplyHigh(left, right)
    else if (width <= 32) extend((left * right) >> width, width)
    else extend((Math.multiplyHigh(left, right) << (64 - width)) | ((left * right) >>> width), width)
  }
}

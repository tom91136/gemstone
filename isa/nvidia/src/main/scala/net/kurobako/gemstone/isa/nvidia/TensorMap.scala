package net.kurobako.gemstone.isa.nvidia

import TensorMapping.*
import TensorMapping.Field as Read
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** The 128-byte tensor map that tensor copies describe their global tensor and box with. Fields `tensormap.replace`
  * reaches sit where ptxas writes them: the global address at byte 0; at word 8 the rank less one from bit 4, the
  * element type from bit 7, the interleave from bit 11, the swizzle from bit 13 (its third bit at 22), the NaN fill at
  * bit 15, the TF32 flag at bit 16 and the swizzle atomicity from bit 19; the global strides in 16-byte units at words
  * 0x0c..0x18 with their bits 32..35 in nibbles of word 0x1c; the global sizes less one at words 0x20..0x30; the
  * traversal strides less one in three-bit fields of word 0x34 and the box sizes less one in its top byte and the bytes
  * of word 0x38. Fields only the driver encodes follow at word 0x40: the mode, the L2 promotion, the channels per
  * pixel, the pixels per column, then the im2col lower and upper corners for W, H and D.
  */
private[isa] object TensorMap {
  inline val Bytes = 128

  enum Mode derives CanEqual {
    case Tile, Im2col, Im2colWide, Im2colWide128
  }

  object Element {

    /** The element a `tensormap.replace.tile.elemtype` immediate names. */
    def fromInstruction(value: Int): Option[Element] = values.find(_.instruction == value)

    // by code, then by code with the TF32 flag set
    private val decoded =
      Array.tabulate(32)(key => values.find(element => element.tf32 == key >= 16 && element.code.raw == key % 16))

    def decode(code: Int, tf32: Boolean): Option[Element] =
      if (code < 0 || code >= 16) None else decoded(if (tf32) code + 16 else code)
  }

  /** Element types by their hardware code, which `tensormap.replace` names directly up to `BFloat16`. Packed types move
    * in units of `values` elements, `bytes` of them in global memory and `stored` in shared memory: two 4-bit values to
    * a byte for `.b4x16`, sixteen 4-bit values padded to 16 bytes for `.b4x16_p64`, and sixteen 6-bit values padded to
    * 16 bytes for `.b6x16_p32` loads or spread one to a byte for `.b6p2x16` stores.
    */
  enum Element(
      val code: Int in 0 ~ 13,
      val bytes: Int in (1, 2, 4, 8, 12),
      val floating: Boolean,
      val values: Int in (1, 2, 16) = 1,
      val stored: Int in (0, 1, 16) = 0
  ) derives CanEqual {
    case U8          extends Element(code = 0, bytes = 1, floating = false)
    case U16         extends Element(code = 1, bytes = 2, floating = false)
    case U32         extends Element(code = 2, bytes = 4, floating = false)
    case S32         extends Element(code = 3, bytes = 4, floating = false)
    case U64         extends Element(code = 4, bytes = 8, floating = false)
    case S64         extends Element(code = 5, bytes = 8, floating = false)
    case F16         extends Element(code = 6, bytes = 2, floating = true)
    case F32         extends Element(code = 7, bytes = 4, floating = true)
    case F32Flushed  extends Element(code = 8, bytes = 4, floating = true)
    case F64         extends Element(code = 9, bytes = 8, floating = true)
    case BF16        extends Element(code = 10, bytes = 2, floating = true)
    case TF32        extends Element(code = 7, bytes = 4, floating = true)
    case TF32Flushed extends Element(code = 8, bytes = 4, floating = true)
    case B4x16       extends Element(code = 11, bytes = 1, floating = false, values = 2, stored = 1)
    case B4x16P64    extends Element(code = 12, bytes = 8, floating = false, values = 16, stored = 16)
    case B6x16       extends Element(code = 13, bytes = 12, floating = false, values = 16, stored = 16)

    def width: ScalarBitWidth = (bytes * 8).unsafe[ScalarBitWidth]
    def size: Int in 0 ~ 8    = bytes.unsafe[Int in 0 ~ 8]
    def tf32: Boolean         = this == TF32 || this == TF32Flushed
    def sharedBytes: Int      = if (stored == 0) bytes else stored

    /** The `tensormap.replace.tile.elemtype` immediate that names this element. */
    def instruction: Int = this match {
      case TF32        => 11
      case TF32Flushed => 12
      case B4x16       => 13
      case B4x16P64    => 14
      case B6x16       => 15
      case _           => code
    }

    /** The bits of the NaN that NaN fill writes for out-of-bounds floating-point elements. */
    def outOfBoundsNaN: Long = bytes.raw match {
      case 2 => 0x7fffL
      case 4 => 0x7fffffffL
      case _ => 0x7fffffffffffffffL
    }
  }

  enum Reduction derives CanEqual {
    case Add, Minimum, Maximum, Increment, Decrement, And, Or, Xor

    /** The new value of an element that receives `operand`, or None where the element type has no such reduction. */
    def apply(element: Element, old: Long, operand: Long): Option[Long] = {
      import Element.*
      def atomic(operation: AtomicOperation): Option[Long] = Some(operation(old, operand, element.width))
      (this, element) match {
        case (Add, U32 | S32 | U64 | S64)        => atomic(AtomicOperation.Add)
        case (Add, F16 | F32 | F32Flushed | F64) => atomic(AtomicOperation.FloatAdd)
        case (Add, BF16)                         => atomic(AtomicOperation.BFloat16Add)
        case (Minimum, U32 | U64)                => atomic(AtomicOperation.MinimumUnsigned)
        case (Minimum, S32 | S64)                => atomic(AtomicOperation.MinimumSigned)
        case (Minimum, F16)                      => atomic(AtomicOperation.FloatMinimum)
        case (Minimum, BF16)                     => Some(bfloat16Extremum(old, operand)(_ < _))
        case (Maximum, U32 | U64)                => atomic(AtomicOperation.MaximumUnsigned)
        case (Maximum, S32 | S64)                => atomic(AtomicOperation.MaximumSigned)
        case (Maximum, F16)                      => atomic(AtomicOperation.FloatMaximum)
        case (Maximum, BF16)                     => Some(bfloat16Extremum(old, operand)(_ >= _))
        case (Increment, U32)                    => atomic(AtomicOperation.IncrementWrap)
        case (Decrement, U32)                    => atomic(AtomicOperation.DecrementWrap)
        case (And, U32 | S32 | U64 | S64)        => atomic(AtomicOperation.And)
        case (Or, U32 | S32 | U64 | S64)         => atomic(AtomicOperation.Or)
        case (Xor, U32 | S32 | U64 | S64)        => atomic(AtomicOperation.Xor)
        case _                                   => None
      }
    }
  }

  // A NaN operand loses to a number, and -0 orders below +0, as the binary16 forms do.
  private inline def bfloat16Extremum(old: Long, operand: Long)(inline keepsOld: (Int, Int) => Boolean): Long = {
    val a                       = (old & 0xffffL).toInt
    val b                       = (operand & 0xffffL).toInt
    def nan(bits: Int): Boolean = (bits & 0x7f80) == 0x7f80 && (bits & 0x7f) != 0
    def key(bits: Int): Int     = if ((bits & 0x8000) != 0) -(bits & 0x7fff) - 1 else bits
    if (nan(a)) b.toLong
    else if (nan(b)) a.toLong
    else if (keepsOld(key(a), key(b))) a.toLong
    else b.toLong
  }

  enum Operation derives CanEqual {
    case Load, Store, Prefetch
    case Reduce(operator: Reduction)
  }

  enum Field derives CanEqual {
    case GlobalAddress, Rank, BoxDimension, GlobalDimension, GlobalStride, ElementStride, ElementType, Interleave,
      Swizzle, SwizzleAtomicity, FillMode
  }

  /** A decoded tensor map. Strides are in bytes from dimension 1; corners are W, H and D. */
  case class Descriptor(
      address: Long,
      rank: Int in 1 ~ 5,
      element: Element,
      interleave: Int,
      swizzle: Int,
      atomicity: Int,
      nanFill: Boolean,
      dimensions: Vec[Long],
      strides: Vec[Long],
      box: Vec[Int],
      traversal: Vec[Int],
      mode: Mode,
      channels: Int,
      pixels: Int,
      lower: Vec[Int],
      upper: Vec[Int]
  ) {

    /** Elements of the box along each dimension, as the traversal stride thins it. */
    def boxElements: Vec[Int] =
      box.zip(traversal).map((size, stride) => Execute.elements(size.toLong, stride.toLong).toInt)

  }

  private def word(bytes: Array[Byte], offset: Int): Long = LittleEndian.decode(bytes, offset, 4)

  // Fields of word 8.
  private inline val Flags         = 8
  private inline val RankBits      = 0x70L
  private inline val ElementBits   = 0x780L
  private inline val InterleaveBit = 0x1800L
  private inline val SwizzleBits   = 0x6000L
  private inline val NaNFillBit    = 0x8000L
  private inline val TF32Bit       = 0x10000L
  private inline val AtomicityBits = 0x380000L
  private inline val SwizzleHigh   = 0x400000L

  private def insert(bytes: Array[Byte], offset: Int, mask: Long)(value: Long): Unit =
    LittleEndian.write(
      (word(bytes, offset) & ~mask) | ((value << java.lang.Long.numberOfTrailingZeros(mask)) & mask),
      bytes,
      offset,
      4
    )

  private def strideHigh(index: Int): Long    = 0xfL << (index * 4)
  private def traversalBits(index: Int): Long = 7L << (index * 3)

  // The first box size is the top byte of word 0x34, the others the bytes of word 0x38.
  private def boxWord(index: Int): Int  = if (index == 0) 0x34 else 0x38
  private def boxBits(index: Int): Long = if (index == 0) 0xffL << 24 else 0xffL << ((index - 1) * 8)

  /** Bits of a map word: `width` bits from bit `shift` of the `bytes`-byte little-endian word at byte `offset`, shifted
    * up by `into` in the field they build.
    */
  case class Bits(offset: Int, bytes: Int, shift: Int, width: Int, into: Int = 0)

  /** The bits a field is built from, added to `bias`, and sign-extended from 32 bits when `signed`. */
  case class Layout(bits: Vec[Bits], bias: Long = 0L, signed: Boolean = false)

  /** Where a field of a tensor map sits, at `axis` where the field repeats. */
  def layout(field: Read, axis: Int): Layout = {
    def word(offset: Int, shift: Int, width: Int, into: Int = 0): Bits =
      Bits(offset = offset, bytes = 4, shift = shift, width = width, into = into)
    field match {
      case Read.Address    => Layout(Vec(Bits(offset = 0, bytes = 8, shift = 0, width = 64)))
      case Read.Rank       => Layout(Vec(word(offset = 8, shift = 4, width = 3)), bias = 1L)
      case Read.Mode       => Layout(Vec(word(offset = 0x40, shift = 0, width = 32)))
      case Read.Element    => Layout(Vec(word(offset = 8, shift = 7, width = 4)))
      case Read.Tf32       => Layout(Vec(word(offset = 8, shift = 16, width = 1)))
      case Read.Interleave => Layout(Vec(word(offset = 8, shift = 11, width = 2)))
      case Read.Swizzle    =>
        Layout(Vec(
          word(offset = 8, shift = 13, width = 2),
          word(offset = 8, shift = 22, width = 1, into = 2)
        ))
      case Read.Atomicity => Layout(Vec(word(offset = 8, shift = 19, width = 3)))
      case Read.FillMode  => Layout(Vec(word(offset = 8, shift = 15, width = 1)))
      case Read.Dimension => Layout(Vec(word(offset = 0x20 + 4 * axis, shift = 0, width = 32)), bias = 1L)
      case Read.Stride    =>
        Layout(Vec(
          word(offset = 0x0c + 4 * (axis - 1), shift = 0, width = 32, into = 4),
          word(offset = 0x1c, shift = 4 * (axis - 1), width = 4, into = 36)
        ))
      case Read.Box =>
        Layout(
          Vec(if (axis == 0) word(offset = 0x34, shift = 24, width = 8)
          else word(offset = 0x38, shift = 8 * (axis - 1), width = 8)),
          bias = 1L
        )
      case Read.Traversal => Layout(Vec(word(offset = 0x34, shift = 3 * axis, width = 3)), bias = 1L)
      case Read.Channels  => Layout(Vec(word(offset = 0x48, shift = 0, width = 32)), signed = true)
      case Read.Pixels    => Layout(Vec(word(offset = 0x4c, shift = 0, width = 32)), signed = true)
      case Read.Lower     => Layout(Vec(word(offset = 0x50 + 4 * axis, shift = 0, width = 32)), signed = true)
      case Read.Upper     => Layout(Vec(word(offset = 0x5c + 4 * axis, shift = 0, width = 32)), signed = true)
    }
  }

  /** The field `kind` at `axis` of a tensor map whose words `load` reads, built from the bits `layout` gives. */
  inline def field[V](inline algebra: Algebra[V], inline load: (Int, Int) => V, kind: Read, axis: Int): V = {
    val spec  = layout(kind, axis)
    val value = spec.bits.tail.foldLeft(bits(algebra, load, spec.bits(0)))((built, range) =>
      algebra.or(built, bits(algebra, load, range))
    )
    val biased = if (spec.bias == 0L) value else algebra.add(value, algebra.literal(spec.bias))
    if (spec.signed) algebra.signExtend(biased, 32) else biased
  }

  private inline def bits[V](inline algebra: Algebra[V], inline load: (Int, Int) => V, range: Bits): V = {
    val part =
      algebra.field(load(range.offset, range.bytes), range.shift, range.width.unsafe[Int in 1 ~ 64], signed = false)
    if (range.into == 0) part else algebra.shiftLeft(part, algebra.literal(range.into.toLong))
  }

  /** 1 where a tensor map decodes: its rank is from 1 to 5, its mode is one of the four and its element type is named.
    */
  inline def valid[V](inline algebra: Algebra[V], rank: V, mode: V, values: V): V =
    algebra.and(
      algebra.and(
        algebra.lessOrEqual(algebra.literal(1L), rank, signed = false),
        algebra.lessOrEqual(rank, algebra.literal(5L), signed = false)
      ),
      algebra.and(
        algebra.lessOrEqual(mode, algebra.literal(Mode.Im2colWide128.ordinal.toLong), signed = false),
        algebra.notEqual(values, algebra.literal(0L))
      )
    )

  private def read(bytes: Array[Byte], kind: Read, axis: Int = 0): Long =
    field(Execute, (offset, width) => LittleEndian.decode(bytes, offset, width.unsafe[Int in 0 ~ 8]), kind, axis)

  def decode(bytes: Array[Byte]): Either[Fault, Descriptor] = {
    val decoded = Element.decode(read(bytes, Read.Element).toInt, read(bytes, Read.Tf32) != 0L)
    val valid   =
      TensorMap.valid(Execute, read(bytes, Read.Rank), read(bytes, Read.Mode), decoded.fold(0L)(_.values.toLong))
    decoded match {
      case Some(element) if valid != 0L =>
        val rank = read(bytes, Read.Rank).toInt.unsafe[Int in 1 ~ 5]
        Right(Descriptor(
          read(bytes, Read.Address),
          rank,
          element,
          read(bytes, Read.Interleave).toInt,
          read(bytes, Read.Swizzle).toInt,
          read(bytes, Read.Atomicity).toInt,
          read(bytes, Read.FillMode) != 0L,
          Vec.tabulate(rank)(axis => read(bytes, Read.Dimension, axis)),
          Vec.tabulate(rank - 1)(axis => read(bytes, Read.Stride, axis + 1)),
          Vec.tabulate(rank)(axis => read(bytes, Read.Box, axis).toInt),
          Vec.tabulate(rank)(axis => read(bytes, Read.Traversal, axis).toInt),
          Mode.values(read(bytes, Read.Mode).toInt),
          read(bytes, Read.Channels).toInt,
          read(bytes, Read.Pixels).toInt,
          Vec.tabulate(3)(axis => read(bytes, Read.Lower, axis).toInt),
          Vec.tabulate(3)(axis => read(bytes, Read.Upper, axis).toInt)
        ))
      case _ => Left(Fault.Backend("invalid tensor map"))
    }
  }

  /** The largest value `tensormap.replace` accepts for `field`, where it has one. */
  def limit(field: Field): Option[Long] = field match {
    case Field.Interleave       => Some(2L)
    case Field.Swizzle          => Some(4L)
    case Field.SwizzleAtomicity => Some(3L)
    case _                      => None
  }

  /** Whether `tensormap.replace` accepts `value` for `field`. */
  def accepts(field: Field, value: Long): Boolean = field match {
    case Field.ElementType => Element.fromInstruction(value.toInt).isDefined
    case _                 => limit(field).forall(value <= _)
  }

  /** Writes a value `accepts` takes into one field. */
  def update(bytes: Array[Byte], field: Field, ordinal: Int, value: Long): Unit = field match {
    case Field.GlobalAddress   => LittleEndian.write(value, bytes, 0, 8)
    case Field.Rank            => insert(bytes, Flags, RankBits)(value)
    case Field.BoxDimension    => insert(bytes, boxWord(ordinal), boxBits(ordinal))(value - 1L)
    case Field.GlobalDimension => LittleEndian.write(value - 1L, bytes, 0x20 + ordinal * 4, 4)
    case Field.GlobalStride    =>
      val units = value >>> 4
      LittleEndian.write(units, bytes, 0x0c + ordinal * 4, 4)
      insert(bytes, 0x1c, strideHigh(ordinal))(units >>> 32)
    case Field.ElementStride => insert(bytes, 0x34, traversalBits(ordinal))(value - 1L)
    case Field.ElementType   =>
      // ptxas sets the TF32 flag for the TF32 types and leaves it as it was for the others
      Element.fromInstruction(value.toInt).foreach { element =>
        if (element.tf32) insert(bytes, Flags, TF32Bit)(1L)
        insert(bytes, Flags, ElementBits)(element.code.toLong)
      }
    case Field.Interleave => insert(bytes, Flags, InterleaveBit)(value)
    case Field.Swizzle    =>
      insert(bytes, Flags, SwizzleHigh)(value >>> 2)
      insert(bytes, Flags, SwizzleBits)(value)
    case Field.SwizzleAtomicity => insert(bytes, Flags, AtomicityBits)(value)
    case Field.FillMode         => insert(bytes, Flags, NaNFillBit)(value)
  }
}

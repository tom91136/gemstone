package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
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
    def packed: Boolean       = values > 1

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
    def boxElements: Vec[Int] = box.zip(traversal).map((size, stride) => (size + stride - 1) / stride)

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

  private def field(bytes: Array[Byte], offset: Int, mask: Long): Int =
    ((word(bytes, offset) & mask) >>> java.lang.Long.numberOfTrailingZeros(mask)).toInt

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

  private val modes = Mode.values

  def decode(bytes: Array[Byte]): Either[Fault, Descriptor] = {
    val rank  = field(bytes, Flags, RankBits) + 1
    val mode  = modes.lift(word(bytes, 0x40).toInt)
    val types = Element.decode(field(bytes, Flags, ElementBits), field(bytes, Flags, TF32Bit) != 0)
    (rank.refine[Int in 1 ~ 5].toOption, mode, types) match {
      case (Some(validRank), Some(validMode), Some(element)) =>
        val dimensions = Vec.tabulate(rank)(index => word(bytes, 0x20 + index * 4) + 1L)
        val strides    = Vec.tabulate(rank - 1)(index =>
          (word(bytes, 0x0c + index * 4) | (field(bytes, 0x1c, strideHigh(index)).toLong << 32)) << 4
        )
        val box                      = Vec.tabulate(rank)(index => field(bytes, boxWord(index), boxBits(index)) + 1)
        def signed(offset: Int): Int = word(bytes, offset).toInt
        Right(Descriptor(
          LittleEndian.decode(bytes, 0, 8),
          validRank,
          element,
          field(bytes, Flags, InterleaveBit),
          field(bytes, Flags, SwizzleBits) | (field(bytes, Flags, SwizzleHigh) << 2),
          field(bytes, Flags, AtomicityBits),
          field(bytes, Flags, NaNFillBit) != 0,
          dimensions,
          strides,
          box,
          Vec.tabulate(rank)(index => field(bytes, 0x34, traversalBits(index)) + 1),
          validMode,
          word(bytes, 0x48).toInt,
          word(bytes, 0x4c).toInt,
          Vec.tabulate(3)(index => signed(0x50 + index * 4)),
          Vec.tabulate(3)(index => signed(0x5c + index * 4))
        ))
      case _ => Left(Fault.Backend("invalid tensor map"))
    }
  }

  /** Replaces one field as `tensormap.replace` does, or reports why the value is invalid. */
  def replace(bytes: Array[Byte], field: Field, ordinal: Int, value: Long): Either[Fault, Unit] = {
    def invalid = Left(Fault.Backend(s"invalid tensor map $field value $value"))
    field match {
      case Field.GlobalAddress   => Right(LittleEndian.write(value, bytes, 0, 8))
      case Field.Rank            => Right(insert(bytes, Flags, RankBits)(value))
      case Field.BoxDimension    => Right(insert(bytes, boxWord(ordinal), boxBits(ordinal))(value - 1L))
      case Field.GlobalDimension => Right(LittleEndian.write(value - 1L, bytes, 0x20 + ordinal * 4, 4))
      case Field.GlobalStride    =>
        val units = value >>> 4
        LittleEndian.write(units, bytes, 0x0c + ordinal * 4, 4)
        Right(insert(bytes, 0x1c, strideHigh(ordinal))(units >>> 32))
      case Field.ElementStride => Right(insert(bytes, 0x34, traversalBits(ordinal))(value - 1L))
      case Field.ElementType   =>
        // ptxas sets the TF32 flag for the TF32 types and leaves it as it was for the others
        Element.fromInstruction(value.toInt).fold(invalid) { element =>
          if (element.tf32) insert(bytes, Flags, TF32Bit)(1L)
          Right(insert(bytes, Flags, ElementBits)(element.code.toLong))
        }
      case Field.Interleave => if (value > 2) invalid else Right(insert(bytes, Flags, InterleaveBit)(value))
      case Field.Swizzle    =>
        if (value > 4) invalid
        else {
          insert(bytes, Flags, SwizzleHigh)(value >>> 2)
          Right(insert(bytes, Flags, SwizzleBits)(value))
        }
      case Field.SwizzleAtomicity => if (value > 3) invalid else Right(insert(bytes, Flags, AtomicityBits)(value))
      case Field.FillMode         => Right(insert(bytes, Flags, NaNFillBit)(value))
    }
  }

  /** The shared-memory offset a byte offset within a box lands at under the descriptor's swizzle, given the shared
    * address the box starts at.
    */
  def swizzled(descriptor: Descriptor, start: Long, offset: Long): Long = {
    val address = start + offset
    val row     = address >>> 7
    val bits    = descriptor.swizzle match {
      case 1 | 4 => MatrixDescriptor.swizzled(address, 32)
      case 2     => MatrixDescriptor.swizzled(address, 64)
      case 3     =>
        descriptor.atomicity match {
          case 1 => address ^ ((row & 3L) << 5)
          case 2 => address ^ ((row & 3L) << 5) ^ ((row & 1L) << 3)
          case 3 => address ^ ((row & 1L) << 6)
          case _ => MatrixDescriptor.swizzled(address, 128)
        }
      case _ => address
    }
    bits - start
  }
}

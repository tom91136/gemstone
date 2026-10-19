package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** A buffer resource descriptor (V#), four dwords, as `generation` lays it out, and where a lane's access through it
  * lands.
  *
  * A lane addresses `base + soffset + bufferOffset` where, from the lane's index and its offset (the offset VGPR plus
  * the instruction offset), `bufferOffset` is `index * stride + offset`, or with swizzling, which applies once an index
  * does, the element-interleaved `(index / indexStride * stride + offset / element * element) * indexStride + index %
  * indexStride * element + offset % element`. GFX10 and later index by the index VGPR, or by the lane when ADD_TID is
  * set and the instruction has none; GFX8 and GFX9 add the lane to the index VGPR.
  *
  * Range checks follow RDNA2 hardware on GFX10 and later: OOB_SELECT 0 checks the index against NUM_RECORDS and
  * `offset + size` against the stride, 1 the index alone, 2 only that NUM_RECORDS is non-zero, and 3 `offset + soffset
  * + size` against NUM_RECORDS in bytes, or, swizzled with a stride, as 0 does. GFX9 checks the index for an indexed
  * access with a stride and otherwise `offset` before `soffset` in bytes, and GFX8 the index of a swizzled access with
  * a stride and otherwise the buffer offset in bytes; an ADD_TID access without an index, the private segment, is not
  * checked. Multi-dword accesses check each dword, so the bytes in range lead.
  *
  * The functions over prelude types are what definitions call and execution runs.
  */
private[isa] object BufferResource {
  import ResourceGeneration.*

  private inline def bits(value: UInt[32]): Long = toRaw[UInt[32]](value)

  /** The base address dwords 0 and 1 hold. */
  def base(word0: UInt[32], word1: UInt[32]): UInt[64] =
    fromRaw[UInt[64]]((bits(word0) & 0xffffffffL) | ((bits(word1) & 0xffffL) << 32))

  /** An ADDR64 access's address: the base plus a 64-bit address and the scalar offset, ignoring the range. */
  def wideAddress(word0: UInt[32], word1: UInt[32], address: UInt[64], scalarOffset: UInt[32]): UInt[64] =
    fromRaw[UInt[64]](
      toRaw[UInt[64]](base(word0, word1)) + toRaw[UInt[64]](address) +
        (bits(scalarOffset) & 0xffffffffL)
    )

  def stride(generation: ResourceGeneration, word1: Long, word3: Long): Long = {
    val field = (word1 >>> 16) & 0x3fffL
    if (generation == GFX12) field << StrideScales((word3 >>> 18).toInt & 3) else field
  }

  // RDNA4 STRIDE_SCALE multiplies the stride by 1, 4, 8 or 32
  private val StrideScales = Array(0, 2, 3, 5)

  inline def numRecords(word2: Long): Long = word2 & 0xffffffffL

  /** The bytes a swizzled element spans, or zero where swizzling is off. */
  def element(generation: ResourceGeneration, word1: Long, word3: Long): Int = generation match {
    case GFX8          => if (Bits.isSet(word1, 31)) 2 << ((word3 >>> 19).toInt & 3) else 0
    case GFX9 | GFX10  => if (Bits.isSet(word1, 31)) 4 else 0
    case GFX11 | GFX12 =>
      val code = (word1 >>> 30).toInt & 3
      if (code == 0) 0 else 2 << code
  }

  inline def indexStride(word3: Long): Int = 8 << ((word3 >>> 21).toInt & 3)

  inline def addThread(word3: Long): Boolean = Bits.isSet(word3, 23)

  inline def outOfBoundsSelect(word3: Long): Int = (word3 >>> 28).toInt & 3

  /** The data format the descriptor names for format instructions. */
  def format(generation: ResourceGeneration, word3: Long): TexelFormat = generation match {
    case GFX8 | GFX9 => TexelFormat.legacy((word3 >>> 15).toInt & 0xf, (word3 >>> 12).toInt & 7)
    case GFX10       => TexelFormat.unified(generation, (word3 >>> 12).toInt & 0x7f)
    case _           => TexelFormat.unified(generation, (word3 >>> 12).toInt & 0x3f)
  }

  /** The index a lane addresses by: its index VGPR, `vector`, when `indexed`, and the lane under ADD_TID. */
  def index(
      generation: ResourceGeneration,
      word3: UInt[32],
      indexed: Boolean,
      vector: UInt[32],
      lane: UInt[32]
  ): UInt[64] = {
    val thread = if (addThread(bits(word3))) bits(lane) else 0L
    fromRaw[UInt[64]](generation match {
      case GFX8 | GFX9 => (if (indexed) bits(vector) else 0L) + thread
      case _           => if (indexed) bits(vector) else thread
    })
  }

  /** The byte offset into the buffer of `index` and `offset`. */
  def bufferOffset(
      generation: ResourceGeneration,
      word1: UInt[32],
      word3: UInt[32],
      indexed: Boolean,
      index: UInt[64],
      offset: UInt[32]
  ): UInt[64] = fromRaw[UInt[64]](
    placed(
      stride(generation, bits(word1), bits(word3)),
      element(generation, bits(word1), bits(word3)),
      indexed || addThread(bits(word3)),
      indexStride(bits(word3)),
      toRaw[UInt[64]](index),
      bits(offset)
    )
  )

  /** [[bufferOffset]] with the descriptor's `stride`, swizzle `element` and `indexStride` decoded, `indexing` once the
    * access has an index or ADD_TID applies.
    */
  def placed(stride: Long, element: Int, indexing: Boolean, indexStride: Int, at: Long, within: Long)
      : Long =
    if (element == 0 || !indexing) at * stride + within
    else {
      val elements = indexStride.toLong
      (at / elements * stride + within / element * element) * elements + at % elements * element + within % element
    }

  /** How many leading bytes of an access of `bytes`, checked in `component`-byte parts, are in range. */
  def valid(
      generation: ResourceGeneration,
      word1: UInt[32],
      word2: UInt[32],
      word3: UInt[32],
      indexed: Boolean,
      index: UInt[64],
      offset: UInt[32],
      scalarOffset: UInt[32],
      bytes: Int,
      component: Int
  ): UInt[32] = fromRaw[UInt[32]](
    inRange(
      generation = generation,
      word2 = bits(word2),
      word3 = bits(word3),
      stride = stride(generation, bits(word1), bits(word3)),
      element = element(generation, bits(word1), bits(word3)),
      indexed = indexed,
      index = toRaw[UInt[64]](index),
      offset = bits(offset),
      scalarOffset = bits(scalarOffset),
      bytes = bytes,
      component = component
    ).toLong
  )

  /** [[valid]] with the descriptor's `stride` and swizzle `element` decoded. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def inRange(
      generation: ResourceGeneration,
      word2: Long,
      word3: Long,
      stride: Long,
      element: Int,
      indexed: Boolean,
      index: Long,
      offset: Long,
      scalarOffset: Long,
      bytes: Int,
      component: Int
  ): Int = {
    val records = numRecords(word2)
    // the leading components whose end, from `start`, stays within `limit`
    def within(start: Long, limit: Long): Int =
      if (start + component > limit) 0 else Math.min(bytes.toLong, (limit - start) / component * component).toInt
    // the leading components that start below `limit`
    def starting(start: Long, limit: Long): Int =
      if (start >= limit) 0 else Math.min(bytes.toLong, (limit - start + component - 1) / component * component).toInt
    def structured: Int = if (index >= records) 0 else within(offset, stride)
    generation match {
      case GFX8 =>
        if (addThread(word3) && !indexed) bytes
        else if (element != 0 && stride != 0L) if (index >= records) 0 else bytes
        else starting(index * stride + offset, records)
      case GFX9 =>
        if (addThread(word3) && !indexed) bytes
        else if (indexed && stride != 0L) if (index >= records) 0 else bytes
        // the scalar offset counts, unlike the ISA documents say: Tensile's CDNA kernels need it to zero K's tail
        else starting(offset + scalarOffset, records)
      case _ =>
        outOfBoundsSelect(word3) match {
          case 0 => structured
          case 1 => if (index >= records) 0 else bytes
          case 2 => if (records == 0L) 0 else bytes
          case _ =>
            if (element != 0 && stride != 0L) structured
            else within(offset + scalarOffset, records)
        }
    }
  }

  /** The destination select dword 3 holds for `component`, as [[TexelFormat.select]]. */
  def destination(word3: UInt[32], component: Int): UInt[32] =
    fromRaw[UInt[32]](TexelFormat.select(bits(word3), component).toLong)

  /** The bytes of a texel of the format dword 3 names, at least one. */
  def texelBytes(generation: ResourceGeneration, word3: UInt[32]): UInt[32] =
    fromRaw[UInt[32]](Math.max(format(generation, bits(word3)).layout.bytes, 1).toLong)

  /** Whether dword 3 names a format that stores convert to. */
  def formatted(generation: ResourceGeneration, word3: UInt[32]): Bool = format(generation, bits(word3)).valid

  /** Whether a whole texel of the format dword 3 names is in range, as [[valid]] checks one. */
  def texelValid(
      generation: ResourceGeneration,
      word1: UInt[32],
      word2: UInt[32],
      word3: UInt[32],
      indexed: Boolean,
      index: UInt[64],
      offset: UInt[32],
      scalarOffset: UInt[32]
  ): Bool = {
    val bytes = toRaw[UInt[32]](texelBytes(generation, word3)).toInt
    toRaw[UInt[32]](
      valid(generation, word1, word2, word3, indexed, index, offset, scalarOffset, bytes, bytes)
    ) == bytes.toLong
  }

  /** [[TexelFormat.component]] `index` of a texel of the format dword 3 names, through its selects. */
  def component(
      generation: ResourceGeneration,
      word3: UInt[32],
      low: UInt[64],
      high: UInt[64],
      index: Int,
      half: Boolean
  ): UInt[32] = fromRaw[UInt[32]](
    TexelFormat.component(
      format(generation, bits(word3)),
      TexelFormat.select(bits(word3), index),
      toRaw[UInt[64]](low),
      toRaw[UInt[64]](high),
      half
    )
  )

  /** [[TexelFormat.packed]] in the format and through the selects dword 3 names. */
  def packed(
      generation: ResourceGeneration,
      word3: UInt[32],
      x: UInt[32],
      y: UInt[32],
      z: UInt[32],
      w: UInt[32],
      part: Int
  ): UInt[64] = fromRaw[UInt[64]](
    TexelFormat.packed(
      format(generation, bits(word3)),
      bits(word3),
      Array(bits(x), bits(y), bits(z), bits(w)),
      converted = true,
      part
    )
  )

  /** [[TexelFormat.widen]] in the format dword 3 names. */
  def widened(generation: ResourceGeneration, word3: UInt[32], value: UInt[16]): UInt[32] =
    fromRaw[UInt[32]](
      TexelFormat.widen(format(generation, bits(word3)).number, toRaw[UInt[16]](value))
    )

  /** How many whole elements of `size` bytes, of `count`, a scalar load from byte `start` reads: those that end within
    * NUM_RECORDS records of the stride, or bytes without one.
    */
  def scalarElements(
      generation: ResourceGeneration,
      word1: UInt[32],
      word2: UInt[32],
      word3: UInt[32],
      start: UInt[32],
      size: Int,
      count: Int
  ): UInt[32] = {
    val stride  = this.stride(generation, bits(word1), bits(word3))
    val records = numRecords(bits(word2))
    val limit   = if (stride == 0L) records else records * stride
    val first   = bits(start)
    fromRaw[UInt[32]](if (first + size > limit) 0L else Math.min(count.toLong, (limit - first) / size))
  }
}

package net.kurobako.gemstone.isa.nvidia

import VMIR.Matrix.*
import net.kurobako.gemstone.constraint.*

/** Warpgroup matrix products, PTX `wgmma.mma_async` and Hopper's GMMA: four consecutive warps whose 128 lanes hold D
  * and any register A, each warp sixteen rows of them, with matrix descriptors placing A and B in shared memory in the
  * canonical layouts of the PTX ISA (9.7.16.5.1).
  */
private[isa] object Warpgroup {
  val Lanes: Int = 128
  val Rows: Int  = 64

  /** The swizzle a descriptor's top two bits name, by its ordinal, and the bytes of each row of its pattern. */
  enum Swizzle(val bytes: Int in (16, 32, 64, 128)) derives CanEqual {
    case Interleave extends Swizzle(16)
    case B128       extends Swizzle(128)
    case B64        extends Swizzle(64)
    case B32        extends Swizzle(32)

    /** The 16-byte chunks in a row of the pattern. */
    def width: Int = bytes / 16
  }

  object Descriptor {

    /** The fields of a descriptor's 64 bits; offsets and addresses hold their bits 4 to 17. */
    def apply(bits: Long): Descriptor = Descriptor(
      MatrixDescriptor.start(bits),
      MatrixDescriptor.leading(bits),
      MatrixDescriptor.stride(bits),
      MatrixDescriptor.base(bits),
      Swizzle.fromOrdinal(((bits >>> 62) & 3L).toInt)
    )
  }

  /** A shared memory matrix descriptor: the matrix's start, its leading and stride dimension byte offsets, the base
    * offset of a swizzle pattern that starts off its boundary, and the swizzle.
    */
  case class Descriptor(start: Long, leading: Long, stride: Long, base: Int, swizzle: Swizzle) {

    /** The shared window offset and the bit within its byte of element (`mn`, `k`) of a matrix of `bits`-wide elements,
      * row `mn` of A or column `mn` of B, K-major or, `transposed`, MN-major.
      */
    // call sites name these arguments, which the argument checks enforce
    @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
    def locate(bits: Int, mn: Int, k: Int, transposed: Boolean): (address: Long, bit: Int) = {
      val elements = 128 / bits
      val width    = swizzle.width
      val offset   =
        if (!transposed) {
          val chunk = if (swizzle == Swizzle.Interleave) leading else 16L
          (mn % 8 * 16L * width + mn / 8 * stride + k / elements * chunk) * 8 + k % elements * bits
        } else if (swizzle == Swizzle.Interleave)
          (mn / elements * stride + k % 8 * 16L + k / 8 * leading) * 8 + mn % elements * bits
        else
          (mn / elements % width * 16L +
            mn /
            (elements * width) * leading + k % 8 * 16L * width + k / 8 * stride) * 8 +
            mn                               % elements * bits
      val address = start + offset / 8
      val phase   = ((address >>> 7) - base) & (width - 1)
      (address ^ (phase << 4), (offset % 8).toInt)
    }
  }

  /** The row and column of D a lane's accumulator element `index` holds: each warp's sixteen rows as `mma.m16n8`
    * fragments, one after another along N.
    */
  def accumulator(lane: Int, index: Int): (row: Int, column: Int) =
    (lane / 32 * 16 + lane % 32 / 4 + index / 2 % 2 * 8, index / 4 * 8 + lane % 4 * 2 + index % 2)

  /** The row and column of A, packed when A is sparse, a lane's register element `index` holds: each warp's sixteen
    * rows as the A fragment of `mma.m16n8`.
    */
  def multiplicand(element: Element, lane: Int, index: Int): (row: Int, column: Int) = {
    val chunk = if (element.bits < 32) element.packed.raw else 1
    (
      lane / 32 * 16 + lane % 32 / 4 + index / chunk % 2 * 8,
      lane                  % 4 * chunk + index      % chunk + index / (chunk * 2) * chunk * 4
    )
  }

  /** The registers of a lane's fragment of A: four, whatever the element. */
  val MultiplicandRegisters: Int = 4

  /** The registers of a lane's fragment of a D of `n` columns. */
  def accumulatorRegisters(n: Int, element: Element): Int = n * Rows / Lanes / element.packed

  /** Whether a warpgroup product of `n` columns of `d` from `a` and `b`, of depth `k`, dense or with a `sparse` A,
    * exists: a binary16 D only from half-precision or FP8 products, and integer and single-bit products only at N of 8,
    * 16, 24 or a multiple of 16.
    */
  def supports(n: Int, k: Int, a: Element, b: Element, d: Element, sparse: Boolean): Boolean = {
    import Element.*
    def depth(dense: Int): Boolean = k == (if (sparse) dense * 2 else dense)
    val fp8                        = Set(E4M3, E5M2)
    val bytes                      = Set(S8, U8)
    val integral                   = n == 8 || n == 16 || n == 24 || n % 16 == 0
    n >= 8 && n <= 256 && n % 8 == 0 && (
      a == F16 && b == F16 &&
        (d == F16 || d == F32) && depth(16) ||
        a == BF16 && b == BF16 && d == F32 && depth(16) ||
        a == TF32 && b == TF32 && d == F32 && depth(8) ||
        fp8(a) && fp8(b) &&
        (d == F16 || d == F32) && depth(32) ||
        bytes(a) && bytes(b) && d == S32 && integral && depth(32) ||
        a == B1 && b == B1 && d == S32 && integral && !sparse && depth(256)
    )
  }
}

package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Matrix.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** The matrix descriptor and canonical shared memory layouts of `wgmma.mma_async`, against the exact layouts the PTX
  * ISA gives for them (9.7.16.5.1.2.1.3), and the element arithmetic it adds to `mma`.
  */
class WarpgroupSuite extends munit.FunSuite {
  private def descriptor(leading: Long, stride: Long, swizzle: Int): Long =
    Descriptors.warpgroup(
      start = 0,
      leading = leading << 4,
      stride = stride << 4,
      swizzle = Warpgroup.Swizzle.fromOrdinal(swizzle)
    )

  /** The byte offsets of an `mn` by `k` block from the CuTe layout `((m...), (k...)):((m strides), (k strides))` of
    * `bits`-wide elements under Swizzle<`swizzle`,4,3>.
    */
  private def exact(bits: Int, mn: Vec[Int], mnStrides: Vec[Int], k: Vec[Int], kStrides: Vec[Int], swizzle: Int)(
      row: Int,
      column: Int
  ): Long = {
    def mode(coordinate: Int, shape: Vec[Int], strides: Vec[Int]): Int =
      shape.zip(strides).foldLeft((coordinate, 0)) { case ((rest, sum), (size, stride)) =>
        (rest / size, sum + rest % size * stride)
      }._2
    val offset = (mode(row, mn, mnStrides) + mode(column, k, kStrides)).toLong * bits / 8
    MatrixDescriptor.swizzled(offset, 16 << swizzle)
  }

  private def check(layout: Warpgroup.Descriptor, bits: Int, rows: Int, columns: Int, transposed: Boolean)(
      expected: (Int, Int) => Long
  )(using munit.Location): Unit =
    for {
      row    <- 0 until rows
      column <- 0 until columns
    } assertEquals(
      layout.locate(bits = bits, mn = row, k = column, transposed = transposed).address,
      expected(row, column),
      s"($row, $column)"
    )

  test("a descriptor holds bits 4 to 17 of its start and offsets, the base offset and the swizzle") {
    val bits = 0x123L | (0x45L << 16) | (0x67L << 32) | (5L << 49) | (2L << 62)
    assertEquals(
      Warpgroup.Descriptor(bits),
      Warpgroup.Descriptor(
        start = 0x1230L,
        leading = 0x450L,
        stride = 0x670L,
        base = 5,
        swizzle = Warpgroup.Swizzle.B64
      )
    )
    assertEquals(Warpgroup.Descriptor(3L << 62).swizzle, Warpgroup.Swizzle.B32)
    assertEquals(Warpgroup.Descriptor(1L << 62).swizzle, Warpgroup.Swizzle.B128)
  }

  test("K-major without a swizzle, tf32: ((8,2),(4,4)):((4,32),(1,64)) with LBO 16 and SBO 8") {
    check(
      Warpgroup.Descriptor(descriptor(leading = 16, stride = 8, swizzle = 0)),
      bits = 32,
      rows = 16,
      columns = 16,
      transposed = false
    )(
      exact(32, Vec(8, 2), Vec(4, 32), Vec(4, 4), Vec(1, 64), 0)
    )
  }

  test("K-major 32-byte swizzle, tf32: Swizzle<1,4,3> o ((8,2),(4,4)):((8,64),(1,4)) with SBO 16") {
    check(
      Warpgroup.Descriptor(descriptor(leading = 1, stride = 16, swizzle = 3)),
      bits = 32,
      rows = 16,
      columns = 8,
      transposed = false
    )(
      exact(32, Vec(8, 2), Vec(8, 64), Vec(4, 2), Vec(1, 4), 1)
    )
  }

  test("MN-major without a swizzle, bf16: ((8,1,2),(8,2)):((1,8,64),(8,128)) with LBO 16 and SBO 8") {
    check(
      Warpgroup.Descriptor(descriptor(leading = 16, stride = 8, swizzle = 0)),
      bits = 16,
      rows = 16,
      columns = 16,
      transposed = true
    )(
      exact(16, Vec(8, 1, 2), Vec(1, 8, 64), Vec(8, 2), Vec(8, 128), 0)
    )
  }

  test("MN-major 32-byte swizzle, bf16: Swizzle<1,4,3> o ((8,2,2),(8,2)):((1,8,128),(16,256)) with LBO 16, SBO 32") {
    check(
      Warpgroup.Descriptor(descriptor(leading = 16, stride = 32, swizzle = 3)),
      bits = 16,
      rows = 32,
      columns = 16,
      transposed = true
    )(
      exact(16, Vec(8, 2, 2), Vec(1, 8, 128), Vec(8, 2), Vec(16, 256), 1)
    )
  }

  test("MN-major 64-byte swizzle, bf16: Swizzle<2,4,3> o ((8,4,2),(8,2)):((1,8,256),(32,512)) with LBO 32, SBO 64") {
    check(
      Warpgroup.Descriptor(descriptor(leading = 32, stride = 64, swizzle = 2)),
      bits = 16,
      rows = 64,
      columns = 16,
      transposed = true
    )(
      exact(16, Vec(8, 4, 2), Vec(1, 8, 256), Vec(8, 2), Vec(32, 512), 2)
    )
  }

  test("a swizzle applies to the address a start within its pattern reaches, as advancing along K needs") {
    val layout = Warpgroup.Descriptor((0x420L >>> 4) | (64L << 32) | (1L << 62))
    // row 3 of a 128-byte pattern from 0x400: the second 32 bytes of K land in chunk 2 ^ 3
    assertEquals(layout.locate(bits = 16, mn = 3, k = 0, transposed = false).address, 0x400L + 3 * 128 + ((2 ^ 3) << 4))
  }

  test("the accumulator and register A fragments are mma.m16n8's, warp by warp along M") {
    assertEquals(Warpgroup.accumulator(lane = 0, index = 0), (row = 0, column = 0))
    assertEquals(Warpgroup.accumulator(lane = 37, index = 6), (row = 16 + 1 + 8, column = 8 + 2 + 0))
    assertEquals(
      Warpgroup.multiplicand(Matrix.Element.F16, lane = 127, index = 7),
      (row = 48 + 7 + 8, column = 6 + 1 + 8)
    )
    assertEquals(Warpgroup.multiplicand(Matrix.Element.TF32, lane = 65, index = 3), (row = 32 + 8, column = 1 + 4))
  }

  /** One f16 element of D from sixteen products of `a` and `b`, as the staged semantics compute it. */
  private def element(scale: Value, negateA: Boolean, negateB: Boolean)(c: Double, a: Double, b: Double): Double = {
    val shape                                  = Shape(64, 8, 16)
    val operation: Operation.WarpgroupMultiply = Operation.WarpgroupMultiply(
      Operation.Multiply(
        Fragment(shape, Role.A, Element.F16, wmma = false),
        Fragment(shape, Role.B, Element.F16, wmma = false, columnMajor = true),
        Fragment(shape, Role.C, Element.F32, wmma = false),
        Fragment(shape, Role.C, Element.F32, wmma = false),
        Vec.empty,
        Vec.empty,
        Vec.empty,
        Vec.empty,
        Matrix.accumulation(
          architecture = 90,
          shape = shape,
          input = Element.F16,
          result = Element.F32,
          rounding = ConversionRounding.RN
        )
      ),
      Vec(Value.Immediate(0L)),
      Vec(Value.Immediate(0L)),
      scale,
      negateA,
      negateB,
      transposeA = false,
      transposeB = false
    )
    val half   = IEEE754.floatToBinary16(_: Float).raw
    val inputs = IEEE754.fromDouble(c, 32) +: (0 until 16).flatMap(_ => Vec(half(a.toFloat), half(b.toFloat)))
    IEEE754.toDouble(
      WarpgroupSemantics.multiply(Execute, operation)(Probe(inputs*))(Vector.empty).head,
      32
    )
  }

  test("imm-scale-a and imm-scale-b negate A's and B's elements") {
    assertEquals(element(Value.Immediate(1L), negateA = false, negateB = false)(3.0, 1.0, 2.0), 35.0)
    assertEquals(element(Value.Immediate(1L), negateA = true, negateB = false)(3.0, 1.0, 2.0), -29.0)
    assertEquals(element(Value.Immediate(1L), negateA = true, negateB = true)(3.0, 1.0, 2.0), 35.0)
  }

  test("a false scale-d drops D as the addend") {
    assertEquals(element(Value.Immediate(0L), negateA = false, negateB = false)(3.0, 1.0, 2.0), 32.0)
  }
}

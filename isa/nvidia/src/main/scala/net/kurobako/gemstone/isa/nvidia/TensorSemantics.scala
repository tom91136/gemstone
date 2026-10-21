package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Matrix.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** NVIDIA matrix multiply-accumulate semantics, one element of D at a time. Integer products sum exactly and wrap or
  * saturate; binary64 ones chain fused multiply-adds; narrower floating-point ones widen exactly to binary64 and
  * accumulate in blocks as [[Accumulation.Blocked]] describes, with any NaN or infinity among the products and C
  * deciding the result as IEEE addition of them would.
  */
// inline bodies: each expansion sees one receiver class
@SuppressWarnings(Array("baumkuchen:megamorphic-call"))
private[isa] object TensorSemantics {

  /** Slots of [[MatrixOperands.hold]]: a block's products, then their exponents. Blocks hold at most 64 products. */
  private val Exponents = 64
  val Slots: Int        = 2 * Exponents

  /** The names of indexed intermediates, built once rather than for every product term of every element. */
  private class Indexed(prefix: String) {
    private val names             = Array.tabulate(256)(index => s"$prefix$index")
    def apply(index: Int): String = if (index < names.length) names(index) else s"$prefix$index"
  }

  private val xs = Indexed("x")
  private val ys = Indexed("y")
  private val fs = Indexed("f")
  private val ps = Indexed("p")
  private val gs = Indexed("g")
  private val ss = Indexed("s")

  /** The products a row of A contributes: half of B's rows when A is sparse. */
  private def terms(operation: Operation.Multiply): Int =
    if (operation.sparsity.isDefined) operation.b.rows / 2 else operation.b.rows

  /** How a sparse A's packed elements find theirs in B: the packed elements per chunk, the chunk's width in B and the
    * element's bits.
    */
  class Sparse(element: Element) {
    val packed: Int = packedPerChunk(element)
    val width: Int  = chunkWidth(element)
    val bits: Int   = element.bits
  }

  def sparse(operation: Operation.Multiply): Option[Sparse] =
    Option.when(operation.sparsity.isDefined)(Sparse(operation.a.element))

  /** Element `index` of the row of A, negated where `negate` says. */
  inline def multiplicandA[V](
      inline algebra: Algebra[V],
      element: Element,
      operands: MatrixOperands[V],
      negate: Boolean,
      index: Int
  ): V = if (negate) negation(algebra, element, operands.a(index)) else operands.a(index)

  /** Element `index` of the column of B, negated where `negate` says. */
  inline def multiplicandB[V](
      inline algebra: Algebra[V],
      element: Element,
      operands: MatrixOperands[V],
      negate: Boolean,
      index: Int
  ): V = if (negate) negation(algebra, element, operands.b(index)) else operands.b(index)

  /** An element of a floating-point multiplicand negated, its sign flipped. */
  inline def negation[V](inline algebra: Algebra[V], element: Element, value: V): V = element match {
    case Element.F16                     => algebra.primitive(value)((x: Float16) => ProductArithmetic.negate(x))
    case Element.BF16                    => algebra.primitive(value)((x: BFloat16) => ProductArithmetic.negate(x))
    case Element.TF32                    => algebra.primitive(value)((x: TensorFloat32) => ProductArithmetic.negate(x))
    case Element.E4M3                    => algebra.primitive(value)((x: Float8E4M3) => ProductArithmetic.negate(x))
    case Element.E3M2                    => algebra.primitive(value)((x: Float6E3M2) => ProductArithmetic.negate(x))
    case Element.E2M3                    => algebra.primitive(value)((x: Float6E2M3) => ProductArithmetic.negate(x))
    case Element.E2M1 | Element.E2M1Byte =>
      algebra.primitive(value)((x: Float4E2M1) => ProductArithmetic.negate(x))
    case _ => algebra.primitive(value)((x: Float8E5M2) => ProductArithmetic.negate(x))
  }

  /** The element of B's column, by `b`, that packed element `index` of A's row multiplies, A being dense without
    * `sparsity`.
    */
  inline def selected[V](
      inline algebra: Algebra[V],
      sparsity: Option[Sparse],
      operands: MatrixOperands[V],
      inline b: Int => V
  )(index: Int): V = sparsity match {
    case None         => b(index)
    case Some(sparse) =>
      val base               = index / sparse.packed * sparse.width
      val pair               = if (sparse.bits == 4) index % 2 else 0
      val choice             = operands.selection(index)
      def is(value: Long): V = algebra.equal(choice, algebra.literal(value))
      if (sparse.bits == 32)
        algebra.select(algebra.lessThan(choice, algebra.literal(2L), signed = false), b(base), b(base + 1))
      else {
        val step = if (sparse.bits == 4) 2 else 1
        algebra.select(
          is(0L),
          b(base + pair),
          algebra.select(
            is(1L),
            b(base + step + pair),
            algebra.select(is(2L), b(base + 2 * step + pair), b(base + 3 * step + pair))
          )
        )
      }
  }

  inline def integral[V](inline algebra: Algebra[V], element: Element, value: V): V = element match {
    case Element.S8 => algebra.primitive(value)((x: SInt[8]) => TensorArithmetic.widen(x))
    case Element.U8 => algebra.primitive(value)((x: UInt[8]) => TensorArithmetic.widen(x))
    case Element.S4 => algebra.primitive(value)((x: SInt[4]) => TensorArithmetic.widen(x))
    case _          => algebra.primitive(value)((x: UInt[4]) => TensorArithmetic.widen(x))
  }

  /** Integer and single-bit products, summed exactly from C on; saturating sums clamp after each chunk. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def integer[V](
      inline algebra: Algebra[V],
      operation: Operation.Multiply,
      chunk: Int,
      operands: MatrixOperands[V],
      addend: V,
      negateA: Boolean,
      negateB: Boolean
  ): V = {
    def left(index: Int): V  = multiplicandA(algebra, operation.a.element, operands, negateA, index)
    def right(index: Int): V = multiplicandB(algebra, operation.b.element, operands, negateB, index)
    val sparsity             = sparse(operation)
    var sum                  = operands.bind("s", algebra.primitive(addend)((x: SInt[32]) => TensorArithmetic.widen(x)))
    operation.bits match {
      case Some(bits) =>
        var segment = 0
        while (segment < operation.b.rows / 32) {
          sum = algebra.add(
            sum,
            algebra.primitive(left(segment), right(segment))((x: UInt[32], y: UInt[32]) =>
              TensorArithmetic.population(x, y, bits)
            )
          )
          segment += 1
        }
      case None =>
        var index = 0
        while (index < terms(operation)) {
          sum = operands.bind(
            "s",
            algebra.add(
              sum,
              algebra.multiply(
                integral(algebra, operation.a.element, left(index)),
                integral(algebra, operation.b.element, selected(algebra, sparsity, operands, right(_))(index))
              )
            )
          )
          index += 1
          if (operation.saturate && index % chunk == 0 && index < terms(operation))
            sum = operands.bind("s", algebra.primitive(sum)(TensorArithmetic.clamp))
        }
    }
    if (operation.saturate) algebra.primitive(sum)(TensorArithmetic.saturate)
    else algebra.primitive(sum)(TensorArithmetic.wrap)
  }

  /** Binary64 products, each fused with the sum from C on. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def fused[V](
      inline algebra: Algebra[V],
      operation: Operation.Multiply,
      rounding: VMIR.ConversionRounding,
      operands: MatrixOperands[V]
  ): V = {
    val sparsity = sparse(operation)
    var sum      = operands.c
    var index    = 0
    while (index < terms(operation)) {
      sum = operands.bind(
        "s",
        algebra.primitive(operands.a(index), selected(algebra, sparsity, operands, operands.b)(index), sum)(
          (x: Float64, y: Float64, z: Float64) => TensorArithmetic.fused(x, y, z, rounding)
        )
      )
      index += 1
    }
    sum
  }

  /** `value`, an element of `element`'s format, widened exactly to binary64. */
  inline def widen[V](inline algebra: Algebra[V], element: Element, value: V): V = element match {
    case Element.F16                     => algebra.primitive(value)((x: Float16) => TensorArithmetic.widen(x))
    case Element.F32                     => algebra.primitive(value)((x: Float32) => TensorArithmetic.widen(x))
    case Element.BF16                    => algebra.primitive(value)((x: BFloat16) => TensorArithmetic.widen(x))
    case Element.TF32                    => algebra.primitive(value)((x: TensorFloat32) => TensorArithmetic.widen(x))
    case Element.E4M3                    => algebra.primitive(value)((x: Float8E4M3) => TensorArithmetic.widen(x))
    case Element.E5M2                    => algebra.primitive(value)((x: Float8E5M2) => TensorArithmetic.widen(x))
    case Element.E3M2                    => algebra.primitive(value)((x: Float6E3M2) => TensorArithmetic.widen(x))
    case Element.E2M3                    => algebra.primitive(value)((x: Float6E2M3) => TensorArithmetic.widen(x))
    case Element.E2M1 | Element.E2M1Byte => algebra.primitive(value)((x: Float4E2M1) => TensorArithmetic.widen(x))
    case _                               => value
  }

  /** The least normal exponent of an element format, the lowest a factor aligns by. */
  def minimum(element: Element): Int = element match {
    case Element.F16 | Element.E5M2      => -14
    case Element.E4M3                    => -6
    case Element.E3M2                    => -2
    case Element.E2M3 | Element.E2M1Byte => 0
    case Element.E2M1                    => 0
    case _                               => -126
  }

  inline def scale[V](inline algebra: Algebra[V], format: ScaleFormat, value: V): V = format match {
    case ScaleFormat.UE8M0 => algebra.primitive(value)((x: ScaleE8M0) => TensorArithmetic.scale(x))
    case ScaleFormat.UE4M3 => algebra.primitive(value)((x: Float8E4M3) => TensorArithmetic.scale(x))
  }

  /** The per-instruction constants of [[blocked]], computed once rather than for every element of D. */
  class BlockedProduct(operation: Operation.Multiply, val accumulation: Accumulation.Blocked) {
    val c: Element                   = operation.c.element
    val d: Element                   = operation.d.element
    val left: Element                = operation.a.element
    val right: Element               = operation.b.element
    val accumulator: Element         = Matrix.accumulator(c, d)
    val half: Boolean                = accumulator == Element.F16
    val count: Int                   = terms(operation)
    val per: Int                     = operation.scaling.fold(count)(scaling => count / scaling.vector)
    val scaling: Option[ScaleFormat] = operation.scaling.map(_.format)
    val minimumX: Int                = minimum(left)
    val minimumY: Int                = minimum(right)
    val sparsity: Option[Sparse]     = sparse(operation)
  }

  /** Floating-point products accumulated in blocks from C on, each block's sum rounded to the accumulator format. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def blocked[V](
      inline algebra: Algebra[V],
      blocks: BlockedProduct,
      operands: MatrixOperands[V],
      addend: V,
      negateA: Boolean,
      negateB: Boolean
  ): V = {
    def left(index: Int): V  = multiplicandA(algebra, blocks.left, operands, negateA, index)
    def right(index: Int): V = multiplicandB(algebra, blocks.right, operands, negateB, index)
    val accumulation         = blocks.accumulation
    val extra                = accumulation.extra
    val fraction             = accumulation.fraction.raw
    val nearest              = accumulation.nearest
    var special              = operands.bind("z", widen(algebra, blocks.c, addend))
    var sum                  = if (accumulation.late) algebra.literal(0L) else addend
    var format               = if (accumulation.late) blocks.accumulator else blocks.c
    var start                = 0
    while (start < blocks.count) {
      val end     = math.min(start + accumulation.size, blocks.count)
      val running = operands.bind("w", widen(algebra, format, sum))
      var maximum = algebra.maximum(
        algebra.literal(accumulation.floor.toLong),
        algebra.primitive(running)((x: Float64) => TensorArithmetic.exponent(x, -126)),
        signed = true
      )
      var index = start
      while (index < end) {
        val x = operands.bind(xs(index), widen(algebra, blocks.left, left(index)))
        val y = operands.bind(
          ys(index),
          widen(algebra, blocks.right, selected(algebra, blocks.sparsity, operands, right(_))(index))
        )
        val minimumX = blocks.minimumX
        val minimumY = blocks.minimumY
        val exponent = algebra.add(
          algebra.primitive(x)((p: Float64) => TensorArithmetic.exponent(p, minimumX)),
          algebra.primitive(y)((q: Float64) => TensorArithmetic.exponent(q, minimumY))
        )
        val product   = algebra.primitive(x, y)(TensorArithmetic.product)
        var term      = product
        var alignment = exponent
        blocks.scaling match {
          case Some(scaleFormat) =>
            val factor = operands.bind(
              fs(index),
              algebra.primitive(
                scale(algebra, scaleFormat, operands.scaleA(index / blocks.per)),
                scale(algebra, scaleFormat, operands.scaleB(index / blocks.per))
              )(TensorArithmetic.product)
            )
            term = algebra.primitive(product, factor)(TensorArithmetic.product)
            alignment = algebra.primitive(exponent, factor)(TensorArithmetic.scaled)
          case None =>
        }
        val held = operands.hold(index - start, ps(index), term)
        special = operands.bind("z", algebra.primitive(special, held)(TensorArithmetic.special))
        maximum =
          algebra.maximum(maximum, operands.hold(Exponents + index - start, gs(index), alignment), signed = true)
        index += 1
      }
      val largest = operands.bind("m", maximum)
      var total = algebra.primitive(running, largest)((x: Float64, m: SInt[64]) => TensorArithmetic.align(x, m, extra))
      index = start
      while (index < end) {
        total = algebra.add(
          total,
          algebra.primitive(operands.held(index - start), largest)((x: Float64, m: SInt[64]) =>
            TensorArithmetic.align(x, m, extra)
          )
        )
        index += 1
      }
      val aligned = operands.bind("t", total)
      sum = operands.bind(
        "s",
        if (blocks.half)
          algebra.primitive(aligned, largest, running)((t: SInt[64], m: SInt[64], w: Float64) =>
            TensorArithmetic.roundHalf(t, m, w, extra, fraction, nearest)
          )
        else
          algebra.primitive(aligned, largest, running)((t: SInt[64], m: SInt[64], w: Float64) =>
            TensorArithmetic.round(t, m, w, extra, fraction, nearest)
          )
      )
      format = blocks.accumulator
      start = end
    }
    if (blocks.half) {
      val total =
        if (accumulation.late) algebra.primitive(sum, addend)((s: Float16, c: Float16) => FloatArithmetic.add(s, c))
        else sum
      algebra.primitive(total, special)((s: Float16, z: Float64) => TensorArithmetic.finish(s, z))
    } else {
      val total =
        if (accumulation.late) algebra.primitive(sum, addend)((s: Float32, c: Float32) => FloatArithmetic.add(s, c))
        else sum
      val finished = algebra.primitive(total, special)((s: Float32, z: Float64) => TensorArithmetic.finish(s, z))
      if (blocks.d == Element.F16)
        algebra.primitive(finished)((s: Float32) => Conversions.toFloating(s, VMIR.ConversionRounding.RN, 16))
      else finished
    }
  }

  /** Half-precision `m8n8k4` as ptxas emulates it with binary32 fused multiply-adds. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def emulated[V](inline algebra: Algebra[V], operation: Operation.Multiply, operands: MatrixOperands[V]): V = {
    def single(name: String, value: V): V =
      operands.bind(
        name,
        algebra.primitive(value)((x: Float16) => Conversions.toFloating(x, VMIR.ConversionRounding.RN, 32))
      )
    val c                             = if (operation.c.element == Element.F16) single("w", operands.c) else operands.c
    def fma(index: Int, addend: V): V =
      algebra.primitive(single(xs(index), operands.a(index)), single(ys(index), operands.b(index)), addend)(
        (x: Float32, y: Float32, z: Float32) => FloatArithmetic.fusedMultiplyAdd(x, y, z, VMIR.ConversionRounding.RN)
      )
    if (operation.d.element == Element.F16) {
      def pair(index: Int): V = operands.bind(
        ss(index),
        fma(
          index + 1,
          algebra.primitive(operands.held(index), operands.held(index + 1))((x: Float32, y: Float32) =>
            FloatArithmetic.multiply(x, y)
          )
        )
      )
      val _      = operands.hold(0, "x0", single("x0", operands.a(0)))
      val _      = operands.hold(1, "y0", single("y0", operands.b(0)))
      val first  = pair(0)
      val _      = operands.hold(2, "x2", single("x2", operands.a(2)))
      val _      = operands.hold(3, "y2", single("y2", operands.b(2)))
      val second = pair(2)
      val sum   = operands.bind("s", algebra.primitive(c, first)((x: Float32, y: Float32) => FloatArithmetic.add(x, y)))
      val total = algebra.primitive(sum, second)((x: Float32, y: Float32) => FloatArithmetic.add(x, y))
      algebra.primitive(total)((x: Float32) => Conversions.toFloating(x, VMIR.ConversionRounding.RN, 16))
    } else {
      var sum   = algebra.literal(0L)
      var index = 0
      while (index < 4) {
        sum = operands.bind("s", fma(index, sum))
        index += 1
      }
      algebra.primitive(c, sum)((x: Float32, y: Float32) => FloatArithmetic.add(x, y))
    }
  }
}

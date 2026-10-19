package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.annotation.*

/** Operands that probe staged semantics at their edges, and comparisons against the kernels they replaced. */
object StagedTesting {

  val booleans: Seq[Boolean]                = Seq(false, true)
  val floatWidths: Seq[Int in (16, 32, 64)] = Seq(16, 32, 64)
  val integerWidths: Seq[Int in 1 ~ 64]     = Seq(8, 16, 32, 64)

  private val random = scala.util.Random(0x5eed)

  /** Encodings at `width` bits: zeros, units, subnormals, extremes, infinities, NaNs and random patterns. */
  def floats(width: Int in (16, 32, 64)): Vector[Long] = {
    val sign     = 1L << (width.raw - 1)
    val infinity = IEEE754.infinity(width)
    val fraction = (sign - 1L) & ~infinity
    val values   = Vector(
      0.0,
      1.0,
      0.5,
      2.0,
      3.0,
      0.25,
      1.5,
      2.5,
      10.0,
      100.0,
      1e-3,
      Math.PI,
      7.75,
      0.999,
      1e10,
      65504.0,
      65520.0,
      1e-7,
      0.75,
      4.5,
      5.5,
      1e300,
      1e-300,
      1.0000001,
      0.49999997,
      123456.789,
      1.0 / 3.0
    )
      .map(IEEE754.fromDouble(_, width))
    val bits = Vector(
      1L,
      fraction,
      fraction + 1L,
      infinity - 1L,
      infinity,
      infinity + 1L,
      infinity | (fraction >> 1),
      infinity | fraction,
      infinity | (fraction >> 1) + 1L
    )
    val positive = (values ++ bits).distinct
    val randoms  = Vector.fill(48)(Bits.truncate(random.nextLong(), width))
    (positive ++ positive.map(_ | sign) ++ randoms).distinct
  }

  /** Values at `width` bits: zero, one, the extremes of both signednesses and random patterns. */
  def integers(width: Int in 1 ~ 64): Vector[Long] = {
    val mask = Bits.mask(width)
    val top  = 1L << (width.raw - 1)
    (Vector(0L, 1L, 2L, 3L, 7L, 8L, 31L, 32L, 33L, 63L, 64L, 65L, 0x55L, mask, mask - 1L, top, top - 1L, top + 1L) ++
      Vector.fill(24)(random.nextLong())).map(_ & mask).distinct
  }

  def toDouble(width: Int in (16, 32, 64), bits: Long): Double    = IEEE754.toDouble(bits, width)
  def fromDouble(width: Int in (16, 32, 64), value: Double): Long = IEEE754.fromDouble(value, width)

  /** Inputs of `inputs` at which `actual` and `expected` disagree, as shown by `show`. */
  def mismatches[A, B](inputs: Iterable[A], actual: A => B, expected: A => B)(using CanEqual[B, B]): Vector[String] =
    mismatches(inputs, actual, expected, (found, wants) => found == wants)

  def mismatches[A, B](
      inputs: Iterable[A],
      actual: A => B,
      expected: A => B,
      agree: (B, B) => Boolean
  ): Vector[String] = inputs.view.flatMap { input =>
    val found = actual(input)
    val wants = expected(input)
    Option.when(!agree(found, wants))(s"$input: staged $found, reference $wants")
  }.take(5).toVector

  /** A binary64 accumulator from zero through `step` over `lanes` lanes. */
  def sumOver(lanes: Int)(step: (Double, Int) => Double): Double = {
    @tailrec def loop(lane: Int, sum: Double): Double = if (lane == lanes) sum else loop(lane + 1, step(sum, lane))
    loop(0, 0.0)
  }

  /** An accumulator of encoded bits from `initial` through `step` over `lanes` lanes. */
  def sumBits(lanes: Int, initial: Long)(step: (Long, Int) => Long): Long = {
    @tailrec def loop(lane: Int, sum: Long): Long = if (lane == lanes) sum else loop(lane + 1, step(sum, lane))
    loop(0, initial)
  }

  trait Kernel3 {
    def apply(a: Long, b: Long, c: Long): Long
    def tupled: ((Long, Long, Long)) => Long = triple => apply(triple._1, triple._2, triple._3)
  }
  trait Kernel4    { def apply(a: Long, b: Long, c: Long, d: Long): Long }
  trait Fold       { def apply(lanes: Seq[Long]): Long                   }
  trait BinaryFold {
    def apply(left: Seq[Long], right: Seq[Long]): Long
    def tupled: ((Seq[Long], Seq[Long])) => Long = lanes => apply(lanes._1, lanes._2)
  }
  trait AccumulatedFold {
    def apply(left: Seq[Long], right: Seq[Long], addend: Long): Long
    def tupled: ((Seq[Long], Seq[Long], Long)) => Long = lanes => apply(lanes._1, lanes._2, lanes._3)
  }
  trait Decomposition { def apply(a: Long): (Long, Long) }

  extension (closure: Closures.Closure) {
    def asUnary: Long => Long                      = a => closure(Seq(a)).head
    def asBinary: (Long, Long) => Long             = (a, b) => closure(Seq(a, b)).head
    def asTernary: Kernel3                         = (a, b, c) => closure(Seq(a, b, c)).head
    def asQuaternary: Kernel4                      = (a, b, c, d) => closure(Seq(a, b, c, d)).head
    def asPair: Decomposition                      = a => pair(closure(Seq(a)))
    def asBinaryPair: (Long, Long) => (Long, Long) = (a, b) => pair(closure(Seq(a, b)))
    def asFold: Fold                               = lanes => closure(lanes).head
    def asBinaryFold: BinaryFold                   = (left, right) => closure(left ++ right).head
    def asAccumulatedFold: AccumulatedFold         = (left, right, addend) =>
      closure(left ++ right :+ addend).head
  }

  private def pair(results: Seq[Long]): (Long, Long) = (results(0), results(1))

  def pairs(values: Seq[Long]): Seq[(Long, Long)] = for {
    a <- values
    b <- values
  } yield (a, b)

  /** A deterministic sample of triples, every value appearing in each position. */
  def triples(values: Seq[Long], count: Int = 4000): Seq[(Long, Long, Long)] =
    Seq.tabulate(count)(index =>
      (
        values(index                         % values.size),
        values((index / values.size + index) % values.size),
        values(random.nextInt(values.size))
      )
    )
}

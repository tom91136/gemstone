package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** SPV_INTEL_subgroup_matrix_multiply_accumulate's per-subgroup product. Each of the N lanes passes a part of A, a
  * column of B and a column of C, packed as the extension maps them, and receives its column of the result.
  * Floating-point products are exact in binary64 and sum there with C before one rounding; integer products sum at 64
  * bits and wrap to the result.
  */
private object SubgroupMatrixMultiply {

  enum Kind derives CanEqual {
    case Signed, Unsigned, Half, BFloat16, Single, TensorFloat32, Double
  }

  object Element {
    def of(code: Long): Element = Element(Kind.fromOrdinal((code >>> 8).toInt), (code & 0xff).toInt)
  }

  /** How `bits`-wide elements of an operand read. */
  case class Element(kind: Kind, bits: Int) {
    def floating: Boolean = kind != Kind.Signed && kind != Kind.Unsigned

    def code: Long = kind.ordinal.toLong << 8 | bits.toLong

    def read(raw: Long): Double = {
      val value = raw & Bits.mask(bits.unsafe[Int in 1 ~ 64])
      kind match {
        case Kind.Signed        => Bits.signExtend(value, bits.unsafe[Int in 1 ~ 64]).toDouble
        case Kind.Unsigned      => value.toDouble
        case Kind.Half          => IEEE754.toDouble(value, 16)
        case Kind.BFloat16      => Minifloat.BFloat16.toDouble(value)
        case Kind.Single        => IEEE754.toDouble(value, 32)
        case Kind.TensorFloat32 => IEEE754.toDouble(value & ~0x1fffL, 32)
        case Kind.Double        => IEEE754.toDouble(value, 64)
      }
    }

    def integer(raw: Long): Long = kind match {
      case Kind.Signed => Bits.signExtend(raw, bits.unsafe[Int in 1 ~ 64])
      case _           => raw & Bits.mask(bits.unsafe[Int in 1 ~ 64])
    }

    def write(value: Double): Long = kind match {
      case Kind.Half     => IEEE754.fromDouble(value, 16)
      case Kind.BFloat16 => Minifloat.BFloat16.fromDouble(value)
      case Kind.Single   => IEEE754.fromDouble(value, 32)
      case _             => IEEE754.fromDouble(value, 64)
    }
  }

  object Dimensions {
    def of(selector: Vec[Long]): Dimensions = Dimensions(
      inner = selector(0).toInt,
      a = Element.of(selector(1)),
      b = Element.of(selector(2)),
      c = Element.of(selector(3)),
      result = Element.of(selector(4)),
      aComponent = selector(5).toInt,
      bComponent = selector(6).toInt
    )
  }

  /** The product's shape: K, the elements of A, B, C and the result, and the component widths A and B are packed in. */
  case class Dimensions(
      inner: Int,
      a: Element,
      b: Element,
      c: Element,
      result: Element,
      aComponent: Int,
      bComponent: Int
  ) {
    def selector: Vec[Long] =
      Vec(inner.toLong, a.code, b.code, c.code, result.code, aComponent.toLong, bComponent.toLong)
  }

  /** Each lane's result column, from the lanes' (subgroup-local id, A, B, C) contributions. */
  def apply(shape: Dimensions, lanes: Vec[(Int, Array[Long], Array[Long], Array[Long])], subgroup: Int)
      : Map[Int, Array[Long]] = {
    val rows   = lanes.headOption.fold(0)(_._4.length)
    val inner  = shape.inner
    val a      = Array.ofDim[Long](rows, inner)
    val perA   = shape.aComponent / shape.a.bits
    val total  = subgroup.toLong * perA
    val spread = math.max(1L, total / inner)
    lanes.foreach { (lane, parts, _, _) =>
      parts.indices.foreach(component =>
        (0 until perA).foreach { part =>
          val flat = lane.toLong * perA + part
          val row  = component * spread + flat / inner
          if (flat < total && row < rows && flat / inner < spread)
            a(row.toInt)((flat % inner).toInt) = parts(component) >>> (part * shape.a.bits)
        }
      )
    }
    val perB                                = shape.bComponent / shape.b.bits
    def b(parts: Array[Long], k: Int): Long = parts(k / perB) >>> ((k % perB) * shape.b.bits)
    if (shape.result.floating) {
      val left = a.map(_.map(shape.a.read(_)))
      lanes.map { (lane, _, parts, addend) =>
        val right = Array.tabulate(inner)(k => shape.b.read(b(parts, k)))
        lane -> Array.tabulate(rows)(row => shape.result.write(sum(shape.c.read(addend(row)), left(row), right)))
      }.toMap
    } else {
      val left = a.map(_.map(shape.a.integer(_)))
      val mask = Bits.mask(shape.result.bits.unsafe[Int in 1 ~ 64])
      lanes.map { (lane, _, parts, addend) =>
        val right = Array.tabulate(inner)(k => shape.b.integer(b(parts, k)))
        lane -> Array.tabulate(rows)(row => sum(shape.c.integer(addend(row)), left(row), right) & mask)
      }.toMap
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def sum(initial: Double, left: Array[Double], right: Array[Double]): Double = {
    var total = initial
    var k     = 0
    while (k < right.length) {
      total += left(k) * right(k)
      k += 1
    }
    total
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def sum(initial: Long, left: Array[Long], right: Array[Long]): Long = {
    var total = initial
    var k     = 0
    while (k < right.length) {
      total += left(k) * right(k)
      k += 1
    }
    total
  }
}

package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The lane layout of matrices, which hold their columns in order, one lane per element. */
private[isa] object Matrices {

  /** The source lane of `lane` in the transpose of a `rows` by `columns` matrix. */
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def transposed(lane: Int, rows: Int, columns: Int): Int = (lane % columns) * rows + lane / columns

  private def widened(bits: Long, width: Long): Long =
    java.lang.Double.doubleToRawLongBits(IEEE754.toDouble(bits, width.toInt.unsafe[Int in (16, 32, 64)]))

  private def narrowed(bits: Long, width: Long): Long =
    IEEE754.fromDouble(java.lang.Double.longBitsToDouble(bits), width.toInt.unsafe[Int in (16, 32, 64)])

  private def doubled(bits: Long): Double = java.lang.Double.longBitsToDouble(bits)

  private def raw(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  private def sum(left: Long, right: Long): Long = raw(doubled(left) + doubled(right))

  private def difference(left: Long, right: Long): Long = raw(doubled(left) - doubled(right))

  private def product(left: Long, right: Long): Long = raw(doubled(left) * doubled(right))

  private def quotient(left: Long, right: Long): Long = raw(doubled(left) / doubled(right))

  private def negated(bits: Long): Long = raw(-doubled(bits))

  /** The element of `width` bits in the double precision the determinant and inverse compute in. */
  val Widen: Primitive[Primitive.Binary] = Primitive("net.kurobako.gemstone.isa.spirv.Matrices.widen", widened(_, _))

  /** The double precision value as an element of `width` bits. */
  val Narrow: Primitive[Primitive.Binary] = Primitive("net.kurobako.gemstone.isa.spirv.Matrices.narrow", narrowed(_, _))

  val Add: Primitive[Primitive.Binary] = Primitive("net.kurobako.gemstone.isa.spirv.Matrices.add", sum(_, _))

  val Subtract: Primitive[Primitive.Binary] =
    Primitive("net.kurobako.gemstone.isa.spirv.Matrices.subtract", difference(_, _))

  val Multiply: Primitive[Primitive.Binary] =
    Primitive("net.kurobako.gemstone.isa.spirv.Matrices.multiply", product(_, _))

  val Divide: Primitive[Primitive.Binary] = Primitive("net.kurobako.gemstone.isa.spirv.Matrices.divide", quotient(_, _))

  val Negate: Primitive[Primitive.Unary] = Primitive("net.kurobako.gemstone.isa.spirv.Matrices.negate", negated(_))

  def widen(bits: UInt[64], width: UInt[64]): UInt[64] = fromRaw[UInt[64]](widened(toRaw(bits), toRaw(width)))

  def narrow(bits: UInt[64], width: UInt[64]): UInt[64] = fromRaw[UInt[64]](narrowed(toRaw(bits), toRaw(width)))

  def add(left: UInt[64], right: UInt[64]): UInt[64] = fromRaw[UInt[64]](sum(toRaw(left), toRaw(right)))

  def subtract(left: UInt[64], right: UInt[64]): UInt[64] = fromRaw[UInt[64]](difference(toRaw(left), toRaw(right)))

  def multiply(left: UInt[64], right: UInt[64]): UInt[64] = fromRaw[UInt[64]](product(toRaw(left), toRaw(right)))

  def divide(left: UInt[64], right: UInt[64]): UInt[64] = fromRaw[UInt[64]](quotient(toRaw(left), toRaw(right)))

  def negate(bits: UInt[64]): UInt[64] = fromRaw[UInt[64]](negated(toRaw(bits)))
}

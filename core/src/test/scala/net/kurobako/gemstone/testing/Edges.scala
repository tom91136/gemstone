package net.kurobako.gemstone.testing

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import scala.util.Random

/** Operand values as execution holds them, favouring boundaries, and the comparison that treats NaNs as equal. */
object Edges {

  /** The encodings at `width` bits of zeros, units, infinities, a quiet NaN, the smallest subnormal and the largest
    * finite value.
    */
  def specials(width: Int in (16, 32, 64)): Vec[Long] = {
    val sign     = IEEE754.signBit(width)
    val infinity = IEEE754.infinity(width)
    val one      = IEEE754.one(width)
    Vec(0L, sign, one, one | sign, infinity, infinity | sign, infinity | IEEE754.quietBit(width), 1L, infinity - 1L)
  }

  def sample(tpe: Type, random: Random): Long = tpe match {
    case Type.Bool              => if (random.nextBoolean()) 1L else 0L
    case Type.Integer(width, _) =>
      val bits  = width
      val value = random.nextInt(8) match {
        case 0 => 0L
        case 1 => 1L
        case 2 => -1L
        case 3 => 1L << (bits - 1)
        case 4 => (1L << (bits - 1)) - 1L
        case _ => random.nextLong()
      }
      tpe.represent(value)
    case Type.Encoded(format) =>
      val code = tpe.represent(random.nextLong())
      if (random.nextInt(3) == 0) Vec(0L, format.maximum, format.nan, code)(random.nextInt(4)) else code
    case Type.Float(width) =>
      val edges = specials(width.unsafe[Int in (16, 32, 64)])
      if (random.nextInt(3) == 0) edges(random.nextInt(edges.size)) else tpe.represent(random.nextLong())
    case Type.Ranged(_, intervals) =>
      val (low, high) = intervals(random.nextInt(intervals.size))
      low + Math.floorMod(random.nextLong(), high - low + 1)
  }

  private def nan(tpe: Type, value: Long): Boolean = tpe match {
    case Type.Float(width) if width == 16 || width == 32 || width == 64 =>
      IEEE754.isNaN(value, width.unsafe[Int in (16, 32, 64)])
    case Type.Encoded(format) => format.isNaN(value)
    case _                    => false
  }

  /** Equal bits, or NaNs of `tpe` on both sides. */
  def same(tpe: Type)(expected: Long, obtained: Long): Boolean =
    expected == obtained || (nan(tpe, expected) && nan(tpe, obtained))

  /** Equal bits, or NaNs at `width` bits on both sides. */
  def same(width: Int in (16, 32, 64))(left: Long, right: Long): Boolean =
    left == right || (IEEE754.isNaN(left, width) && IEEE754.isNaN(right, width))
}

package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.implicitNotFound
import scala.compiletime.summonInline

object Type {

  /** The prelude type of a narrow encoding. */
  def name(format: Minifloat): String = format match {
    case Minifloat.BFloat16      => "BFloat16"
    case Minifloat.TensorFloat32 => "TensorFloat32"
    case Minifloat.E4M3          => "Float8E4M3"
    case Minifloat.E5M2          => "Float8E5M2"
    case Minifloat.E4M3FNUZ      => "Float8E4M3FNUZ"
    case Minifloat.E5M2FNUZ      => "Float8E5M2FNUZ"
    case Minifloat.E2M3          => "Float6E2M3"
    case Minifloat.E3M2          => "Float6E3M2"
    case Minifloat.E2M1          => "Float4E2M1"
    case Minifloat.E8M0          => "ScaleE8M0"
  }

  /** A width the caller knows is in range. */
  def width(bits: Int): Int in 1 ~ 64 = bits.unsafe[Int in 1 ~ 64]

  def unsigned(width: Int in 1 ~ 64): Type = Integer(width, signed = false)
  def signed(width: Int in 1 ~ 64): Type   = Integer(width, signed = true)

  /** The type rendered semantics give `T`, which must be a prelude type. */
  inline def of[T]: Type = summonInline[Reified[T]].tpe

  object Reified {
    given Reified[Boolean]                                              = Reified(Bool)
    given unsigned[W <: Int](using width: ValueOf[W]): Reified[UInt[W]] =
      Reified(Type.unsigned(Type.width(width.value)))
    given signed[W <: Int](using width: ValueOf[W]): Reified[SInt[W]] = Reified(Type.signed(Type.width(width.value)))
    given Reified[Float16]                                            = Reified(Float(Type.width(16)))
    given Reified[Float32]                                            = Reified(Float(Type.width(32)))
    given Reified[Float64]                                            = Reified(Float(Type.width(64)))
    given Reified[BFloat16]                                           = Reified(Encoded(Minifloat.BFloat16))
    given Reified[TensorFloat32]                                      = Reified(Encoded(Minifloat.TensorFloat32))
    given Reified[Float8E4M3]                                         = Reified(Encoded(Minifloat.E4M3))
    given Reified[Float8E5M2]                                         = Reified(Encoded(Minifloat.E5M2))
    given Reified[Float8E4M3FNUZ]                                     = Reified(Encoded(Minifloat.E4M3FNUZ))
    given Reified[Float8E5M2FNUZ]                                     = Reified(Encoded(Minifloat.E5M2FNUZ))
    given Reified[Float6E2M3]                                         = Reified(Encoded(Minifloat.E2M3))
    given Reified[Float6E3M2]                                         = Reified(Encoded(Minifloat.E3M2))
    given Reified[Float4E2M1]                                         = Reified(Encoded(Minifloat.E2M1))
    given Reified[ScaleE8M0]                                          = Reified(Encoded(Minifloat.E8M0))
    given floating[W <: Int](using width: ValueOf[W]): Reified[FloatingPoint[W]] =
      Reified(Float(Type.width(width.value)))
  }

  /** Evidence that `T` is a prelude type, and the [[Type]] rendered semantics give it. */
  @implicitNotFound("primitive operands and results must be prelude types, not ${T}")
  class Reified[T](val tpe: Type)
}

/** The type of a rendered value: a sized integer or float, a truth value, or one of these refined to the union of
  * inclusive intervals its encoding allows, such as `UInt[5] in (0 ~ 17) | (24 ~ 31)`.
  */
enum Type derives CanEqual {
  case Integer(width: Int in 1 ~ 64, signed: Boolean)
  case Float(width: Int in 1 ~ 64)

  /** A narrow floating-point encoding, held as its bits. */
  case Encoded(format: Minifloat)
  case Bool
  case Ranged(base: Type, intervals: Vec[(low: Long, high: Long)])

  /** The type without its range. */
  def unranged: Type = this match {
    case Ranged(base, _) => base.unranged
    case _               => this
  }

  /** The bits a value takes; a truth value takes one. */
  def bits: Int in 1 ~ 64 = unranged match {
    case Integer(width, _) => width
    case Float(width)      => width
    case Encoded(format)   => format.width
    case _                 => 1
  }

  /** The low bits of `value` as this type holds them: extended by sign, truncated, or 0 or 1 for a truth value. */
  def represent(value: Long): Long = unranged match {
    case Bool                  => if (value != 0L) 1L else 0L
    case Integer(width, true)  => Bits.signExtend(value, width)
    case Integer(width, false) => Bits.truncate(value, width)
    case Float(width)          => Bits.truncate(value, width)
    case Encoded(format)       => value & ((1L << format.width) - 1L)
    case Ranged(_, _)          => value
  }
}

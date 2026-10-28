package net.kurobako.gemstone.isa.nvidia

import MatrixTerms.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Term.Steps.*
import scala.collection.mutable

private[isa] object MatrixTerms {

  /** The rendered type of a matrix element; single-bit elements are named as 32-bit segments. */
  def elementType(element: Matrix.Element): Type = element match {
    case Matrix.Element.F16                            => Type.Float(16)
    case Matrix.Element.F32                            => Type.Float(32)
    case Matrix.Element.F64                            => Type.Float(64)
    case Matrix.Element.BF16                           => Type.Encoded(Minifloat.BFloat16)
    case Matrix.Element.TF32                           => Type.Encoded(Minifloat.TensorFloat32)
    case Matrix.Element.S8                             => Type.signed(8)
    case Matrix.Element.U8                             => Type.unsigned(8)
    case Matrix.Element.S4                             => Type.signed(4)
    case Matrix.Element.U4                             => Type.unsigned(4)
    case Matrix.Element.B1                             => Type.unsigned(32)
    case Matrix.Element.S32                            => Type.signed(32)
    case Matrix.Element.E4M3                           => Type.Encoded(Minifloat.E4M3)
    case Matrix.Element.E5M2                           => Type.Encoded(Minifloat.E5M2)
    case Matrix.Element.E3M2                           => Type.Encoded(Minifloat.E3M2)
    case Matrix.Element.E2M3                           => Type.Encoded(Minifloat.E2M3)
    case Matrix.Element.E2M1 | Matrix.Element.E2M1Byte => Type.Encoded(Minifloat.E2M1)
  }
}

/** The operands of an element of a matrix product as state, with each bound intermediate a step. */
class MatrixTerms(names: Names, operation: Matrix.Operation.Multiply) extends MatrixOperands[Term] {
  private val bindings          = Vec.newBuilder[Term.Step]
  private val slots             = mutable.HashMap.empty[Int, String]
  protected def scaleType: Type = operation.scaling.map(_.format) match {
    case Some(Matrix.ScaleFormat.UE4M3) => Type.Encoded(Minifloat.E4M3)
    case _                              => Type.Encoded(Minifloat.E8M0)
  }
  def a(index: Int): Term         = Term.Variable(names.declare(s"a$index", elementType(operation.a.element)))
  def b(index: Int): Term         = Term.Variable(names.declare(s"b$index", elementType(operation.b.element)))
  def c: Term                     = Term.Variable(names.declare("c", elementType(operation.c.element)))
  def selection(index: Int): Term =
    Term.Variable(names.declare(s"e${Matrix.selectionField(operation.a.element, index)}", Type.unsigned(2)))
  def scaleA(block: Int): Term              = Term.Variable(names.declare(s"sa$block", scaleType))
  def scaleB(block: Int): Term              = Term.Variable(names.declare(s"sb$block", scaleType))
  def bind(name: String, value: Term): Term = {
    names.keep(name)
    bindings ++= assign(names, name, value).all
    Term.Variable(name)
  }

  /** The intermediates bound so far, in order. */
  def steps: Term.Steps                                = Term.Steps(bindings.result())
  def hold(slot: Int, name: String, value: Term): Term = {
    slots(slot) = name
    bind(name, value)
  }
  def held(slot: Int): Term = Term.Variable(slots(slot))
}

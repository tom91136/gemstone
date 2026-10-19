package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

object Shape {

  /** A pointer's value: a flat byte address. */
  val Address: Type = Type.unsigned(64)
}

/** The type of an id as rendered semantics see it: a scalar, or the lanes, members or pointee it is made of. A matrix
  * is `columns` column vectors of `rows` lanes.
  */
enum Shape {
  case Scalar(tpe: Type)
  case Vector(component: Type, lanes: Int)
  case Matrix(component: Type, rows: Int, columns: Int)
  case Structure(members: Vec[Shape])
  case Pointer(pointee: Shape)

  /** A cooperative matrix, which each invocation holds whole, of `rows` by `columns` components in row-major lanes.
    */
  case Cooperative(component: Type, rows: Int, columns: Int, use: Int)

  /** The scalar values the shape holds, in lane order, under `name`, and their types. */
  def components(name: String): Vec[(name: String, tpe: Type)] = this match {
    case Scalar(tpe)                      => Vec((name, tpe))
    case Vector(component, lanes)         => Vec.tabulate(lanes)(lane => (s"${name}_$lane", component))
    case Matrix(component, rows, columns) =>
      Vec.tabulate(rows * columns)(lane => (s"${name}_${lane / rows}_${lane % rows}", component))
    case Structure(members) => members.zipWithIndex.flatMap((member, index) => member.components(s"${name}_$index"))
    case Pointer(_)         => Vec((name, Shape.Address))
    case Cooperative(component, rows, columns, _) =>
      Vec.tabulate(rows * columns)(lane => (s"${name}_${lane / columns}_${lane % columns}", component))
  }

  def count: Int = this match {
    case Vector(_, lanes)                 => lanes
    case Matrix(_, rows, columns)         => rows * columns
    case Cooperative(_, rows, columns, _) => rows * columns
    case Scalar(_) | Pointer(_)           => 1
    case Structure(members)               => members.headOption.fold(1)(_.count)
  }

  /** The type of each lane of a scalar, vector or matrix, or of a pointer's address. */
  def scalar: Type = this match {
    case Scalar(tpe)                        => tpe
    case Vector(component, _)               => component
    case Matrix(component, _, _)            => component
    case Cooperative(component = component) => component
    case Pointer(_)                         => Shape.Address
    case Structure(members)                 => members.headOption.fold(Shape.Address)(_.scalar)
  }
}

package net.kurobako.gemstone.isa.catalogue

import net.kurobako.gemstone.collection.*
import upickle.default.ReadWriter

enum Expression derives ReadWriter {
  case Constant(value: BigInt, relaxed: Boolean)
  case Reference(name: String, attribute: Option[String], relaxed: Boolean)
  case Scale(value: Expression, factor: BigInt)
  case Multiply(value: Expression, factor: BigInt)
  case Identity(first: Expression, second: Expression)
  case FixedFloat(value: Expression, format: FloatEncoding)
  case Relaxed(value: Expression)
  case FloatConvert(
      value: Expression,
      selector: String,
      cases: Vec[(Vec[BigInt], FloatEncoding)],
      fallback: FloatEncoding
  )
  case Lookup(name: String, arguments: Vec[Expression])

  /** Bindings this expression reads; an attribute reference also names its base binding when `bases` is set. */
  def references(bases: Boolean): Set[String] = this match {
    case Reference(name, attribute, _) =>
      attribute.fold(Set(name))(suffix => Set(s"$name@$suffix") ++ Option.when(bases)(name))
    case FixedFloat(value, _)                => value.references(bases)
    case Relaxed(value)                      => value.references(bases)
    case Scale(value, _)                     => value.references(bases)
    case Multiply(value, _)                  => value.references(bases)
    case Identity(first, second)             => first.references(bases) ++ second.references(bases)
    case FloatConvert(value, selector, _, _) => value.references(bases) + selector
    case Lookup(_, arguments)                => arguments.flatMap(_.references(bases)).toSet
    case Constant(_, _)                      => Set.empty
  }
}

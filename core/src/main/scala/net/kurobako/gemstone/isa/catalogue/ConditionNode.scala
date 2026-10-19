package net.kurobako.gemstone.isa.catalogue

import net.kurobako.gemstone.collection.*
import upickle.default.ReadWriter

/** A condition expression node as stored in a catalogue: strings and operands are ids into the catalogue's tables. */
enum ConditionNode derives ReadWriter {
  case Number(value: BigInt)
  case StringValue(value: Int)
  case Field(name: Int, attribute: Option[Int])
  case Parameter(name: Int)
  case Constant(name: Int)
  case EnumValue(kind: Int, name: Int)
  case Unary(operator: Int, value: Int)
  case Binary(operator: Int, left: Int, right: Int)
  case Call(name: Int, arguments: Vec[Int])
  case Conditional(condition: Int, whenTrue: Int, whenFalse: Int)
}

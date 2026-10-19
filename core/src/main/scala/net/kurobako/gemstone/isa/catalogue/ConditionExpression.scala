package net.kurobako.gemstone.isa.catalogue

import net.kurobako.gemstone.collection.*

enum ConditionExpression derives CanEqual {
  case Number(value: BigInt)
  case StringValue(value: String)
  case Field(name: String, attribute: Option[String])
  case Parameter(name: String)
  case Constant(name: String)
  case EnumValue(kind: String, name: String)
  case Unary(operator: UnaryOperator, value: ConditionExpression)
  case Binary(operator: BinaryOperator, left: ConditionExpression, right: ConditionExpression)
  case Call(name: BuiltinCall, arguments: Vec[ConditionExpression])
  case Conditional(condition: ConditionExpression, whenTrue: ConditionExpression, whenFalse: ConditionExpression)
}

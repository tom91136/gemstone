package net.kurobako.gemstone.isa.catalogue

object BinaryOperator {
  def from(spelling: String): BinaryOperator = spelling match {
    case "&&"  => BinaryOperator.LogicalAnd
    case "||"  => BinaryOperator.LogicalOr
    case "->"  => BinaryOperator.Implies
    case "=="  => BinaryOperator.Equal
    case "!="  => BinaryOperator.NotEqual
    case "<"   => BinaryOperator.Less
    case "<="  => BinaryOperator.LessEqual
    case ">"   => BinaryOperator.Greater
    case ">="  => BinaryOperator.GreaterEqual
    case "+"   => BinaryOperator.Add
    case "-"   => BinaryOperator.Subtract
    case "*"   => BinaryOperator.Multiply
    case "/"   => BinaryOperator.Divide
    case "%"   => BinaryOperator.Remainder
    case "&"   => BinaryOperator.BitwiseAnd
    case "|"   => BinaryOperator.BitwiseOr
    case "^"   => BinaryOperator.BitwiseXor
    case "<<"  => BinaryOperator.ShiftLeft
    case ">>"  => BinaryOperator.ShiftRight
    case value => BinaryOperator.Unsupported(value)
  }
}

enum BinaryOperator derives CanEqual {
  case LogicalAnd, LogicalOr, Implies, Equal, NotEqual, Less, LessEqual, Greater, GreaterEqual
  case Add, Subtract, Multiply, Divide, Remainder, BitwiseAnd, BitwiseOr, BitwiseXor, ShiftLeft, ShiftRight
  case Unsupported(vendorSpelling: String)

  def spelling: String = this match {
    case LogicalAnd                  => "&&"
    case LogicalOr                   => "||"
    case Implies                     => "->"
    case Equal                       => "=="
    case NotEqual                    => "!="
    case Less                        => "<"
    case LessEqual                   => "<="
    case Greater                     => ">"
    case GreaterEqual                => ">="
    case Add                         => "+"
    case Subtract                    => "-"
    case Multiply                    => "*"
    case Divide                      => "/"
    case Remainder                   => "%"
    case BitwiseAnd                  => "&"
    case BitwiseOr                   => "|"
    case BitwiseXor                  => "^"
    case ShiftLeft                   => "<<"
    case ShiftRight                  => ">>"
    case Unsupported(vendorSpelling) => vendorSpelling
  }
}

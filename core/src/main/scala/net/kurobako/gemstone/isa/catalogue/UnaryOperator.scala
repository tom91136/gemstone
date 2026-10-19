package net.kurobako.gemstone.isa.catalogue

object UnaryOperator {
  def from(spelling: String): UnaryOperator = spelling match {
    case "!"   => UnaryOperator.LogicalNot
    case "-"   => UnaryOperator.Negate
    case "~"   => UnaryOperator.BitwiseNot
    case value => UnaryOperator.Unsupported(value)
  }
}

enum UnaryOperator derives CanEqual {
  case LogicalNot, Negate, BitwiseNot
  case Unsupported(vendorSpelling: String)

  def spelling: String = this match {
    case LogicalNot                  => "!"
    case Negate                      => "-"
    case BitwiseNot                  => "~"
    case Unsupported(vendorSpelling) => vendorSpelling
  }
}

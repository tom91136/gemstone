package net.kurobako.gemstone.isa.catalogue

/** Known helper calls are closed; table lookup names remain explicit data. */
object BuiltinCall {
  def from(name: String): BuiltinCall = name match {
    case "DEFINED" => BuiltinCall.Defined
    case "IsEven"  => BuiltinCall.IsEven
    case "IsOdd"   => BuiltinCall.IsOdd
    case value     => BuiltinCall.Table(value)
  }
}

enum BuiltinCall derives CanEqual {
  case Defined, IsEven, IsOdd
  case Table(tableName: String)

  def spelling: String = this match {
    case Defined          => "DEFINED"
    case IsEven           => "IsEven"
    case IsOdd            => "IsOdd"
    case Table(tableName) => tableName
  }
}

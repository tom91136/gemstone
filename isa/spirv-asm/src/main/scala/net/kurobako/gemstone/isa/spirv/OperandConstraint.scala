package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

object OperandConstraint {
  enum ValueClass derives CanEqual {
    case BoolScalar, IntScalar, Pointer, Vector

    def description: String = this match {
      case BoolScalar => "boolean scalar"
      case IntScalar  => "scalar integer"
      case Pointer    => "pointer"
      case Vector     => "vector"
    }

    def opcodes: Vec[String] = this match {
      case BoolScalar => Vec("OpTypeBool")
      case IntScalar  => Vec("OpTypeInt")
      case Pointer    => Vec("OpTypePointer", "OpTypeUntypedPointerKHR")
      case Vector     => Vec("OpTypeVector")
    }
  }
}

/** What an operand names or holds, beyond the kind its grammar gives it. */
enum OperandConstraint derives CanEqual {
  case Type
  case Declares(opcodes: Vec[String])
  case Value(kind: OperandConstraint.ValueClass)
  case ScopeId
  case MemorySemanticsId
  case ScalarComponent
  case ByteWidth
  case OneOf(values: Vec[Long])

  def describe: String = this match {
    case Type              => "a type"
    case Declares(opcodes) => if (opcodes.size == 1) s"an ${opcodes.head}" else s"one of ${opcodes.mkString(", ")}"
    case Value(kind)       => s"a ${kind.description} value"
    case ScopeId           => "a 32-bit integer naming a Scope"
    case MemorySemanticsId => "a 32-bit integer naming MemorySemantics"
    case ScalarComponent   => "a scalar type"
    case ByteWidth         => "a whole number of bytes"
    case OneOf(values)     => s"one of ${values.mkString(", ")}"
  }
}

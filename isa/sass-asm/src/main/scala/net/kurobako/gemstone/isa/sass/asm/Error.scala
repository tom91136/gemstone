package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.text.*

object Error {
  def detail(value: String): NonEmpty[String] =
    (if (value.nonEmpty) value else "unspecified SASS error").unsafe[NonEmpty[String]]
}

enum Error {
  case Syntax(error: SourceError)
  case Assembly(detail: NonEmpty[String])
  case Disassembly(detail: NonEmpty[String])
  case Printing(detail: NonEmpty[String])
  case DuplicateLabel(name: NonEmpty[String])
  case UndefinedLabel(name: NonEmpty[String])
  case TargetMismatch(expected: Target, actual: Target)

  def message: String = this match {
    case Syntax(error)                    => error.diagnostic
    case Assembly(detail)                 => detail.raw
    case Disassembly(detail)              => detail.raw
    case Printing(detail)                 => detail.raw
    case DuplicateLabel(name)             => s"duplicate label ${name.raw}"
    case UndefinedLabel(name)             => s"undefined label ${name.raw}"
    case TargetMismatch(expected, actual) => s"target ${actual.name} does not match ${expected.name}"
  }

  override def toString: String = message
}

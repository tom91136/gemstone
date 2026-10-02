package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object Operand {
  def isAddress(operand: Operand): Boolean = operand match {
    case Address(_) => true
    case _          => false
  }

  def isParenthesized(operand: Operand): Boolean = operand match {
    case Parenthesized(_) => true
    case _                => false
  }

  def isVectorLiteral(operand: Operand): Boolean = operand match {
    case VectorLiteral(_) => true
    case _                => false
  }

  def isWithPredicate(operand: Operand): Boolean = operand match {
    case WithPredicate(_, _) => true
    case _                   => false
  }
}

/** Lossless syntax used by generated, opcode-specific [[Instruction]] cases. */
enum Operand {
  case Immediate(raw: NonEmpty[String])
  case Register(name: NonEmpty[String], part: Option[NonEmpty[String]])
  case Symbol(name: NonEmpty[String])
  case StringLiteral(rawContents: String)
  case Qualifier(path: NonEmpty[Vec[NonEmpty[String]]])
  case VectorLiteral(items: Vec[Operand])
  case Parenthesized(items: Vec[Operand])
  case Address(items: Vec[Operand])
  case Qualified(base: Operand, qualifier: NonEmpty[String])
  case Index(base: Operand, index: Operand)
  case Add(left: Operand, right: Operand)
  case Subtract(left: Operand, right: Operand)
  case WithPredicate(base: Operand, predicate: Operand)
  case Negate(operand: Operand)
}

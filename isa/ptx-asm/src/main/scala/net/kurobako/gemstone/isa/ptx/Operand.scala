package net.kurobako.gemstone.isa.ptx

import OperandDomains.*
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

  /** `operand` without the predicate a `base|predicate` form also names. */
  def splitPredicate(operand: Operand): (base: Operand, predicate: Option[Operand]) = operand match {
    case WithPredicate(base, predicate) => (base = base, predicate = Some(predicate))
    case other                          => (base = other, predicate = None)
  }

  def isWithPredicate(operand: Operand): Boolean = operand match {
    case WithPredicate(_, _) => true
    case _                   => false
  }
}

/** Lossless syntax used by generated, opcode-specific [[Instruction]] cases. */
enum Operand {
  case Immediate(raw: NonEmpty[String])
  case Register(name: NonEmpty[String], part: Option[RegisterPart])
  case Symbol(name: NonEmpty[String])
  case GenericAddress(symbol: Operand)
  case StringLiteral(rawContents: String)
  case Qualifier(path: NonEmpty[Vec[NonEmpty[String]]])
  case VectorLiteral(items: Vec[Operand])
  case Parenthesized(items: Vec[Operand])
  case Address(items: Vec[Operand])
  case Qualified(base: Operand, qualifier: QualifiedOperand)
  case Index(base: Operand, index: Operand)
  case Add(left: Operand, right: Operand)
  case Subtract(left: Operand, right: Operand)
  case WithPredicate(base: Operand, predicate: Operand)
  case Negate(operand: Operand)

  /** A logically negated predicate source, `!p`. */
  case Not(operand: Operand)

  /** Rebuilds this operand with `transform` applied to each direct child. */
  def mapChildren(transform: Operand => Operand): Operand = this match {
    case GenericAddress(symbol)         => GenericAddress(transform(symbol))
    case VectorLiteral(items)           => VectorLiteral(items.map(transform))
    case Parenthesized(items)           => Parenthesized(items.map(transform))
    case Address(items)                 => Address(items.map(transform))
    case Qualified(base, qualifier)     => Qualified(transform(base), qualifier)
    case Index(base, index)             => Index(transform(base), transform(index))
    case Add(left, right)               => Add(transform(left), transform(right))
    case Subtract(left, right)          => Subtract(transform(left), transform(right))
    case WithPredicate(base, predicate) => WithPredicate(transform(base), transform(predicate))
    case Negate(value)                  => Negate(transform(value))
    case Not(value)                     => Not(transform(value))
    case leaf                           => leaf
  }
}

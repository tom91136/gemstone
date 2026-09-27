package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.NonEmptyVec

case class Operand(kind: NonEmptyString, words: NonEmptyVec[Word]) {
  def singleWord: Either[String, Word] =
    Either.cond(words.length == 1, words.head, s"$kind contains ${words.length} words")
}

object Operand {
  // Generated Id and single-word literal operands are non-empty by construction.
  private[v1_6] def id(operand: Operand): Int        = operand.words.head.raw
  private[v1_6] def unsigned(operand: Operand): Long = operand.words.head.toUnsigned.raw
}

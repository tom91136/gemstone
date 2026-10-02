package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object Operand {
  // Generated Id and single-word literal operands are non-empty by construction.
  def id(operand: Operand): Int        = operand.words.head.raw
  def unsigned(operand: Operand): Long = operand.words.head.toUnsigned.toLong
}

case class Operand(kind: OperandKind, words: NonEmpty[Vec[Word]]) {
  def singleWord: Either[NonEmpty[String], Word] =
    Either.cond(words.length == 1, words.head, s"$kind contains ${words.length} words")
}

package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class Operand(kind: SPIRVOperandKind, words: Vec[Word] :| MinLength[1]) {
  def singleWord: Either[String, Word] =
    Either.cond(words.size == 1, words.head, s"$kind contains ${words.size} words")
}

object Operand {
  // Generated Id and single-word literal operands are non-empty by construction.
  private[v1_6] def id(operand: Operand): Int          = operand.words.head.bits
  private[v1_6] def operandBits(operand: Operand): Int = operand.words.head.bits
  private[v1_6] def unsigned(operand: Operand): Long   = operand.words.head.toUnsigned
}

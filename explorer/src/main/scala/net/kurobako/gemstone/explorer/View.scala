package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*

/** The ISA-neutral view the explorer renders; each ISA model translates its own instruction descriptions into it. */
object View {

  /** A piece of an instruction's written syntax; slots stand for operands or modifiers. */
  enum Token {
    case Text(value: String)
    case Slot(name: String, detail: String)
  }

  /** The values an operand field accepts: a one-line `summary` and every value as a (codes, meaning) row. */
  case class Domain(summary: String, values: Vec[(String, String)])

  case class OperandSlot(name: String, kind: String, detail: String = "", domain: Option[Domain] = None)

  /** A modifier or operand position that takes one of `values`. */
  case class Choice(name: String, values: Vec[String])

  /** The instruction word bits a field occupies, least significant value bit first; `ignored` marks an operand slot the
    * instruction does not read, and `domain` lists the values the field takes where the catalogue models them.
    */
  case class Field(
      name: String,
      positions: Vec[Int],
      detail: String,
      ignored: Boolean = false,
      domain: Option[Domain] = None
  )

  /** A `width`-bit instruction word; `fixed` maps the bit positions every encoding of the form shares to their value.
    */
  case class Encoding(width: Int, fields: Vec[Field], fixed: Map[Int, Boolean])

  case class Form(
      name: String,
      syntax: Vec[Token],
      operands: Vec[OperandSlot],
      choices: Vec[Choice] = Vec.empty,
      properties: Vec[(String, String)] = Vec.empty,
      constraints: Vec[String] = Vec.empty,
      encoding: Option[Encoding] = None
  )

  /** An instruction's forms; `target` names the architecture whose catalogue they come from, when there is one. The
    * forms are built when first read, as the search lists need only their names.
    */
  class Entry(
      val language: Language,
      val mnemonic: String,
      val formNames: Vec[String],
      build: () => Vec[Form],
      val target: Option[String] = None
  ) {
    lazy val forms: Vec[Form]   = build()
    lazy val key: String        = mnemonic.toLowerCaseRoot
    lazy val searchText: String = (key +: formNames).mkString("\n").toLowerCaseRoot

    override def equals(other: Any): Boolean = other match {
      case that: Entry =>
        language == that.language && mnemonic == that.mnemonic && target == that.target &&
        formNames == that.formNames
      case _ => false
    }

    override def hashCode: Int = (language, mnemonic, target, formNames).hashCode

    override def toString: String = s"Entry($language, $mnemonic, $target)"
  }

  case class Register(name: String, encoding: Option[BigInt] = None, note: String = "")
  case class RegisterFile(name: String, description: String, registers: Vec[Register])

  /** Fixed bits of a value deposited at `positions`, one value bit per position. */
  def deposit(positions: Vec[Int], value: BigInt): Map[Int, Boolean] =
    positions.zipWithIndex.map((position, bit) => position -> value.testBit(bit)).toMap
}

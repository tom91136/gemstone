package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*

object Lookup {
  private val Record  = "\u0001"
  private val Field   = "\u0002"
  private val Section = "\u0003"

  private def pack(sections: Vec[Vec[Vec[String]]]): String =
    sections.map(_.map(_.mkString(Field)).mkString(Record)).mkString(Section)

  private def unpack(text: String): Vec[Vec[Vec[String]]] =
    Vec.from(text.split(Section, -1)).map(section =>
      if (section.isEmpty) Vec.empty
      else Vec.from(section.split(Record, -1)).map(record => Vec.from(record.split(Field, -1)))
    )

  /** The PTX type each register an instance names is declared as. */
  case class Registers(declared: Vec[(name: String, declared: String)]) extends Lookup {
    def constructor: String = "registers"
    def encoded: String     = pack(Vec(declared.map((name, tpe) => Vec(name, tpe))))
  }

  /** A SASS instance's operands, constant operands and the way it writes its form. */
  case class Sass(
      operands: Map[String, String],
      constants: Vec[(binding: String, offset: Long)],
      written: SASSModel.Written
  ) extends Lookup {
    def constructor: String = "sass"
    def encoded: String     = pack(Vec(
      Vec.from(operands).sortBy(_._1).map((spelling, binding) => Vec(spelling, binding)),
      constants.map((binding, offset) => Vec(binding, offset.toString)),
      Vec(Vec(written.sm.toString, written.form, written.opcode) ++ written.texts)
    ))
  }

  /** The operand each spelling of an AMDGPU instance binds. */
  case class Spellings(operands: Map[String, String]) extends Lookup {
    def constructor: String = "spellings"
    def encoded: String = pack(Vec(Vec.from(operands).sortBy(_._1).map((spelling, binding) => Vec(spelling, binding))))
  }

  /** The mnemonic of the SPIR-V entry whose instances include the one checked. */
  case class Mnemonic(name: String) extends Lookup {
    def constructor: String = "mnemonic"
    def encoded: String     = name
  }

  def registers(encoded: String): Registers = Registers(unpack(encoded).head.map(record => (record(0), record(1))))

  def sass(encoded: String): Sass = {
    val sections = unpack(encoded)
    val written  = sections(2).head
    Sass(
      sections(0).map(record => record(0) -> record(1)).toMap,
      sections(1).map(record => (record(0), record(1).toLong)),
      SASSModel.Written(written(0).toInt, written(1), written(2), written.drop(3))
    )
  }

  def spellings(encoded: String): Spellings =
    Spellings(unpack(encoded).head.map(record => record(0) -> record(1)).toMap)

  def mnemonic(encoded: String): Mnemonic = Mnemonic(encoded)
}

/** What a reference needs of the instance a case checks, which its generator found while describing it. A generated
  * check carries it as one string literal that `Lookup`'s constructors decode.
  */
sealed trait Lookup {

  /** The constructor of `Lookup` that decodes [[encoded]]. */
  def constructor: String
  def encoded: String
}

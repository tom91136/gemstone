package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.string.*
import upickle.default.*

/** The generated operand domains of every ISA family and the domain of each operand field of each encoding. */
object DomainCatalogue {

  /** The domains of one ISA family, and for each format the signature of every opcode. */
  private class Family(isa: ISA) {
    private val json = resources.json[ujson.Value](classOf[ISA], s"domains-${isa.toString.toLowerCaseRoot}.json")

    private val rules = json("alignment").arr.toVector.map { rule =>
      DomainTable.Alignment(Run.file(rule("file").str), rule("fromBits").num.toInt, rule("alignment").num.toInt)
    }

    val tables: Map[String, DomainTable] = json("domains").obj.view.map { (id, domain) =>
      id -> DomainTable(id, Run.read(domain), rules)
    }.toMap

    private val signatures =
      json("signatures").arr.toVector.map(_("fields").obj.map((field, id) => field -> id.str).toMap)

    private val opcodes: Map[String, Map[Int, Int]] = json("encodings").obj.view.map { (format, pairs) =>
      format -> pairs.arr.view.map(pair => pair(0).num.toInt -> pair(1).num.toInt).toMap
    }.toMap

    def field(format: EncodingFormat, opcode: Int, field: EncodingField): Option[DomainTable] =
      opcodes.get(format.toString).flatMap(_.get(opcode)).flatMap(signatures(_).get(field.toString)).flatMap(tables.get)
  }

  private lazy val families: Map[ISA, Family] = ISA.values.map(isa => isa -> Family(isa)).toMap

  def table(isa: ISA, id: String): Option[DomainTable] = families(isa).tables.get(id)

  /** The domain of `field` in the encoding `(format, opcode)`; none when the field is ignored or unmodelled. */
  def field(isa: ISA, format: EncodingFormat, opcode: Int, field: EncodingField): Option[DomainTable] =
    families(isa).field(format, opcode, field)
}

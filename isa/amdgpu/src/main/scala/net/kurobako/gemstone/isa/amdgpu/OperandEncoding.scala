package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import upickle.default.*

private object OperandEncoding {
  given ReadWriter[Int in (4, 8)] = readwriter[Int].bimap(_.raw, _.unsafe[Int in (4, 8)])

  case class NamedField(field: InstructionLayout.Field, names: Map[String, String]) derives ReadWriter

  object Catalogue {
    private lazy val entries = resources.json[Vector[OperandEncoding]](getClass, "operands.json")
      .map(value => (value.isa, value.kind) -> value).toMap

    def forKind(isa: ISA, kind: OperandKind): Option[OperandEncoding] = entries.get((isa, kind))
  }
}

private case class OperandEncoding(
    isa: ISA,
    kind: OperandKind,
    fields: Vector[OperandEncoding.NamedField] = Vector.empty,
    selectors: Map[String, Int] = Map.empty,
    integers: Map[Int, Long] = Map.empty,
    floating: Map[Int, Double] = Map.empty,
    special: Map[Int, SpecialSource] = Map.empty,
    literals: Map[Int, Int in (4, 8)] = Map.empty
) derives ReadWriter

package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import upickle.default.*

private object OperandEncoding {
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
    fields: Vector[OperandEncoding.NamedField] = Vector.empty
) derives ReadWriter

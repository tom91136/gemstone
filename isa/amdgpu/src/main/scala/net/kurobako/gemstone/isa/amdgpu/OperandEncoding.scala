package net.kurobako.gemstone.isa.amdgpu

import scala.util.Using
import upickle.default.*

private object OperandEncoding {
  case class NamedField(field: InstructionLayout.Field, names: Map[String, String]) derives ReadWriter

  object Catalogue {
    private lazy val entries = Using.resource(
      Option(getClass.getResourceAsStream("operands.json"))
        .getOrElse(throw IllegalStateException("missing AMDGPU operands.json"))
    )(input => read[Vector[OperandEncoding]](input).map(value => (value.isa, value.kind) -> value).toMap)

    def forKind(isa: ISA, kind: OperandKind): Option[OperandEncoding] = entries.get((isa, kind))
  }
}

private case class OperandEncoding(
    isa: ISA,
    kind: OperandKind,
    fields: Vector[OperandEncoding.NamedField],
    selectors: Map[String, Int] = Map.empty,
    integers: Map[Int, Long] = Map.empty,
    floating: Map[Int, Double] = Map.empty,
    special: Map[Int, SpecialSource] = Map.empty,
    literals: Map[Int, Int] = Map.empty
) derives ReadWriter

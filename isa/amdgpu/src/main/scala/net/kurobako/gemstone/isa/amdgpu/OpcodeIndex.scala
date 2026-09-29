package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

/** Immutable lookup from encoded instruction fields to generated instruction definitions. */
private object OpcodeIndex {
  private type Key = (String, Int, ISA)

  private lazy val byEncoding: Map[Key, Vec[Instruction.Definition]] =
    Instruction.definitions.view.flatMap { definition =>
      definition.meta.encodings.view.flatMap { encoding =>
        val formats: Vec[String] = encoding.format match {
          case format if format.startsWith("VOP3_")    => Vec(format, "VOP3")
          case format if format == "VOP2_INST_LITERAL" => Vec("VOP2_INST_LITERAL", "VOP2")
          case format if format == "FLAT_GLBL"         => Vec("FLAT_GLBL", "GLOBAL", "FLAT")
          case format                                  => Vec(format)
        }
        formats.view.map { format =>
          (format, encoding.opcode, encoding.isa) -> definition
        }
      }
    }.toSeq.groupMap(_._1)(_._2).view.mapValues(definitions => Vec.from(definitions).distinct).toMap

  def definitions(format: String, opcode: Int, target: Target): Vec[Instruction.Definition] =
    byEncoding.getOrElse((format, opcode, target.isa), Vec.empty)
}

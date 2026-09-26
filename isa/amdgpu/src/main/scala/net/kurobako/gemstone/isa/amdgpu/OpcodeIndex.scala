package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

/** Immutable lookup from encoded instruction fields to generated instruction definitions. */
private[amdgpu] object OpcodeIndex {
  private type Key = (String, Int, Family, String)

  private lazy val byEncoding: Map[Key, Vec[Instruction.Definition]] =
    Instruction.definitions.view.flatMap { definition =>
      definition.meta.encodings.view.flatMap { encoding =>
        val formats = encoding.format match {
          case format if format.raw.startsWith("VOP3_")    => Vec(format.raw, "VOP3")
          case format if format.raw == "VOP2_INST_LITERAL" => Vec("VOP2_INST_LITERAL", "VOP2")
          case format if format.raw == "FLAT_GLBL"         => Vec("FLAT_GLBL", "GLOBAL", "FLAT")
          case format                                      => Vec(format.raw)
        }
        formats.view.map { format =>
          (format, encoding.opcode, encoding.family, encoding.target.raw) -> definition
        }
      }
    }.toSeq.groupMap(_._1)(_._2).view.mapValues(definitions => Vec.from(definitions).distinct).toMap

  def definitions(format: String, opcode: Int, target: Target): Vec[Instruction.Definition] =
    byEncoding.getOrElse((format, opcode, target.family, target.metadataTarget.raw), Vec.empty)
}

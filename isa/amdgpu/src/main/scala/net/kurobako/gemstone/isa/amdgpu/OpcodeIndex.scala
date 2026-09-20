package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

/** Immutable lookup from encoded instruction fields to generated opcode names. */
private[amdgpu] object OpcodeIndex {
  private type Key = (String, Int, Family, String)

  private lazy val byEncoding: Map[Key, Vec[String]] =
    Instruction.definitions.iterator.flatMap { definition =>
      definition.meta.encodings.iterator.flatMap { encoding =>
        val formats = encoding.format match {
          case format if format.value.startsWith("VOP3_")    => Vec(format.value, "VOP3")
          case format if format.value == "VOP2_INST_LITERAL" => Vec("VOP2_INST_LITERAL", "VOP2")
          case format if format.value == "FLAT_GLBL"         => Vec("FLAT_GLBL", "GLOBAL", "FLAT")
          case format                                        => Vec(format.value)
        }
        formats.iterator.map { format =>
          (format, encoding.opcode, encoding.family, encoding.target.value) -> definition.opcode.value
        }
      }
    }.toSeq.groupMap(_._1)(_._2).view.mapValues(names => Vec.from(names).distinct).toMap

  def names(format: String, opcode: Int, target: Target): Vec[String] =
    byEncoding.getOrElse((format, opcode, target.family, target.metadataTarget.value), Vec.empty[String])
}

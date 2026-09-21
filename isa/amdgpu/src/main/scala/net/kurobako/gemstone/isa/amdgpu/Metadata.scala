package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*

final case class Metadata(encodings: Vec[Encoding]) {
  def supports(family: Family): Boolean = encodings.exists(_.family == family)
  def supports(target: Target): Boolean =
    encodings.exists(encoding => encoding.family == target.family && encoding.target == target.metadataTarget)
}

object Metadata {
  private[amdgpu] def instructionsFor(family: Family): Vec[Instruction.Definition] =
    Instruction.definitions.view.filter(_.meta.supports(family)).toVec

  private[amdgpu] def encodingsFor(definition: Instruction.Definition, family: Family): Vec[Encoding] =
    definition.meta.encodings.filter(_.family == family)

  private[amdgpu] def encodingsFor(definition: Instruction.Definition, target: Target): Vec[Encoding] =
    definition.meta.encodings.filter(encoding =>
      encoding.family == target.family && encoding.target == target.metadataTarget
    )

  private[amdgpu] def encodingsFor(instruction: Instruction, target: Target): Vec[Encoding] =
    encodingsFor(instruction.definition, target)

  private[amdgpu] lazy val allEncodings: Vec[Encoding] =
    Instruction.definitions.view.map(_.meta).toVec.distinct.flatMap(_.encodings)
}

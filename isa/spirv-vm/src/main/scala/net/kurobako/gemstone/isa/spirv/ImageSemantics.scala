package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.render.*

/** SPIR-V image operations, as requests to a [[SPIRVMachine]]. */
private object ImageSemantics {

  private val opcodes = Set(
    "OpSampledImage",
    "OpImage",
    "OpImageSampleImplicitLod",
    "OpImageSampleExplicitLod",
    "OpImageSampleDrefImplicitLod",
    "OpImageSampleDrefExplicitLod",
    "OpImageSampleProjImplicitLod",
    "OpImageSampleProjExplicitLod",
    "OpImageSampleProjDrefImplicitLod",
    "OpImageSampleProjDrefExplicitLod",
    "OpImageGather",
    "OpImageDrefGather",
    "OpImageQueryLod",
    "OpImageQueryLevels",
    "OpImageQuerySizeLod",
    "OpImageQuerySize",
    "OpImageQuerySamples",
    "OpImageQueryOrder",
    "OpImageQueryFormat",
    "OpImageFetch",
    "OpImageRead",
    "OpImageWrite",
    "OpImageTexelPointer"
  )

  def handles(instruction: Instruction): Boolean = opcodes(instruction.opcode.raw)

  inline def image[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(instruction: Instruction): F[Unit] =
    machine.image(instruction)

  /** The forms the explorer shows: sampled-image construction, sampling, texel access and image queries. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] =
    Option.when(handles(instruction))(image(machine)(instruction))
}

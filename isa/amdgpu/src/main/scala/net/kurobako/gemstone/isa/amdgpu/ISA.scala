package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome
import Metadata.*

/** Operations bound to one public AMDGPU architectural-family package. */
final class ISA private[amdgpu] (val family: Family) {
  final val instructions: Vec[Instruction.Definition] = instructionsFor(family)

  final def validate(instruction: Instruction, target: Target): ValidationOutcome[Validation.Issue] =
    if (target.family == family) Validation.validate(instruction, target)
    else ValidationOutcome.Incomplete(Vec.nonEmpty(Validation.Issue.WrongFamily(target, family)))

  final def encode(instruction: Instruction, target: Target): Either[Binary.CodecError, Vec[Byte]] =
    requireFamily(target).flatMap(_ => Binary.encode(instruction, target))

  final def encodeSOPP(instruction: Instruction, target: Target): Either[Binary.CodecError, Vec[Byte]] =
    requireFamily(target).flatMap(_ => Binary.encodeSOPP(instruction, target))

  final def decodeSOPP(bytes: Vec[Byte], target: Target): Either[Binary.CodecError, Instruction] =
    requireFamily(target).flatMap(_ => Binary.decodeSOPP(bytes, target))

  private def requireFamily(target: Target): Either[Binary.CodecError, Unit] =
    Either.cond(target.family == family, (), Binary.CodecError.WrongFamily(target, family))
}

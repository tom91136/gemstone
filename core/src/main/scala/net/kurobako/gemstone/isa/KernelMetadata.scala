package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Format-neutral information needed by a native facade to marshal a kernel launch. */
final case class KernelMetadata private (
    argumentBlockSize: Int :| Positive0,
    explicitParameterCount: Int :| Positive0,
    parameters: Vec[KernelMetadata.Parameter],
    requiredWorkGroupSize: Option[KernelMetadata.WorkGroupSize],
    workGroupSizeHint: Option[KernelMetadata.WorkGroupSize],
    bindingIndices: Vec[Int :| Positive0],
    entryPointId: Option[Int :| Positive0]
)

object KernelMetadata {
  enum ParameterKind {
    case Scalar
    case Memory
    case Local
  }

  final case class WorkGroupSize(x: Int :| Positive, y: Int :| Positive, z: Int :| Positive)

  final case class Parameter(
      width: Int :| Positive,
      offset: Int :| Positive0,
      kind: ParameterKind,
      containsPointers: Boolean
  )

  def validated(
      argumentBlockSize: Int :| Positive0,
      explicitParameterCount: Int :| Positive0,
      parameters: Vec[Parameter],
      requiredWorkGroupSize: Option[WorkGroupSize] = None,
      workGroupSizeHint: Option[WorkGroupSize] = None,
      bindingIndices: Vec[Int :| Positive0] = Vec.empty,
      entryPointId: Option[Int :| Positive0] = None
  ): Either[String, KernelMetadata] =
    SymbolValidation
      .kernelParameters(
        argumentBlockSize,
        explicitParameterCount,
        parameters.map(parameter => (parameter.offset, parameter.width))
      )
      .map(_ =>
        KernelMetadata(
          argumentBlockSize,
          explicitParameterCount,
          parameters,
          requiredWorkGroupSize,
          workGroupSizeHint,
          bindingIndices,
          entryPointId
        )
      )
}

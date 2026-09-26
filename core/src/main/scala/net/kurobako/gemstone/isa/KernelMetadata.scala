package net.kurobako.gemstone.isa

import net.kurobako.gemstone.{NonNegativeInt, PositiveInt, Vec}

/** Format-neutral information needed by a native facade to marshal a kernel launch. */
final case class KernelMetadata private (
    argumentBlockSize: NonNegativeInt,
    explicitParameterCount: NonNegativeInt,
    parameters: Vec[KernelMetadata.Parameter],
    requiredWorkGroupSize: Option[KernelMetadata.WorkGroupSize],
    workGroupSizeHint: Option[KernelMetadata.WorkGroupSize],
    bindingIndices: Vec[NonNegativeInt],
    entryPointId: Option[NonNegativeInt]
)

object KernelMetadata {
  enum ParameterKind {
    case Scalar
    case Memory
    case Local
  }

  final case class WorkGroupSize(x: PositiveInt, y: PositiveInt, z: PositiveInt)

  final case class Parameter(
      width: PositiveInt,
      offset: NonNegativeInt,
      kind: ParameterKind,
      containsPointers: Boolean
  )

  def validated(
      argumentBlockSize: NonNegativeInt,
      explicitParameterCount: NonNegativeInt,
      parameters: Vec[Parameter],
      requiredWorkGroupSize: Option[WorkGroupSize] = None,
      workGroupSizeHint: Option[WorkGroupSize] = None,
      bindingIndices: Vec[NonNegativeInt] = Vec.empty,
      entryPointId: Option[NonNegativeInt] = None
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

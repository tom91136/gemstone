package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.vm.LaunchGeometry

/** Format-neutral information needed by a native facade to marshal a kernel launch. */
case class KernelMetadata private (
    argumentBlockSize: NonNegative[Int],
    explicitParameterCount: NonNegative[Int],
    parameters: Vec[KernelMetadata.Parameter],
    requiredWorkGroupSize: Option[LaunchGeometry.Dimensions],
    workGroupSizeHint: Option[LaunchGeometry.Dimensions],
    bindingIndices: Vec[NonNegative[Int]],
    entryPointId: Option[NonNegative[Int]]
)

object KernelMetadata {
  enum ParameterKind {
    case Scalar
    case Allocation
    case Local
  }

  case class Parameter(
      width: Positive[Int],
      offset: NonNegative[Int],
      kind: ParameterKind,
      containsPointers: Boolean
  )

  def validated(
      argumentBlockSize: NonNegative[Int],
      explicitParameterCount: NonNegative[Int],
      parameters: Vec[Parameter],
      requiredWorkGroupSize: Option[LaunchGeometry.Dimensions] = None,
      workGroupSizeHint: Option[LaunchGeometry.Dimensions] = None,
      bindingIndices: Vec[NonNegative[Int]] = Vec.empty,
      entryPointId: Option[NonNegative[Int]] = None
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

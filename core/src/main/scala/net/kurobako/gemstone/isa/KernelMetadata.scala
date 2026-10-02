package net.kurobako.gemstone.isa

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

object KernelMetadata {
  enum ParameterKind {
    case Scalar
    case Allocation
    case Local
  }

  case class Parameter(
      width: Int > 0,
      offset: Int >= 0,
      kind: ParameterKind,
      containsPointers: Boolean
  )

  def validated(
      argumentBlockSize: Int >= 0,
      explicitParameterCount: Int >= 0,
      parameters: Vec[Parameter],
      requiredWorkGroupSize: Option[LaunchGeometry.Dimensions] = None,
      workGroupSizeHint: Option[LaunchGeometry.Dimensions] = None,
      bindingIndices: Vec[Int >= 0] = Vec.empty,
      entryPointId: Option[Int >= 0] = None,
      registerCount: Option[Int >= 0] = None
  ): Either[NonEmpty[String], KernelMetadata] =
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        explicitParameterCount.raw <= parameters.size,
        (),
        "explicit parameter count exceeds the parameter count"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        parameters.forall(parameter =>
          parameter.offset.raw.toLong + parameter.width.raw.toLong <= argumentBlockSize.raw.toLong
        ),
        (),
        "kernel parameter lies outside the argument block"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        parameters.monotonic((previous, current) =>
          previous.offset.raw.toLong + previous.width.raw.toLong <= current.offset.raw.toLong
        ),
        (),
        "kernel parameters must be ordered and non-overlapping"
      )
    } yield KernelMetadata(
      argumentBlockSize,
      explicitParameterCount,
      parameters,
      requiredWorkGroupSize,
      workGroupSizeHint,
      bindingIndices,
      entryPointId,
      registerCount
    )
}

/** Format-neutral information needed by a native facade to marshal a kernel launch. */
case class KernelMetadata private (
    argumentBlockSize: Int >= 0,
    explicitParameterCount: Int >= 0,
    parameters: Vec[KernelMetadata.Parameter],
    requiredWorkGroupSize: Option[LaunchGeometry.Dimensions],
    workGroupSizeHint: Option[LaunchGeometry.Dimensions],
    bindingIndices: Vec[Int >= 0],
    entryPointId: Option[Int >= 0],
    registerCount: Option[Int >= 0]
)

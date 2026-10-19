package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.Wire.*

object KernelTask {
  enum Argument derives Codec, CanEqual {
    case Scalar(bytes: NonEmpty[Vec[Byte]])
    case Memory(region: AllocationId, offset: Long >= 0, range: Option[Long > 0] = None)
    case Local(size: Long > 0)

    /** A slot that a descriptor binding replaces before execution. */
    case Descriptor
  }

  case class ArgumentBuffer(region: AllocationId, offset: Long >= 0) derives Codec

  enum RelocationSource derives Codec, CanEqual {
    case Memory(region: AllocationId)
    case Argument(index: Int >= 0)
  }

  case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: Long >= 0,
      target: AllocationId,
      targetOffset: Long >= 0
  ) derives Codec

  def validated(
      kernelName: NonEmpty[String],
      groupSize: LaunchGeometry.Dimensions,
      groupCount: LaunchGeometry.Dimensions,
      arguments: Vec[Argument],
      globalSize: Option[LaunchGeometry.GlobalDimensions] = None,
      privateSegmentBytes: Int >= 0 = 0,
      groupSegmentBytes: Int >= 0 = 0,
      pointerRelocations: Vec[PointerRelocation] = Vec.empty,
      workDim: Int in 1 ~ 3 = 3,
      globalOffset: LaunchGeometry.Coordinates = LaunchGeometry.Coordinates.origin,
      discoverArgumentRelocations: Boolean = false,
      argumentBuffer: Option[ArgumentBuffer] = None,
      cluster: Option[LaunchGeometry.Dimensions] = None,
      cooperative: Boolean = false
  ): Either[NonEmpty[String], KernelTask] =
    for {
      geometry <- LaunchGeometry.forLaunch(groupSize, groupCount, globalSize, workDim, globalOffset)
      _        <- Either.cond[NonEmpty[String], Unit](
        argumentBuffer.isEmpty || (arguments.isEmpty && !discoverArgumentRelocations),
        (),
        "an argument buffer cannot be combined with structured arguments or argument relocation discovery"
      )
      _ <- validateRelocations(pointerRelocations, arguments.nonNegativeSize)
    } yield KernelTask(
      kernelName,
      geometry,
      privateSegmentBytes,
      groupSegmentBytes,
      arguments,
      pointerRelocations,
      discoverArgumentRelocations,
      argumentBuffer,
      cluster,
      cooperative
    )
  private def validateRelocations(
      pointerRelocations: Vec[PointerRelocation],
      argumentCount: Int >= 0
  ): Either[NonEmpty[String], Unit] =
    Either.cond[NonEmpty[String], Unit](
      pointerRelocations.forall {
        case PointerRelocation(RelocationSource.Argument(index), _, _, _) => index < argumentCount
        case PointerRelocation(RelocationSource.Memory(_), _, _, _)       => true
      },
      (),
      "pointer relocations must have an in-range source argument"
    )

}

case class KernelTask private (
    kernelName: NonEmpty[String],
    geometry: LaunchGeometry,
    privateSegmentBytes: Int >= 0,
    groupSegmentBytes: Int >= 0,
    arguments: Vec[KernelTask.Argument],
    pointerRelocations: Vec[KernelTask.PointerRelocation],
    discoverArgumentRelocations: Boolean,
    argumentBuffer: Option[KernelTask.ArgumentBuffer],
    // The thread-block cluster shape a launch names; workgroups of one cluster run together.
    cluster: Option[LaunchGeometry.Dimensions],
    // Every workgroup of a cooperative launch is resident at once, so workgroups can wait on each other.
    cooperative: Boolean
) {
  def withArguments(arguments: Vec[KernelTask.Argument]): Either[NonEmpty[String], KernelTask] =
    Either.cond(arguments.size == this.arguments.size, copy(arguments = arguments), "argument count cannot change")

  def withPointerRelocations(additional: Vec[KernelTask.PointerRelocation]): Either[NonEmpty[String], KernelTask] =
    KernelTask.validateRelocations(additional, arguments.nonNegativeSize)
      .map(_ => if (additional.isEmpty) this else copy(pointerRelocations = pointerRelocations ++ additional))
}

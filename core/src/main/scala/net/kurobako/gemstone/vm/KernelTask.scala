package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object KernelTask {
  enum Argument {
    case Scalar(bytes: Vec[Byte])
    case Memory(region: AllocationId, offset: Long >= 0, range: Option[Long > 0] = None)
    case Local(size: Long > 0)
  }

  enum RelocationSource {
    case Memory(region: AllocationId)
    case Argument(index: Int >= 0)
  }

  case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: Long >= 0,
      target: AllocationId,
      targetOffset: Long >= 0
  )

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
      discoverArgumentRelocations: Boolean = false
  ): Either[NonEmpty[String], KernelTask] =
    for {
      geometry <- LaunchGeometry.forLaunch(groupSize, groupCount, globalSize, workDim, globalOffset)
      _        <- validateRelocations(pointerRelocations, arguments.size)
    } yield KernelTask(
      kernelName,
      geometry,
      privateSegmentBytes,
      groupSegmentBytes,
      arguments,
      pointerRelocations,
      discoverArgumentRelocations
    )
  private def validateRelocations(
      pointerRelocations: Vec[PointerRelocation],
      argumentCount: Int
  ): Either[NonEmpty[String], Unit] =
    Either.cond[NonEmpty[String], Unit](
      pointerRelocations.forall {
        case PointerRelocation(RelocationSource.Argument(index), _, _, _) => index.raw < argumentCount
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
    discoverArgumentRelocations: Boolean
) {
  def withArguments(arguments: Vec[KernelTask.Argument]): Either[NonEmpty[String], KernelTask] =
    Either.cond(arguments.size == this.arguments.size, copy(arguments = arguments), "argument count cannot change")

  def withPointerRelocations(additional: Vec[KernelTask.PointerRelocation]): Either[NonEmpty[String], KernelTask] =
    KernelTask.validateRelocations(additional, arguments.size)
      .map(_ => if (additional.isEmpty) this else copy(pointerRelocations = pointerRelocations ++ additional))
}

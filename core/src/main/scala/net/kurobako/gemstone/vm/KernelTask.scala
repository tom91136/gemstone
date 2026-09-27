package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Constraint.NonEmptyString

case class KernelTask private (
    kernelName: NonEmptyString,
    geometry: LaunchGeometry,
    privateSegmentBytes: NonNegative[Int],
    groupSegmentBytes: NonNegative[Int],
    arguments: Vec[KernelTask.Argument],
    pointerRelocations: Vec[KernelTask.PointerRelocation],
    discoverArgumentRelocations: Boolean
) {
  def withPointerRelocations(additional: Vec[KernelTask.PointerRelocation]): KernelTask =
    if (additional.isEmpty) this else copy(pointerRelocations = pointerRelocations ++ additional)
}

object KernelTask {
  enum Argument {
    case Scalar(bytes: Vec[Byte])
    case Memory(region: MemoryRegion, offset: NonNegative[Long])
    case Local(size: Positive[Long])
  }

  enum RelocationSource {
    case Memory(region: MemoryRegion)
    case Argument(index: NonNegative[Int])
  }

  case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: NonNegative[Long],
      target: MemoryRegion,
      targetOffset: NonNegative[Long]
  )

  def validated(
      kernelName: NonEmptyString,
      groupSize: LaunchGeometry.Dimensions,
      groupCount: LaunchGeometry.Dimensions,
      arguments: Vec[Argument],
      globalSize: Option[LaunchGeometry.GlobalDimensions] = None,
      privateSegmentBytes: NonNegative[Int] = 0,
      groupSegmentBytes: NonNegative[Int] = 0,
      pointerRelocations: Vec[PointerRelocation] = Vec.empty,
      workDim: IntBetween[1, 3] = 3,
      globalOffset: LaunchGeometry.Coordinates = LaunchGeometry.Coordinates.origin,
      discoverArgumentRelocations: Boolean = false
  ): Either[String, KernelTask] =
    for {
      geometry <- LaunchGeometry.forLaunch(groupSize, groupCount, globalSize, workDim, globalOffset)
      _        <- Either.cond(
        pointerRelocations.forall {
          case PointerRelocation(RelocationSource.Argument(index), _, _, _) => index.raw < arguments.size
          case PointerRelocation(RelocationSource.Memory(_), _, _, _)       => true
        },
        (),
        "pointer relocations must have an in-range source argument"
      )
    } yield KernelTask(
      kernelName,
      geometry,
      privateSegmentBytes,
      groupSegmentBytes,
      arguments,
      pointerRelocations,
      discoverArgumentRelocations
    )
}

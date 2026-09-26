package net.kurobako.gemstone.vm

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveLong, WorkDimension}

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName

final case class KernelTask private (
    kernelName: KernelName,
    geometry: LaunchGeometry,
    privateSegmentBytes: NonNegativeInt,
    groupSegmentBytes: NonNegativeInt,
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
    case Memory(region: MemoryRegion, offset: NonNegativeLong)
    case Local(size: PositiveLong)
  }

  enum RelocationSource {
    case Memory(region: MemoryRegion)
    case Argument(index: NonNegativeInt)
  }

  final case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: NonNegativeLong,
      target: MemoryRegion,
      targetOffset: NonNegativeLong
  )

  def validated(
      kernelName: KernelName,
      groupSize: LaunchGeometry.Dimensions,
      groupCount: LaunchGeometry.Dimensions,
      arguments: Vec[Argument],
      globalSize: Option[LaunchGeometry.GlobalDimensions] = None,
      privateSegmentBytes: NonNegativeInt = NonNegativeInt.Zero,
      groupSegmentBytes: NonNegativeInt = NonNegativeInt.Zero,
      pointerRelocations: Vec[PointerRelocation] = Vec.empty,
      workDim: WorkDimension = WorkDimension.Three,
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

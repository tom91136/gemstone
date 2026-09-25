package net.kurobako.gemstone.vm

import net.kurobako.gemstone.WorkDimension

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class KernelTask private (
    kernelName: KernelName,
    geometry: LaunchGeometry,
    privateSegmentBytes: Int :| Positive0,
    groupSegmentBytes: Int :| Positive0,
    arguments: Vec[KernelTask.Argument],
    pointerRelocations: Vec[KernelTask.PointerRelocation]
)

object KernelTask {
  enum Argument {
    case Scalar(bytes: Vec[Byte])
    case Memory(region: MemoryRegion, offset: Long :| Positive0)
    case Local(size: Long :| Positive)
  }

  enum RelocationSource {
    case Memory(region: MemoryRegion)
    case Argument(index: Int :| Positive0)
  }

  final case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: Long :| Positive0,
      target: MemoryRegion,
      targetOffset: Long :| Positive0
  )

  def validated(
      kernelName: KernelName,
      groupSize: LaunchGeometry.Dimensions,
      groupCount: LaunchGeometry.Dimensions,
      arguments: Vec[Argument],
      globalSize: Option[LaunchGeometry.GlobalDimensions] = None,
      privateSegmentBytes: Int :| Positive0 = 0,
      groupSegmentBytes: Int :| Positive0 = 0,
      pointerRelocations: Vec[PointerRelocation] = Vec.empty,
      workDim: WorkDimension = 3,
      globalOffset: LaunchGeometry.Coordinates = LaunchGeometry.Coordinates(0, 0, 0)
  ): Either[String, KernelTask] =
    for {
      geometry <- LaunchGeometry.forLaunch(groupSize, groupCount, globalSize, workDim, globalOffset)
      _        <- Either.cond(
        pointerRelocations.forall {
          case PointerRelocation(RelocationSource.Argument(index), _, _, _) => index < arguments.size
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
      pointerRelocations
    )
}

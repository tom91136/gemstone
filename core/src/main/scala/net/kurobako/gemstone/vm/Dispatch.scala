package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

object Dispatch {

  /** Run every work-group of `geometry`, in parallel when `workgroups` judges the launch large enough. */
  def forEachGroup(geometry: LaunchGeometry, workgroups: Workgroups)(
      run: (Long >= 0, LaunchGeometry.Coordinates) => Either[Fault, Unit]
  ): Either[Fault, Unit] = forEachGroup[Fault](geometry, workgroups, fault => fault, fault => fault)(run)

  /** As above for ISA-specific failures: the first failure `run` reports wins over the scheduler's own faults, which
    * `fromFault` converts.
    */
  def forEachGroup[E](geometry: LaunchGeometry, workgroups: Workgroups, toFault: E => Fault, fromFault: Fault => E)(
      run: (Long >= 0, LaunchGeometry.Coordinates) => Either[E, Unit]
  ): Either[E, Unit] = {
    val total: Long >= 0 = geometry.groupTotal
    val first            = AtomicReference(Option.empty[E])
    val parallel         = workgroups.shouldParallel(total, geometry.groupVolume.toLong)
    workgroups.forEach(total, parallel) { index =>
      run(index, geometry.groupCoordinates(index)).left.map { error =>
        val _ = first.compareAndSet(None, Some(error))
        toFault(error)
      }
    }.left.map(fault => first.get().getOrElse(fromFault(fault)))
  }
}

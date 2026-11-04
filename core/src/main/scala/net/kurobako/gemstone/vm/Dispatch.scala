package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

object Dispatch {

  /** Run every work-group of `geometry`, in parallel when `workgroups` judges the launch large enough. Each group gets
    * its [[Pace]] in the launch's [[Pacing]]; a launch from inside a group parks the enclosing group until it returns.
    */
  def forEachGroup(geometry: LaunchGeometry, workgroups: Workgroups)(
      run: (Long >= 0, LaunchGeometry.Coordinates, Pace) => Either[Fault, Unit]
  ): Either[Fault, Unit] = forEachGroup[Fault](geometry, workgroups, fault => fault, fault => fault)(run)

  /** As above for ISA-specific failures: the first failure `run` reports wins over the scheduler's own faults, which
    * `fromFault` converts.
    */
  def forEachGroup[E](geometry: LaunchGeometry, workgroups: Workgroups, toFault: E => Fault, fromFault: Fault => E)(
      run: (Long >= 0, LaunchGeometry.Coordinates, Pace) => Either[E, Unit]
  ): Either[E, Unit] = {
    val total: Long >= 0 = geometry.groupTotal
    val first            = AtomicReference(Option.empty[E])
    val parallel         = workgroups.shouldParallel(total, geometry.groupVolume)
    val pacing = if (parallel) Pacing(math.min(total.raw, workgroups.concurrency.toLong).toInt) else Pacing.Unpaced
    Pacing.enclosing.parked {
      workgroups.forEach(total, parallel) { index =>
        val pace = pacing.join()
        try
          Pacing.within(pace)(run(index, geometry.groupCoordinates(index), pace)).left.map { error =>
            val _ = first.compareAndSet(None, Some(error))
            toFault(error)
          }
        finally pace.leave()
      }.left.map(fault => first.get().getOrElse(fromFault(fault)))
    }
  }
}

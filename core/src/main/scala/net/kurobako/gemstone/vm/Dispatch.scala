package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

object Dispatch {

  /** Run every work-group of `geometry`, in parallel when `workgroups` judges the launch large enough. Each group gets
    * its [[Pace]] in the launch's [[Pacing]]; a launch from inside a group parks the enclosing group until it returns.
    * A seeded `schedule` runs the groups one at a time on this thread in an order it picks.
    */
  def forEachGroup(
      geometry: LaunchGeometry,
      workgroups: Workgroups
  )(
      run: (Long >= 0, LaunchGeometry.Coordinates, Pace) => Either[Fault, Unit]
  ): Either[Fault, Unit] = forEachGroup(geometry, workgroups, ConcurrencySchedule.Fixed)(run)

  def forEachGroup(
      geometry: LaunchGeometry,
      workgroups: Workgroups,
      schedule: ConcurrencySchedule
  )(
      run: (Long >= 0, LaunchGeometry.Coordinates, Pace) => Either[Fault, Unit]
  ): Either[Fault, Unit] = forEachGroup[Fault](geometry, workgroups, fault => fault, fault => fault, schedule)(run)

  /** As above for ISA-specific failures: the first failure `run` reports wins over the scheduler's own faults, which
    * `fromFault` converts.
    */
  def forEachGroup[E](
      geometry: LaunchGeometry,
      workgroups: Workgroups,
      toFault: E => Fault,
      fromFault: Fault => E,
      schedule: ConcurrencySchedule = ConcurrencySchedule.Fixed
  )(
      run: (Long >= 0, LaunchGeometry.Coordinates, Pace) => Either[E, Unit]
  ): Either[E, Unit] = {
    val total: Long >= 0 = geometry.groupTotal
    val first            = AtomicReference(Option.empty[E])
    val parallel         = !schedule.seeded && workgroups.shouldParallel(total, geometry.groupVolume)
    val pacing = if (parallel) Pacing(math.min(total.raw, workgroups.residency.toLong).toInt, workgroups.coschedule)
    else Pacing.Unpaced
    Pacing.enclosing.parked {
      if (schedule.seeded && total.raw > 0L && total.raw <= Int.MaxValue.toLong) {
        val order = Array.tabulate(total.raw.toInt)(identity)
        ConcurrencySchedule.shuffle(schedule.forLaunch, order)
        var position = 0
        var failure  = Option.empty[E]
        while (position < order.length && failure.isEmpty) {
          val index = order(position).toLong.unsafe[Long >= 0]
          val pace  = pacing.join()
          try
            run(index, geometry.groupCoordinates(index), pace).left.foreach { error =>
              val _ = first.compareAndSet(None, Some(error))
              failure = Some(error)
            }
          finally pace.leave()
          position += 1
        }
        failure.toLeft(())
      } else if (schedule.seeded)
        workgroups.forEach(total, parallel = false) { index =>
          val pace = pacing.join()
          try
            run(index, geometry.groupCoordinates(index), pace).left.map { error =>
              val _ = first.compareAndSet(None, Some(error))
              toFault(error)
            }
          finally pace.leave()
        }.left.map(fault => first.get().getOrElse(fromFault(fault)))
      else
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

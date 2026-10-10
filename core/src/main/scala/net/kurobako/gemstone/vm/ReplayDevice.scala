package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicLong
import net.kurobako.gemstone.collection.*

object ReplayDevice {

  /** A point `step` observed instructions into a launch. It keeps what the launch started from, so replaying the launch
    * reaches the same point.
    */
  class Checkpoint(
      val format: ProgramFormat,
      val image: Vec[Byte],
      val task: KernelTask,
      val step: Long,
      val start: ArrayMemory.Snapshot
  )

  /** Called before each instruction with the checkpoint for that point; a fault stops the launch. */
  trait Observer {
    def step(checkpoint: Checkpoint, at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit]
  }
}

/** Launches that resume from any instruction by deterministic replay. Each launch loads its program afresh and starts
  * from a snapshot of device memory; with backends that run workgroups sequentially, replaying a launch to a checkpoint
  * reaches the same state the launch had there.
  */
class ReplayDevice(
    backends: BackendRegistry,
    memory: ArrayMemory,
    limits: TaskControl.Limits = TaskControl.Limits.Default
) {
  import ReplayDevice.*

  def launch(
      format: ProgramFormat,
      image: Vec[Byte],
      task: KernelTask,
      observer: Observer
  ): Either[Fault, Vec[Event]] = run(Checkpoint(format, image, task, 0L, memory.snapshot()), observer)

  /** Restores device memory to how `checkpoint`'s launch began, replays it unobserved to the checkpoint, then continues
    * observed from there.
    */
  def resume(checkpoint: Checkpoint, observer: Observer): Either[Fault, Vec[Event]] = {
    memory.restore(checkpoint.start)
    run(checkpoint, observer)
  }

  private def run(from: Checkpoint, observer: Observer): Either[Fault, Vec[Event]] =
    backends.load(from.format, from.image).flatMap { program =>
      val steps                     = AtomicLong()
      val replaying: LaunchObserver = (at, state) => {
        val step = steps.getAndIncrement()
        if (step < from.step) Right(())
        else observer.step(Checkpoint(from.format, from.image, from.task, step, from.start), at, state)
      }
      try Device(backends, memory, limits).launch(program, from.task, Some(replaying))
      finally program.close()
    }
}

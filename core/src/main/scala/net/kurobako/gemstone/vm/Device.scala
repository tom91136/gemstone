package net.kurobako.gemstone.vm

import java.util.concurrent.ConcurrentLinkedQueue
import net.kurobako.gemstone.collection.*
import scala.jdk.CollectionConverters.*

/** An in-process device for embedders: backends that load programs, one memory every launch runs against, and
  * synchronous launches. The caller owns and closes the programs it loads.
  */
class Device(backends: BackendRegistry, memory: AddressSpace, limits: TaskControl.Limits = TaskControl.Limits.Default) {
  def load(format: ProgramFormat, image: Vec[Byte]): Either[Fault, Backend.Program] = backends.load(format, image)

  /** Runs `task` to completion under a fresh instruction allowance, returning the events it raised. Parallel workgroups
    * raise events concurrently, so events from different workgroups have no defined order. An `observer` also sees
    * every access to device memory.
    */
  def launch(
      program: Backend.Program,
      task: KernelTask,
      observer: Option[LaunchObserver] = None
  ): Either[Fault, Vec[Event]] = {
    val events = ConcurrentLinkedQueue[Event]()
    program.execute(
      task,
      TaskContext(
        observer.fold(memory)(ObservedAddressSpace(memory, _)),
        TaskControl(limits, observer),
        event => Right(events.add(event)).map(_ => ())
      )
    ).map(_ => Vec.from(events.asScala))
  }
}

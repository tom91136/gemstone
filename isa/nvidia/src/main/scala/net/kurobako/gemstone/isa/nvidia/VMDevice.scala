package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.vm.*

/** What a grid runs against: the device heap, and the control and workgroup scheduling it shares with every grid its
  * device code launches. A cooperative grid also has the address of the driver workspace its grid barrier lives in.
  * Device-side `malloc` draws on the context's heap region when the launch names one. Printed events reach `handler` in
  * workgroup order once the grid that printed them completes.
  */
private case class VMDevice(
    memory: DeviceHeap,
    control: TaskControl,
    workgroups: Workgroups,
    gridWorkspace: Long = 0L,
    handler: Event => Either[Fault, Unit],
    heap: Option[HeapRegion] = None
) {
  val observer: LaunchObserver | Null = control.observer.orNull
}

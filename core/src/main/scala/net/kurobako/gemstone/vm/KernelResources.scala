package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

/** What each workgroup of a launch holds on the device: its lanes, issued in subgroups of `subgroupSize`, the registers
  * each lane takes when the program states them, and the shared memory it allocates, static and dynamic together.
  */
case class KernelResources(
    workgroupLanes: Int > 0,
    subgroupSize: Int > 0,
    registersPerLane: Option[Int >= 0],
    sharedBytes: Long >= 0
) derives CanEqual

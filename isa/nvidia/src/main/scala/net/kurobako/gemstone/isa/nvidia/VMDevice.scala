package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.vm.*

/** What a grid runs against: the device heap, and the control and workgroup scheduling it shares with every grid its
  * device code launches.
  */
private case class VMDevice(memory: DeviceHeap, control: TaskControl, workgroups: Workgroups)

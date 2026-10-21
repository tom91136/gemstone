package net.kurobako.gemstone.vm

/** What one task runs against: the memory it reads and writes, the control that cancels and meters it, and where its
  * events go.
  */
class TaskContext(val memory: AddressSpace, val control: TaskControl, val handler: Event => Either[Fault, Unit]) {
  def withMemory(memory: AddressSpace): TaskContext = TaskContext(memory, control, handler)
}

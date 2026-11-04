package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** The programs of one session share the device address space: program `id` loads at `Base + id * Stride`, between the
  * low addresses of an unbound image and the dispatch constants. A function pointer is therefore an address any other
  * program of the session can call.
  */
private object CodeWindows {
  val Base: Long   = 0x1000000000L
  val Stride: Long = 0x40000000L
  // the first dispatch constant, VMDispatchMemory.KernargAddress
  val Limit: Long = 0x100000000000L

  private val Capacity = (Limit - Base) / Stride

  /** Where a program's image loads, or why it cannot be given a window. */
  def loadBase(id: ProgramId, extent: Long): Either[Fault, Long] =
    if (id.raw >= Capacity) Left(Fault.OutOfResources(s"program ${id.raw} exceeds the $Capacity code windows"))
    else if (extent > Stride)
      Left(Fault.OutOfResources(s"code object spans $extent bytes, beyond the $Stride bytes of a code window"))
    else Right(Base + id.raw * Stride)

  /** The program whose window holds `address`. */
  def program(address: Long): Option[ProgramId] =
    Option.when(address >= Base + Stride && address < Limit)(ProgramId(((address - Base) / Stride).unsafe[Long > 0]))
}

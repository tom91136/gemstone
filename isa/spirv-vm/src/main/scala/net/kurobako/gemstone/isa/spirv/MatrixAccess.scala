package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** The components of a matrix in memory, each `width` bytes. A dense access places each at a byte offset from `base`. A
  * sparse one places each at an element index, where a negative index is a component that is not transferred.
  */
private class MatrixAccess(val base: Pointer, val places: Array[Long], val width: Int in 1 ~ 8, val sparse: Boolean) {
  private def transferred(component: Int): Boolean = !sparse || places(component) >= 0L

  private def at(component: Int): Pointer =
    base.copy(address = base.address + (if (sparse) places(component) * width else places(component)))

  /** Reads each placed component into `into`, leaving the others as they are. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  def read(memory: MemoryState, into: Array[Long]): AddressSpace.ReadResult[Array[Long]] = {
    var failure: Fault | AddressSpace.Pending.type | Null = null
    var component                                         = 0
    while (component < places.length && (failure eq null)) {
      if (transferred(component))
        memory.readScalar(at(component), width) match {
          case Left(error)  => failure = error
          case Right(value) => into(component) = value
        }
      component += 1
    }
    if (failure ne null) Left(failure) else Right(into)
  }

  /** Writes each placed component of `lanes`. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  def write(memory: MemoryState, lanes: Array[Long]): Either[Fault, Unit] = {
    var failure: Fault | Null = null
    var component             = 0
    while (component < places.length && failure == null) {
      if (transferred(component))
        memory.writeScalar(at(component), lanes(component), width) match {
          case Left(error) => failure = error
          case Right(())   => ()
        }
      component += 1
    }
    if (failure != null) Left(failure) else Right(())
  }
}

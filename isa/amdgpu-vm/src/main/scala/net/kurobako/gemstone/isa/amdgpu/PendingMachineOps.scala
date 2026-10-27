package net.kurobako.gemstone.isa.amdgpu

import AmdGpuMachine.*
import VMIR.*
import VMState.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

private object PendingMachineOps {
  val Pending: Fault = Fault.Unsupported("this machine operation has no AMDGPU semantics yet")
}

/** The operations of [[MachineAlgebra]] that no AMDGPU semantics request, which fault when run. */
private trait PendingMachineOps extends AmdGpuMachine[Long, Lane, Array[Byte], Flow] {
  import PendingMachineOps.*

  inline def park(barrier: Long, count: Long, inline blocking: Boolean): Flow[Int] = Left(Pending)

  inline def jumpEach(inline target: Lane => Long): Flow[Int] = Left(Pending)

  inline def branchEach(inline condition: Lane => Long, taken: Int): Flow[Int] = Left(Pending)

  inline def everyLane[A](inline body: Lane => Flow[Unit])(inline next: => Flow[A]): Flow[A] = Left(Pending)

  inline def write[A](lane: Lane, target: ScalarRegisterIndex, value: Long)(inline next: => Flow[A]): Flow[A] =
    Left(Pending)

  inline def load[A](lane: Lane, inline space: Space, address: Long, width: ScalarBitWidth)(
      inline next: Long => Flow[A]
  ): Flow[A] = Left(Pending)

  inline def store[A](lane: Lane, inline space: Space, address: Long, value: Long, width: ScalarBitWidth)(
      inline next: => Flow[A]
  ): Flow[A] = Left(Pending)

  inline def loadBytes[A](lane: Lane, inline space: Space, address: Long, length: Long, what: String)(
      inline next: Array[Byte] => Flow[A]
  ): Flow[A] = Left(Pending)

  inline def storeBytes[A](lane: Lane, inline space: Space, address: Long, data: Array[Byte], what: String)(
      inline next: => Flow[A]
  ): Flow[A] = Left(Pending)
}

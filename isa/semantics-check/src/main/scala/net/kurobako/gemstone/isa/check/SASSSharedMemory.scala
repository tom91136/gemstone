package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.semantics.render.Type

private[check] object SASSSharedMemory {
  import VMIR.*

  /** The address, bytes and direction of the shared access `instruction` makes, if it makes one. */
  def touched(instruction: Instruction): Option[(address: Value, bytes: Int, loads: Boolean)] =
    instruction match {
      case Instruction.Load(_, address, width, _, MemoryAccess.Shared) => Some((address, width.raw / 8, true))
      case Instruction.LoadVector(destinations, address, width, _, MemoryAccess.Shared) =>
        Some((address, destinations.size * width.raw / 8, true))
      case Instruction.Store(address, _, width, MemoryAccess.Shared) => Some((address, width.raw / 8, false))
      case Instruction.StoreVector(address, sources, width, MemoryAccess.Shared) =>
        Some((address, sources.size * width.raw / 8, false))
      case _ => None
    }
}

/** The shared memory a lowered SASS instruction reads and writes, which the VM holds in its workgroup and so starts
  * empty and does not give back. Before a run it takes the bytes `memory` holds, `steps` record the address of each
  * shared access and whether it executed, and after it each store's bytes are read back, as a launch's registers are.
  * [[settle]] then loads and stores them in `memory` as the definition does.
  */
private[check] final class SASSSharedMemory(
    steps: Vec[VMIR.PredicatedInstruction],
    registerCount: Int >= 0,
    memory: SeededMemory
) {
  import VMIR.*

  /** The registers of an access: its address, whether it executed, then one for each byte of a store. */
  private case class Access(address: Int, bytes: Int, loads: Boolean) {
    def executed: Int = address + 1
    def first: Int    = address + 2
    def end: Int      = first + (if (loads) 0 else bytes)
  }

  private val slot: Int => Register = index => Register(index.unsafe[Int >= 0])

  private def register(index: Int): Value = Value.Register(slot(index))

  private val accesses: Vec[Access] =
    steps.flatMap(step => SASSSharedMemory.touched(step.instruction)).foldLeft(Vec.empty[Access])((found, touch) =>
      found :+ Access(found.lastOption.fold(registerCount.raw)(_.end), touch.bytes, touch.loads)
    )

  /** The registers the instrumented steps use beyond the lowering's. */
  val registers: Int = accesses.lastOption.fold(0)(_.end - registerCount.raw)

  /** The stores that put the bytes `memory` holds in the VM's shared memory. */
  val preload: Vec[Instruction] = memory.held.toVec.sortBy(_._1).map((address, byte) =>
    Instruction.Store(
      Value.Immediate(VMAddressSpace.SharedBase + address),
      Value.Immediate(byte & 0xffL),
      8,
      MemoryAccess.Shared
    )
  )

  private val located: Vec[(step: PredicatedInstruction, access: Option[Access])] = {
    val found = accesses.iterator
    steps.map(step => (step, SASSSharedMemory.touched(step.instruction).map(_ => found.next())))
  }

  /** `steps`, each shared access preceded by a record of its address and followed by a record that it executed, and the
    * reads of what each store wrote.
    */
  val instrumented: Vec[PredicatedInstruction] = {
    val bare = (instruction: Instruction) => PredicatedInstruction(instruction, None)
    val body = located.flatMap { (step, access) =>
      access.fold(Vec(step)) { found =>
        val address = SASSSharedMemory.touched(step.instruction).map(_.address).getOrElse(Value.Immediate(0L))
        Vec(
          bare(Instruction.Move(slot(found.address), address, 64)),
          bare(Instruction.Move(slot(found.executed), Value.Immediate(0L), 64)),
          step,
          PredicatedInstruction(Instruction.Move(slot(found.executed), Value.Immediate(1L), 64), step.predication)
        )
      }
    }
    val reads = located.flatMap((step, access) =>
      access.filterNot(_.loads).toVec.map { found =>
        val destinations = Vec.tabulate(found.bytes)(index => slot(found.first + index))
        PredicatedInstruction(
          if (found.bytes == 1)
            Instruction.Load(destinations.head, register(found.address), 8, false, MemoryAccess.Shared)
          else Instruction.LoadVector(destinations, register(found.address), 8, false, MemoryAccess.Shared),
          step.predication
        )
      }
    )
    body ++ reads
  }

  /** Where the registers of [[instrumented]] are read back from the run. */
  val outputs: Vec[(NvidiaReference.Slot, Type)] =
    Vec.tabulate(registers)(index =>
      (NvidiaReference.Slot.Registers(Vec(slot(registerCount.raw + index))), Type.unsigned(64))
    )

  /** Loads and stores in `memory` what the accesses that executed did, from the values of [[outputs]]. */
  def settle(read: Vec[Long]): Unit = accesses.foreach { access =>
    val at = access.address - registerCount.raw
    if (read(access.executed - registerCount.raw) == 1L) {
      val offset = read(at) - VMAddressSpace.SharedBase
      if (access.loads) (0 until access.bytes).foreach(index => memory.load(offset + index, 1))
      else
        memory.write(offset, Vec.tabulate(access.bytes)(index => read(access.first - registerCount.raw + index).toByte))
    }
  }
}

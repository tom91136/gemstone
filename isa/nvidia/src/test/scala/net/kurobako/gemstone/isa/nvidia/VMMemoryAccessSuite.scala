package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class VMMemoryAccessSuite extends munit.FunSuite {
  private val sharedBase    = VMAddressSpace.SharedBase
  private val parameterBase = 0x700000000L

  private class ObservedMemory extends AddressMemory {
    val backing: ArrayCheckedMemory            = ArrayCheckedMemory.allocate(64, GuestAddress(0x10000000000L))
    val scalarWrites: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty
    val systemWrites: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty
    val systemReads: mutable.ArrayBuffer[Int]  = mutable.ArrayBuffer.empty

    override def readInt8(address: GuestAddress): Either[MemoryError, Byte]               = backing.readInt8(address)
    override def readInt32(address: GuestAddress): Either[MemoryError, Int]               = backing.readInt32(address)
    override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
      backing.writeInt8(address, value)
    override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
      backing.writeInt32(address, value)
    override def readLittleEndian(address: GuestAddress, size: Int where Between[1, 8]): Either[MemoryError, Long] =
      backing.readLittleEndian(address, size)
    override def writeLittleEndian(
        address: GuestAddress,
        value: Long,
        size: Int where Between[1, 8]
    ): Either[MemoryError, Unit] = {
      scalarWrites += size.raw
      backing.writeLittleEndian(address, value, size)
    }
    override def readSystemLittleEndian(
        address: GuestAddress,
        size: Int where Between[1, 8]
    ): Either[MemoryError, Long] = {
      systemReads += size.raw
      backing.readSystemLittleEndian(address, size)
    }
    override def writeSystemLittleEndian(
        address: GuestAddress,
        value: Long,
        size: Int where Between[1, 8]
    ): Either[MemoryError, Unit] = {
      systemWrites += size.raw
      backing.writeSystemLittleEndian(address, value, size)
    }
  }

  private def execute(
      instructions: Vec[Instruction],
      memory: AddressMemory,
      parameterBuffers: Map[Long, Array[Byte]] = Map.empty
  ): Either[VMFault, Unit] = {
    val kernel = Kernel("memory_access", Vec.empty, ExecutableProgram(instructions.map(PredicatedInstruction(_, None))))
    val geometry = LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get
    VMInterpreter.execute(kernel, geometry, Map.empty, memory, initialParameterBuffers = parameterBuffers)
  }

  test("shared, local, and generic vector accesses preserve their address routing") {
    val memory  = ObservedMemory()
    val base    = memory.backing.allocation.start
    val program = Vec(
      Instruction.Move("%r0", Value.Immediate(11), 32),
      Instruction.Move("%r1", Value.Immediate(22), 32),
      Instruction.StoreVector(
        Value.Immediate(sharedBase),
        Vec(Value.Register("%r0"), Value.Register("%r1")),
        32,
        MemoryAccess.Shared
      ),
      Instruction.LoadVector(Vec("%r2", "%r3"), Value.Immediate(sharedBase), 32, false, MemoryAccess.Shared),
      Instruction.Store(Value.Immediate(base.raw), Value.Register("%r2"), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 4L), Value.Register("%r3"), 32, MemoryAccess.Global(false)),
      Instruction.StoreVector(
        Value.Immediate(0),
        Vec(Value.Register("%r0"), Value.Register("%r1")),
        32,
        MemoryAccess.Local
      ),
      Instruction.LoadVector(Vec("%r4", "%r5"), Value.Immediate(0), 32, false, MemoryAccess.Local),
      Instruction.Store(Value.Immediate(base.raw + 8L), Value.Register("%r4"), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 12L), Value.Register("%r5"), 32, MemoryAccess.Global(false)),
      Instruction.StoreVector(
        Value.Immediate(sharedBase + 8L),
        Vec(Value.Register("%r0"), Value.Register("%r1")),
        32,
        MemoryAccess.Generic(false)
      ),
      Instruction.LoadVector(
        Vec("%r6", "%r7"),
        Value.Immediate(sharedBase + 8L),
        32,
        false,
        MemoryAccess.Generic(false)
      ),
      Instruction.Store(Value.Immediate(base.raw + 16L), Value.Register("%r6"), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 20L), Value.Register("%r7"), 32, MemoryAccess.Global(false)),
      Instruction.StoreVector(
        Value.Immediate(8),
        Vec(Value.Register("%r0"), Value.Register("%r1")),
        32,
        MemoryAccess.Generic(false)
      ),
      Instruction.LoadVector(Vec("%r8", "%r9"), Value.Immediate(8), 32, false, MemoryAccess.Generic(false)),
      Instruction.Store(Value.Immediate(base.raw + 24L), Value.Register("%r8"), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 28L), Value.Register("%r9"), 32, MemoryAccess.Global(false)),
      Instruction.Return
    )
    assertEquals(execute(program, memory), Right(()))
    assertEquals(
      Vec.tabulate(8)(index => memory.backing.readInt32(base.offset(index.toLong * 4L)).toOption.get),
      Vec(11, 22, 11, 22, 11, 22, 11, 22)
    )
  }

  test("global and generic vector stores retain coherent packed writes and system scope") {
    val memory = ObservedMemory()
    val base   = memory.backing.allocation.start
    val vector = Vec(
      Instruction.Move("%r0", Value.Immediate(101), 32),
      Instruction.Move("%r1", Value.Immediate(668), 32),
      Instruction.StoreVector(
        Value.Immediate(base.raw),
        Vec(Value.Register("%r0"), Value.Register("%r1")),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(execute(vector, memory), Right(()))
    assertEquals(Vec.from(memory.scalarWrites), Vec(8))

    memory.scalarWrites.clear()
    val system = Vec(
      Instruction.Move("%r0", Value.Immediate(303), 32),
      Instruction.Store(Value.Immediate(base.raw + 16L), Value.Register("%r0"), 32, MemoryAccess.Global(true)),
      Instruction.Load("%r1", Value.Immediate(base.raw + 16L), 32, false, MemoryAccess.Global(true)),
      Instruction.Return
    )
    assertEquals(execute(system, memory), Right(()))
    assertEquals(Vec.from(memory.systemWrites), Vec(4))
    assertEquals(Vec.from(memory.systemReads), Vec(4))
    assertEquals(memory.backing.readInt32(base.offset(16L)), Right(303))

    memory.systemWrites.clear()
    memory.systemReads.clear()
    val vectorSystem = Vec(
      Instruction.Move("%r0", Value.Immediate(101), 32),
      Instruction.Move("%r1", Value.Immediate(668), 32),
      Instruction.StoreVector(
        Value.Immediate(base.raw + 24L),
        Vec(Value.Register("%r0"), Value.Register("%r1")),
        32,
        MemoryAccess.Global(true)
      ),
      Instruction.LoadVector(
        Vec("%r2", "%r3"),
        Value.Immediate(base.raw + 24L),
        32,
        false,
        MemoryAccess.Global(true)
      ),
      Instruction.StoreVector(
        Value.Immediate(base.raw + 32L),
        Vec(Value.Register("%r2"), Value.Register("%r3")),
        32,
        MemoryAccess.Generic(true)
      ),
      Instruction.LoadVector(
        Vec("%r4", "%r5"),
        Value.Immediate(base.raw + 32L),
        32,
        false,
        MemoryAccess.Generic(true)
      ),
      Instruction.Return
    )
    assertEquals(execute(vectorSystem, memory), Right(()))
    assertEquals(Vec.from(memory.systemWrites), Vec(4, 4, 4, 4))
    assertEquals(Vec.from(memory.systemReads), Vec(4, 4, 4, 4))
    assertEquals(memory.backing.readInt32(base.offset(24L)), Right(101))
    assertEquals(memory.backing.readInt32(base.offset(28L)), Right(668))
    assertEquals(memory.backing.readInt32(base.offset(32L)), Right(101))
    assertEquals(memory.backing.readInt32(base.offset(36L)), Right(668))
  }

  test("generic parameter-buffer scalar and vector accesses stay inside the parameter buffer") {
    val memory  = ObservedMemory()
    val bytes   = Array[Byte](1, 2, 3, 4, 5, 6, 7, 8)
    val program = Vec(
      Instruction.Move("%r0", Value.Immediate(0x11223344L), 32),
      Instruction.Store(Value.Immediate(parameterBase), Value.Register("%r0"), 32, MemoryAccess.Generic(false)),
      Instruction.Load("%r1", Value.Immediate(parameterBase), 32, false, MemoryAccess.Generic(true)),
      Instruction.LoadVector(Vec("%r2", "%r3"), Value.Immediate(parameterBase), 32, false, MemoryAccess.Generic(true)),
      Instruction.Return
    )
    assertEquals(execute(program, memory, Map(parameterBase -> bytes)), Right(()))
    assertEquals(Vec.from(bytes.toSeq), Vec[Byte](0x44, 0x33, 0x22, 0x11, 5, 6, 7, 8))
    assert(memory.scalarWrites.isEmpty)
    assert(memory.systemReads.isEmpty)
  }

  test("vector memory faults still surface when the complete range is out of bounds") {
    val memory  = ObservedMemory()
    val outside = memory.backing.allocation.start.raw + memory.backing.allocation.size.raw.toLong
    val program = Vec(
      Instruction.LoadVector(Vec("%r0", "%r1"), Value.Immediate(outside), 32, false, MemoryAccess.Generic(false)),
      Instruction.Return
    )
    assert(execute(program, memory).left.toOption.exists {
      case VMFault.Memory(_) => true
      case _                 => false
    })
  }
}

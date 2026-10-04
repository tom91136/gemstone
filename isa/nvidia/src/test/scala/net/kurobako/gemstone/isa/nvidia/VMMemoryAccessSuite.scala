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
    override def readLittleEndian(address: GuestAddress, size: Int in 1 ~ 8): Either[MemoryError, Long] =
      backing.readLittleEndian(address, size)
    override def writeLittleEndian(
        address: GuestAddress,
        value: Long,
        size: Int in 1 ~ 8
    ): Either[MemoryError, Unit] = {
      scalarWrites += size.raw
      backing.writeLittleEndian(address, value, size)
    }
    override def readSystemLittleEndian(
        address: GuestAddress,
        size: Int in 1 ~ 8
    ): Either[MemoryError, Long] = {
      systemReads += size.raw
      backing.readSystemLittleEndian(address, size)
    }
    override def writeSystemLittleEndian(
        address: GuestAddress,
        value: Long,
        size: Int in 1 ~ 8
    ): Either[MemoryError, Unit] = {
      systemWrites += size.raw
      backing.writeSystemLittleEndian(address, value, size)
    }
  }

  private def execute(
      instructions: Vec[Instruction],
      memory: AddressMemory,
      parameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      threads: Int > 0 = 1
  ): Either[VMFault, Unit] = {
    val kernel = Kernel("memory_access", Vec.empty, ExecutableProgram(instructions.map(PredicatedInstruction(_, None))))
    val geometry = LaunchGeometry.forLaunch(Dimensions(threads, 1, 1), Dimensions.One).toOption.get
    VMInterpreter.execute(kernel, geometry, Map.empty, memory, initialParameterBuffers = parameterBuffers)
  }

  test("a uniform warp unlock cannot clear a subsequent owner's lock") {
    class LockMemory extends ObservedMemory {
      override def writeLittleEndian(
          address: GuestAddress,
          value: Long,
          size: Int where Between[1, 8]
      ): Either[MemoryError, Unit] =
        super.writeLittleEndian(address, value, size).flatMap { _ =>
          if (scalarWrites.size == 1)
            backing.atomicModifyLittleEndian(address, size)(_ => 1L).map { previous =>
              assertEquals(previous, 0L)
            }
          else Right(())
        }
    }
    val memory = LockMemory()
    val lock   = memory.backing.allocation.start
    assertEquals(memory.backing.writeInt32(lock, 1), Right(()))
    val instructions = Vec(
      Instruction.Store(Value.Immediate(lock.raw), Value.Immediate(0), 32, MemoryAccess.Global(false)),
      Instruction.Return
    )
    assertEquals(execute(instructions = instructions, memory = memory, threads = 32), Right(()))
    assertEquals(memory.backing.readInt32(lock), Right(1))
  }

  test("uniform stores from distinct warps retain separate transactions") {
    val memory       = ObservedMemory()
    val instructions = Vec(
      Instruction.Store(
        Value.Immediate(memory.backing.allocation.start.raw),
        Value.Immediate(7),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(execute(instructions = instructions, memory = memory, threads = 64), Right(()))
    assertEquals(Vec.from(memory.scalarWrites), Vec(4, 4))
    assertEquals(memory.backing.readInt32(memory.backing.allocation.start), Right(7))
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

  test("shared-window generic loads, stores and atomics alias explicit shared storage") {
    val memory   = ObservedMemory()
    val output   = memory.backing.allocation.start
    val alias    = VMAddressSpace.SharedWindowBase + 16L
    val explicit = sharedBase + 16L
    val program  = Vec(
      Instruction.StoreVector(
        Value.Immediate(alias),
        Vec(Value.Immediate(11), Value.Immediate(22)),
        32,
        MemoryAccess.Generic(false)
      ),
      Instruction.LoadVector(Vec("%r0", "%r1"), Value.Immediate(explicit), 32, false, MemoryAccess.Shared),
      Instruction.AtomicAdd("%r2", Value.Immediate(alias), Value.Immediate(7), 32, AtomicSpace.Generic),
      Instruction.Load("%r3", Value.Immediate(explicit), 32, false, MemoryAccess.Shared),
      Instruction.Store(Value.Immediate(explicit), Value.Immediate(42), 32, MemoryAccess.Shared),
      Instruction.Load("%r4", Value.Immediate(alias), 32, false, MemoryAccess.Generic(false)),
      Instruction.StoreVector(
        Value.Immediate(output.raw),
        Vec(
          Value.Register("%r0"),
          Value.Register("%r1"),
          Value.Register("%r2"),
          Value.Register("%r3"),
          Value.Register("%r4")
        ),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(execute(program, memory), Right(()))
    assertEquals(
      Vec.tabulate(5)(index => memory.backing.readInt32(output.offset(index.toLong * 4)).toOption.get),
      Vec(11, 22, 11, 18, 42)
    )
  }

  test("shared-window bounds exclude other cluster CTAs and straddling or overflowing accesses") {
    val base  = VMAddressSpace.SharedWindowBase
    val limit = base + VMAddressSpace.SharedWindowSize
    assert(VMAddressSpace.isShared(base))
    assert(VMAddressSpace.isShared(limit - 8, 8))
    for (address <- Vec(base - 1, limit, limit - 4, Long.MaxValue)) {
      assert(!VMAddressSpace.isShared(address, 8))
      val memory     = ObservedMemory()
      val operations = Vec(
        Instruction.Load("%r0", Value.Immediate(address), 64, false, MemoryAccess.Generic(false)),
        Instruction.Store(Value.Immediate(address), Value.Immediate(1), 64, MemoryAccess.Generic(false)),
        Instruction.AtomicAdd("%r0", Value.Immediate(address), Value.Immediate(1), 64, AtomicSpace.Generic)
      )
      operations.foreach { operation =>
        assert(execute(Vec(operation, Instruction.Return), memory).left.toOption.exists {
          case VMFault.Memory(_) => true
          case _                 => false
        })
      }
    }
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

  test("parameter buffers retain exact boundaries across imports, allocation and state reuse") {
    val state = VMBlockState(1, VMLaneMask.all(1).toOption.get)
    state.importParameterBuffers(Map(16L -> Array[Byte](1, 2), 64L -> Array[Byte](3, 4)))
    assertEquals(state.readParameterBuffer(16L, 2), Some(513L))
    assertEquals(state.readParameterBuffer(17L, 2), None)
    assertEquals(state.readParameterBuffer(18L, 0), Some(0L))
    assertEquals(state.readParameterBuffer(32L, 1), None)
    assertEquals(state.readParameterBuffer(64L, 2), Some(1027L))
    state.bindParameterBuffer("argument", Array[Byte](5, 6))
    val argument = state.parameterAddresses("argument")
    assertEquals(state.readParameterBuffer(argument, 2), Some(1541L))
    assertEquals(state.readParameterBuffer(argument + 2, 1), None)
    assertEquals(state.readParameterBuffer(Long.MaxValue, 1), None)
    state.reset(VMLaneMask.all(1).toOption.get)
    assertEquals(state.readParameterBuffer(16L, 1), None)
    assertEquals(state.readParameterBuffer(argument, 1), None)
    state.bindParameterBuffer("next", Array[Byte](7))
    assertEquals(state.readParameterBuffer(state.parameterAddresses("next"), 1), Some(7L))
  }

  test("parameter byte storage preserves overlap, growth, zero fill and lane isolation") {
    val state = VMBlockState(2, VMLaneMask.all(2).toOption.get)
    state.writeParameterBytes(0, "argument", 4, 32, 0x44332211L)
    assertEquals(state.readParameterBytes(0, "argument", 0, 8), 0x4433221100000000L)
    state.writeParameterBytes(0, "argument", 6, 32, 0x88776655L)
    assertEquals(state.readParameterBytes(0, "argument", 4, 8), 0x887766552211L)
    state.writeParameterBytes(0, "argument", 5, 16, 0xbbaaL)
    assertEquals(state.readParameterBytes(0, "argument", 4, 8), 0x887766bbaa11L)
    assertEquals(state.readParameterBytes(0, "argument", 16, 8), 0L)
    assertEquals(state.readParameterBytes(1, "argument", 0, 8), 0L)
    assertEquals(state.readParameterBytes(0, "missing", 0, 8), 0L)
    val _ = intercept[ArithmeticException](state.writeParameterBytes(0, "argument", Int.MaxValue, 64, 1L))
    val _ = intercept[ArithmeticException](state.readParameterBytes(0, "argument", Int.MaxValue, 8))
    state.reset(VMLaneMask.all(2).toOption.get)
    assertEquals(state.readParameterBytes(0, "argument", 4, 8), 0L)
  }

}

package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import programs.*
import scala.collection.mutable

class VMMemoryAccessSuite extends munit.FunSuite {
  private val sharedBase = VMAddressSpace.SharedBase
  private val allocation = AllocationId(1)

  private class ObservedMemory extends RegionMemory(AllocationId(1) -> 64) {
    val base: GuestAddress                     = GuestAddress(RegionMemory.Base)
    val scalarWrites: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty
    val systemWrites: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer.empty
    val systemReads: mutable.ArrayBuffer[Int]  = mutable.ArrayBuffer.empty

    override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
      scalarWrites += size.raw
      super.writeValue(address, value, size)
    }
    override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = {
      systemReads += size.raw
      super.readValue(address, size)
    }
    override def writeSystemValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
      systemWrites += size.raw
      super.writeValue(address, value, size)
    }
    def word(offset: Int >= 0): Int = getInt(allocation, offset)
  }

  test("a uniform warp unlock cannot clear a subsequent owner's lock") {
    class LockMemory extends ObservedMemory {
      override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
        super.writeValue(address, value, size).flatMap { _ =>
          if (scalarWrites.size == 1)
            atomicModifyValue(address, size)(_ => 1L).map(previous => assertEquals(previous, 0L))
          else Right(())
        }
    }
    val memory = LockMemory()
    memory.putInt(allocation, 0, 1)
    val instructions = Vec(
      Instruction.Store(Value.Immediate(memory.base.raw), Value.Immediate(0), 32, MemoryAccess.Global(false)),
      Instruction.Return
    )
    assertEquals(execute(instructions, memory, threads = 32), Right(()))
    assertEquals(memory.word(0), 1)
  }

  test("uniform stores from distinct warps retain separate transactions") {
    val memory       = ObservedMemory()
    val instructions = Vec(
      Instruction.Store(
        Value.Immediate(memory.base.raw),
        Value.Immediate(7),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(execute(instructions, memory, threads = 64), Right(()))
    assertEquals(Vec.from(memory.scalarWrites), Vec(4, 4))
    assertEquals(Right(memory.word(0)), Right(7))
  }

  test("shared, local, and generic vector accesses preserve their address routing") {
    val memory  = ObservedMemory()
    val base    = memory.base
    val program = Vec(
      Instruction.Move(r(0), Value.Immediate(11), 32),
      Instruction.Move(r(1), Value.Immediate(22), 32),
      Instruction.StoreVector(
        Value.Immediate(sharedBase),
        Vec(register(0), register(1)),
        32,
        MemoryAccess.Shared
      ),
      Instruction.LoadVector(Vec(r(2), r(3)), Value.Immediate(sharedBase), 32, false, MemoryAccess.Shared),
      Instruction.Store(Value.Immediate(base.raw), register(2), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 4L), register(3), 32, MemoryAccess.Global(false)),
      Instruction.StoreVector(
        Value.Immediate(0),
        Vec(register(0), register(1)),
        32,
        MemoryAccess.Local
      ),
      Instruction.LoadVector(Vec(r(4), r(5)), Value.Immediate(0), 32, false, MemoryAccess.Local),
      Instruction.Store(Value.Immediate(base.raw + 8L), register(4), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 12L), register(5), 32, MemoryAccess.Global(false)),
      Instruction.StoreVector(
        Value.Immediate(sharedBase + 8L),
        Vec(register(0), register(1)),
        32,
        MemoryAccess.Generic(false)
      ),
      Instruction.LoadVector(
        Vec(r(6), r(7)),
        Value.Immediate(sharedBase + 8L),
        32,
        false,
        MemoryAccess.Generic(false)
      ),
      Instruction.Store(Value.Immediate(base.raw + 16L), register(6), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 20L), register(7), 32, MemoryAccess.Global(false)),
      Instruction.StoreVector(
        Value.Immediate(8),
        Vec(register(0), register(1)),
        32,
        MemoryAccess.Generic(false)
      ),
      Instruction.LoadVector(Vec(r(8), r(9)), Value.Immediate(8), 32, false, MemoryAccess.Generic(false)),
      Instruction.Store(Value.Immediate(base.raw + 24L), register(8), 32, MemoryAccess.Global(false)),
      Instruction.Store(Value.Immediate(base.raw + 28L), register(9), 32, MemoryAccess.Global(false)),
      Instruction.Return
    )
    assertEquals(execute(program, memory), Right(()))
    assertEquals(
      Vec.tabulate(8)(index => memory.word((index * 4).unsafe[Int >= 0])),
      Vec(11, 22, 11, 22, 11, 22, 11, 22)
    )
  }

  test("shared-window generic loads, stores and atomics alias explicit shared storage") {
    val memory   = ObservedMemory()
    val output   = memory.base
    val alias    = VMAddressSpace.SharedWindowBase + 16L
    val explicit = sharedBase + 16L
    val program  = Vec(
      Instruction.StoreVector(
        Value.Immediate(alias),
        Vec(Value.Immediate(11), Value.Immediate(22)),
        32,
        MemoryAccess.Generic(false)
      ),
      Instruction.LoadVector(Vec(r(0), r(1)), Value.Immediate(explicit), 32, false, MemoryAccess.Shared),
      Instruction.Atomic(
        Some(r(2)),
        Value.Immediate(alias),
        Value.Immediate(7),
        32,
        MemorySpace.Generic,
        AtomicOperation.Add
      ),
      Instruction.Load(r(3), Value.Immediate(explicit), 32, false, MemoryAccess.Shared),
      Instruction.Store(Value.Immediate(explicit), Value.Immediate(42), 32, MemoryAccess.Shared),
      Instruction.Load(r(4), Value.Immediate(alias), 32, false, MemoryAccess.Generic(false)),
      Instruction.StoreVector(
        Value.Immediate(output.raw),
        Vec(
          register(0),
          register(1),
          register(2),
          register(3),
          register(4)
        ),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(execute(program, memory), Right(()))
    assertEquals(
      Vec.tabulate(5)(index => memory.word((index * 4).unsafe[Int >= 0])),
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
        Instruction.Load(r(0), Value.Immediate(address), 64, false, MemoryAccess.Generic(false)),
        Instruction.Store(Value.Immediate(address), Value.Immediate(1), 64, MemoryAccess.Generic(false)),
        Instruction.Atomic(
          Some(r(0)),
          Value.Immediate(address),
          Value.Immediate(1),
          64,
          MemorySpace.Generic,
          AtomicOperation.Add
        )
      )
      operations.foreach { operation =>
        assert(execute(Vec(operation, Instruction.Return), memory).left.toOption.exists {
          case Fault.Memory(_) => true
          case _               => false
        })
      }
    }
  }

  test("global and generic vector stores retain coherent packed writes and system scope") {
    val memory = ObservedMemory()
    val base   = memory.base
    val vector = Vec(
      Instruction.Move(r(0), Value.Immediate(101), 32),
      Instruction.Move(r(1), Value.Immediate(668), 32),
      Instruction.StoreVector(
        Value.Immediate(base.raw),
        Vec(register(0), register(1)),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(execute(vector, memory), Right(()))
    assertEquals(Vec.from(memory.scalarWrites), Vec(8))

    memory.scalarWrites.clear()
    val system = Vec(
      Instruction.Move(r(0), Value.Immediate(303), 32),
      Instruction.Store(Value.Immediate(base.raw + 16L), register(0), 32, MemoryAccess.Global(true)),
      Instruction.Load(r(1), Value.Immediate(base.raw + 16L), 32, false, MemoryAccess.Global(true)),
      Instruction.Return
    )
    assertEquals(execute(system, memory), Right(()))
    assertEquals(Vec.from(memory.systemWrites), Vec(4))
    assertEquals(Vec.from(memory.systemReads), Vec(4))
    assertEquals(Right(memory.word(16)), Right(303))

    memory.systemWrites.clear()
    memory.systemReads.clear()
    val vectorSystem = Vec(
      Instruction.Move(r(0), Value.Immediate(101), 32),
      Instruction.Move(r(1), Value.Immediate(668), 32),
      Instruction.StoreVector(
        Value.Immediate(base.raw + 24L),
        Vec(register(0), register(1)),
        32,
        MemoryAccess.Global(true)
      ),
      Instruction.LoadVector(
        Vec(r(2), r(3)),
        Value.Immediate(base.raw + 24L),
        32,
        false,
        MemoryAccess.Global(true)
      ),
      Instruction.StoreVector(
        Value.Immediate(base.raw + 32L),
        Vec(register(2), register(3)),
        32,
        MemoryAccess.Generic(true)
      ),
      Instruction.LoadVector(
        Vec(r(4), r(5)),
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
    assertEquals(Right(memory.word(24)), Right(101))
    assertEquals(Right(memory.word(28)), Right(668))
    assertEquals(Right(memory.word(32)), Right(101))
    assertEquals(Right(memory.word(36)), Right(668))
  }

  test("generic parameter-buffer scalar and vector accesses stay inside the parameter buffer") {
    val memory    = ObservedMemory()
    val parameter = Value.Immediate(VMAddressSpace.ParameterBase)
    val program   = Vec(
      Instruction.Move(r(0), Value.Immediate(0x11223344L), 32),
      Instruction.Store(parameter, register(0), 32, MemoryAccess.Generic(false)),
      Instruction.Load(r(1), parameter, 32, false, MemoryAccess.Generic(true)),
      Instruction.LoadVector(Vec(r(2), r(3)), parameter, 32, false, MemoryAccess.Generic(true)),
      Instruction.StoreVector(
        Value.Immediate(memory.base.raw),
        Vec(register(1), register(2), register(3)),
        32,
        MemoryAccess.Global(false)
      ),
      Instruction.Return
    )
    assertEquals(
      execute(
        program,
        memory,
        parameters = Vec(KernelParameter("block", 0, 8)),
        arguments = Map(("block": NonEmpty[String]) -> Argument(Vec[Byte](1, 2, 3, 4, 5, 6, 7, 8).unsafe))
      ),
      Right(())
    )
    assertEquals(Vec(memory.word(0), memory.word(4), memory.word(8)), Vec(0x11223344, 0x11223344, 0x08070605))
    assertEquals(Vec.from(memory.scalarWrites), Vec(4, 4, 4))
    assert(memory.systemReads.isEmpty)
  }

  test("vector memory faults still surface when the complete range is out of bounds") {
    val memory  = ObservedMemory()
    val outside = memory.base.raw + 64L
    val program = Vec(
      Instruction.LoadVector(Vec(r(0), r(1)), Value.Immediate(outside), 32, false, MemoryAccess.Generic(false)),
      Instruction.Return
    )
    assert(execute(program, memory).left.toOption.exists {
      case Fault.Memory(_) => true
      case _               => false
    })
  }

  test("parameter buffers retain exact boundaries across allocation and workgroup reuse") {
    val workgroup = VMWorkgroup(1)
    val block     = workgroup.allocateBuffer(Array[Byte](1, 2))
    val next      = workgroup.allocateBuffer(Array[Byte](3, 4))
    assertEquals(block, VMAddressSpace.ParameterBase)
    assertEquals(workgroup.bufferBytes(block, 2).map(_.toSeq), Some(Seq[Byte](1, 2)))
    assertEquals(workgroup.buffer(block + 1, 2), None)
    assertEquals(workgroup.buffer(block + 2, 0).map(_._2), Some(2))
    assertEquals(workgroup.bufferBytes(next, 2).map(_.toSeq), Some(Seq[Byte](3, 4)))
    assertEquals(workgroup.buffer(Long.MaxValue, 1), None)
    workgroup.reset()
    assertEquals(workgroup.buffer(next, 1), None)
    assertEquals(workgroup.allocateBuffer(Array[Byte](7)), VMAddressSpace.ParameterBase)
  }

  private def executePredicated(
      instructions: Vec[PredicatedInstruction],
      memory: AddressSpace,
      threads: Int > 0
  ): Either[Fault, Unit] =
    LaunchGeometry.forLaunch(Dimensions(threads, 1, 1), Dimensions.One).left.map(Fault.InvalidRequest(_)).flatMap(
      VMInterpreter.execute(
        Kernel(Entry(Routine("test", ExecutableProgram(instructions)), Vec.empty), "PTX"),
        _,
        Map.empty,
        memory
      )
    )

  private def faultLane(result: Either[Fault, Unit]): Option[String] = result match {
    case Left(Fault.Memory(detail)) => "lane (\\d+):".r.findFirstMatchIn(detail).map(_.group(1))
    case _                          => None
  }

  test("a faulting staged store reports the lane that issued it") {
    val memory                                  = ObservedMemory()
    val outside                                 = immediate(memory.base.raw + 0x100000L)
    def on(lane: Int, instruction: Instruction) =
      PredicatedInstruction(instruction, Some(ExecutionPredicate(p(0), lane == 0)))
    val program = Vec(
      PredicatedInstruction(
        Instruction.SetPredicate(p(0), Comparison.Equal, NumericFormat.Unsigned(32), Value.LaneId, immediate(1), false),
        None
      ),
      on(1, Instruction.Load(r(0), immediate(memory.base.raw), 32, false, MemoryAccess.Global(false))),
      on(0, Instruction.Store(outside, immediate(0), 32, MemoryAccess.Global(true))),
      PredicatedInstruction(Instruction.Return, None)
    )
    assertEquals(faultLane(executePredicated(program, memory, 2)), Some("0"))
  }

  test("a faulting read preparation reports the lane whose range faulted") {
    class PreparedMemory extends ObservedMemory {
      override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
        ranges.foldLeft[Either[Fault, Unit]](Right(()))((result, range) =>
          result.flatMap(_ => locate(range._1, range._2).map(_ => ()))
        )
    }
    val memory  = PreparedMemory()
    val program = Vec(
      Instruction.IntegerBinary(IntegerOperation.MultiplyLow, r(1), Value.LaneId, immediate(0x100000L), 64),
      Instruction.IntegerBinary(IntegerOperation.Add, r(1), register(1), immediate(memory.base.raw), 64),
      Instruction.Load(r(0), register(1), 32, false, MemoryAccess.Global(false)),
      Instruction.Return
    )
    assertEquals(faultLane(execute(program, memory, threads = 2)), Some("1"))
  }
}

package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class ContextHeapSuite extends Suite {
  private val global = MemoryAccess.Global(system = false)

  private def heapOf(memory: RegionMemory, size: Long): (HeapRegion, AllocationId) = {
    val id = memory.allocate(size.refine[Long > 0].fold(error => fail(error.raw), identity))
    (HeapRegion(memory.address(id, 0L).fold(fault => fail(fault.message), identity), size), id)
  }

  private def word(memory: RegionMemory, id: AllocationId)(using munit.Location): Long =
    memory.readValue(id, 0L, 8: Int in 1 ~ 8).fold(fault => fail(fault.message), identity)

  private def allocate(memory: AddressSpace, region: HeapRegion, size: Long)(using munit.Location): Long =
    ContextHeap.allocate(memory, region, size).fold(fault => fail(fault.message), identity)

  test("state held in the region survives a fresh view of the same memory") {
    val memory      = RegionMemory()
    val (region, _) = heapOf(memory, 1024L)
    val block       = allocate(DeviceHeap(memory), region, 100L)
    assertEquals(ContextHeap.free(DeviceHeap(memory), region, block), Right(()))
    assertEquals(allocate(DeviceHeap(memory), region, 100L), block)
    val other = allocate(DeviceHeap(memory), region, 100L)
    assertNotEquals(other, block)
  }

  private def kernel(name: NonEmpty[String], parameters: Vec[KernelParameter], instructions: Vec[Instruction]): Kernel =
    Kernel(
      Entry(
        Routine(name, ExecutableProgram(instructions.map(PredicatedInstruction(_, None))), 0, 0),
        parameters
      ),
      "SASS"
    )

  private def load(destination: Int, offset: Long, width: Widths.ScalarBitWidth): Instruction =
    Instruction.Load(
      programs.r(destination),
      Value.Immediate(VMAddressSpace.ParameterBase + offset),
      width,
      signed = false,
      MemoryAccess.Generic(false)
    )

  private val allocator = kernel(
    "alloc",
    Vec(KernelParameter("size", 0, 8), KernelParameter("out", 8, 8, pointer = true)),
    Vec(
      load(0, 0, 64),
      load(1, 8, 64),
      Instruction.StoreParameter(Parameter(0), 0, programs.register(0), 64),
      Instruction.CallBuiltin(Builtin.Malloc, Vec(Parameter(1)), Vec(Parameter(0))),
      Instruction.LoadParameter(Vec(programs.r(2)), Parameter(1), 0, 64, signed = false),
      Instruction.Store(programs.register(1), programs.register(2), 64, global),
      Instruction.Return
    )
  )

  private val releaser = kernel(
    "release",
    Vec(KernelParameter("block", 0, 8)),
    Vec(
      load(0, 0, 64),
      Instruction.StoreParameter(Parameter(0), 0, programs.register(0), 64),
      Instruction.CallBuiltin(Builtin.Free, Vec.empty, Vec(Parameter(0))),
      Instruction.Return
    )
  )

  private def scalar(value: Long): KernelTask.Argument =
    KernelTask.Argument.Scalar(LittleEndian.toVec(value, 8).unsafe)

  private def launch(
      memory: RegionMemory,
      program: Kernel,
      arguments: Vec[KernelTask.Argument],
      heap: Option[KernelTask.DeviceHeap]
  ): Either[Fault, Unit] = {
    val task = KernelTask.validated(
      program.name,
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      deviceHeap = heap
    ).fold(message => fail(message.raw), identity)
    VMKernelLaunch.execute(
      program,
      KernelLaunch(task, TaskContext(memory, TaskControl(), _ => Right(())), Workgroups.Sequential)
    )
  }

  test("a block allocated by one launch is usable and freeable by later launches") {
    val memory  = RegionMemory()
    val (_, id) = heapOf(memory, 1024L)
    val out     = memory.allocate(8L)
    val heap    = Some(KernelTask.DeviceHeap(id, 0L, 1024L))
    assertEquals(launch(memory, allocator, Vec(scalar(100L), KernelTask.Argument.Memory(out, 0L)), heap), Right(()))
    val block = word(memory, out)
    assertNotEquals(block, 0L)
    assertEquals(memory.writeValue(GuestAddress(block), 42L, 8), Right(()))
    assertEquals(launch(memory, releaser, Vec(scalar(block)), heap), Right(()))
    assertEquals(launch(memory, allocator, Vec(scalar(100L), KernelTask.Argument.Memory(out, 0L)), heap), Right(()))
    assertEquals(word(memory, out), block)
  }

  test("device malloc returns null once the heap is exhausted") {
    val memory  = RegionMemory()
    val (_, id) = heapOf(memory, 1024L)
    val out     = memory.allocate(8L)
    val heap    = Some(KernelTask.DeviceHeap(id, 0L, 1024L))
    val first   = launch(memory, allocator, Vec(scalar(2000L), KernelTask.Argument.Memory(out, 0L)), heap)
    assertEquals(first, Right(()))
    assertEquals(word(memory, out), 0L)
  }

  test("device malloc without a heap region fails the launch") {
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    assert(launch(memory, allocator, Vec(scalar(8L), KernelTask.Argument.Memory(out, 0L)), None).isLeft)
  }
}

package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUDeviceHeapSuite extends Suite {
  import InstructionSource.{VectorRegister as Register, *}

  private inline val Lanes = 128
  private val AllocPc      = 40L
  private val FreePc       = 44L
  private val source       = codeKernel(
    target = Target("gfx803"),
    functionTargets = Map(AllocPc -> "__ockl_dm_alloc", FreePc -> "__ockl_dm_dealloc")
  )
  private val outputId = AllocationId(1)
  private val heapId   = AllocationId(2)

  private class HeapDevice(heapBytes: Long) {
    val memory: RegionMemory = RegionMemory()
    memory.allocate(outputId, Lanes * 8)
    memory.allocate(heapId, heapBytes.toInt)
    val output: Long                      = memory.address(outputId, 0L).value.raw
    val heap: Some[KernelTask.DeviceHeap] = Some(KernelTask.DeviceHeap(heapId, 0L, heapBytes.refine[Long > 0].value))

    def task(withHeap: Boolean = true): KernelTask =
      KernelTask.validated(
        "kernel",
        Dimensions(Lanes, 1, 1),
        Dimensions.One,
        Vec.empty,
        deviceHeap = if (withHeap) heap else None
      ).value

    private val slot = Vec(
      Instruction.SMove64(6, Constant(output)),
      Instruction.VBinary(VectorBinaryOperation.ShiftLeft32, 8, Constant(3), Register(0), false)
    )

    def allocate(size: Long, withHeap: Boolean = true): Either[Fault, Unit] = runIR(
      slot ++ Vec(
        Instruction.VMove(0, Constant(size)),
        Instruction.VMove(1, Constant(0)),
        Instruction.SMove64(4, Constant(AllocPc)),
        Instruction.SSwapPc(30, Scalar(4)),
        Instruction.Store(0, MemoryAddress.Global(8, Some(6), 0), 8, false),
        Instruction.End
      ),
      memory,
      source,
      task(withHeap)
    )

    def free(): Either[Fault, Unit] = runIR(
      slot ++ Vec(
        Instruction.Load(0, MemoryAddress.Global(8, Some(6), 0), 8, false, LoadPlacement.Full),
        Instruction.SMove64(4, Constant(FreePc)),
        Instruction.SSwapPc(30, Scalar(4)),
        Instruction.End
      ),
      memory,
      source,
      task()
    )

    def blocks: Vec[Long] =
      Vec.tabulate(Lanes)(lane => memory.readValue(outputId, (lane * 8L).unsafe[Long >= 0], 8: Int in 1 ~ 8).value)
  }

  test("every active lane receives its own aligned block inside the heap") {
    val device = HeapDevice(64 * 1024L)
    assertEquals(device.allocate(100L), Right(()))
    val blocks = device.blocks
    val base   = device.memory.address(heapId, 0L).value.raw
    assertEquals(blocks.toSet.size, Lanes)
    assert(blocks.forall(block => block % 16L == 0L && block >= base && block < base + 64 * 1024L))
  }

  test("blocks allocated by one launch are freed by a later launch and then reused") {
    val device = HeapDevice(64 * 1024L)
    assertEquals(device.allocate(100L), Right(()))
    val first = device.blocks
    assertEquals(device.free(), Right(()))
    assertEquals(device.allocate(100L), Right(()))
    assertEquals(device.blocks.toSet, first.toSet)
  }

  test("blocks stay allocated across launches until they are freed") {
    val device = HeapDevice(64 * 1024L)
    assertEquals(device.allocate(100L), Right(()))
    val first = device.blocks
    assertEquals(device.allocate(100L), Right(()))
    assertEquals(device.blocks.toSet.intersect(first.toSet), Set.empty[Long])
  }

  test("malloc returns null once the heap is exhausted") {
    val device = HeapDevice(1024L)
    assertEquals(device.allocate(2000L), Right(()))
    assert(device.blocks.forall(_ == 0L))
    assertEquals(device.allocate(100L), Right(()))
    assertEquals(device.blocks.count(_ != 0L), 7)
  }

  test("a launch without a heap region fails malloc") {
    val device = HeapDevice(1024L)
    assert(device.allocate(8L, withHeap = false).isLeft)
  }
}

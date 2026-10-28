package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.LockSupport
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.util.Using

/** Embedded gfx900 words shared by the JVM and Native tests. */
class GlobalWorkerSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  private val code = words(
    0xbe8400ff, 0x3000, 0xbe850080, 0xbe8600ff, 0x100, 0xbe8700ff, 0x20000, 0x8e088402, 0x7e000208,
    0xe05c1000, 0x80010100, 0xbf8c0f70, 0xbe8a00ff, 0x4000, 0xbe8b0080, 0xdc7c8000, 0xa0100, 0xbf810000
  )
  test("parallel workgroups keep staged host reads in their own waves") {
    val allocation = AllocationId(1)
    val memory     = ExclusiveReadsMemory()
    memory.allocate(allocation, 4, GuestAddress(0x8000L))
    memory.putInt(allocation, 0, 42)
    val source = codeKernel(target = Target("gfx1030"))
    val loads  = Vec.fill(16)(
      Instruction.Load(2, MemoryAddress.Global(0, None, 0), 4, signed = false, LoadPlacement.Full)
    )
    val kernel = irKernel(
      Vec(Instruction.VMove64(0, InstructionSource.Constant(0x8000L))) ++ loads :+ Instruction.End,
      source
    )
    val dispatch = KernelTask.validated(
      "parallel_reads",
      testDimensions(32),
      testDimensions(4096),
      Vec.empty
    ).value
    val workers = WorkerPool(parallelism = 4, minGroups = 1L, minWorkItems = 1L)
    try
      (0 until 4).foreach(_ => assertEquals(executeKernel(kernel, dispatch, memory, workgroups = workers), Right(())))
    finally workers.close()
    assert(!memory.shared.get, "two waves prepared reads through the same buffer at once")
  }

  test("buffer loads and wide stores use unbound ELF globals on worker threads") {
    val epochs = Seq(17, 71)
    val modes  = Seq(false, true)
    val calls  = Using.resource(PooledWorkgroups()) { workers =>
      for {
        groups   <- Vec[Int > 0](2, 3, 5, 9)
        parallel <- modes
      } {
        val input  = Array.ofDim[Byte](256)
        val output = Array.fill[Byte](256)(0x55.toByte)
        val kernel = codeKernel(
          computePgmRsrc2 = 0x80,
          kernelCodeProperties = 0x08,
          code = code,
          globalMemory = Array(0x3000L -> input, 0x4000L -> output)
        )
        val dispatch = KernelTask.validated(
          "kernel",
          Dimensions.One,
          testDimensions(groups),
          Vec.empty
        ).value
        val policy   = if (parallel) workers else Workgroups.Sequential
        val prepared = compileKernel(kernel)
        for (epoch <- epochs) {
          input.indices.foreach(i => input(i) = (i * 3 + epoch).toByte)
          java.util.Arrays.fill(output, 0x55.toByte)
          assertEquals(
            executeKernel(
              prepared,
              dispatch,
              RejectingMemory(),
              TaskControl(),
              policy
            ),
            Right(())
          )
          assert(output.take(groups.raw * 16).sameElements(input.take(groups.raw * 16)))
          assert(output.drop(groups.raw * 16).forall(_ == 0x55.toByte))
        }
      }
      workers.calls.get()
    }
    assertEquals(calls, 8)
  }

  test("external vector loads preserve every byte without exposing or mutating the owned snapshot") {
    val input    = AllocationId(1)
    val output   = AllocationId(2)
    val original = Vec.tabulate[Byte](64)(i => (i * 17 - 128).toByte)
    class Memory extends RegionMemory(input -> original.length, output -> 64) {
      val snapshots: mutable.ArrayBuffer[Vec[Byte]] = mutable.ArrayBuffer.empty[Vec[Byte]]
      override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = {
        assertEquals(id, input)
        super.read(id, offset, size).map { snapshot =>
          snapshots += snapshot
          snapshot
        }
      }
      override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = {
        assertEquals(id, output)
        super.write(id, offset, bytes)
      }
    }
    val memory = Memory()
    val _      = original.copyToArray(memory.array(input))
    val code   = words(0xc00a0100, 0x0, 0xbf8cc07f, 0x24000084, 0xdc5c8000, 0x1040000, 0xbf8c0f70, 0xdc7c8000,
      0x60100, 0xbf810000)
    val kernel   = codeKernel(kernargSize = 16, computePgmRsrc2 = 0x80, kernelCodeProperties = 0x08, code = code)
    val dispatch = KernelTask.validated(
      "kernel",
      testDimensions(4, 1, 1),
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(input, 0L),
        KernelTask.Argument.Memory(output, 0L)
      )
    ).value
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(memory.bytes(output), original)
    java.util.Arrays.fill(memory.array(output), 0.toByte)
    assertEquals(Vec.from(memory.snapshots.flatten), original)
  }

  test("packing rejects excess typed arguments and infers an empty descriptor size") {
    val end  = Vec.from(LittleEndian.toArray(0xbf810000L, 4))
    val base = codeKernel(kernargSize = 8, code = end)
    val word = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte).unsafe)
    def run(kernel: CodeObjectKernel, args: Vec[KernelTask.Argument]) = executeKernel(
      compileKernel(kernel),
      KernelTask.validated(
        "kernel",
        Dimensions.One,
        Dimensions.One,
        args
      ).value,
      RejectingMemory()
    )
    assertEquals(run(base, Vec(word)), Right(()))
    for (
      extra <- Seq(
        KernelTask.Argument.Scalar(Vec[Byte](1).unsafe),
        KernelTask.Argument.Memory(AllocationId(1), 0L),
        KernelTask.Argument.Local(4L)
      )
    )
      assert(run(base, Vec(word, extra)).isLeft)
    assert(run(base, Vec(KernelTask.Argument.Scalar(Vec.fill(9)(0.toByte).unsafe))).isLeft)
    assertEquals(run(base.copy(kernargSize = 0), Vec.empty), Right(()))
    assertEquals(run(base.copy(kernargSize = 0), Vec(word, word)), Right(()))
    assert(run(base.copy(kernargSize = 0), Vec(KernelTask.Argument.Local(4L))).isLeft)
    assertEquals(
      VMInterpreter.effectiveKernargSize(
        base.copy(kernargSize = 0),
        Vec(KernelTask.Argument.Memory(AllocationId(1), 0L), KernelTask.Argument.Scalar(Vec.fill(12)(0.toByte).unsafe))
      ).map(_.raw),
      Right(20)
    )
  }

  /** Memory that holds each batch of staged reads for a moment, and notes a batch two waves hand it at once. */
  private class ExclusiveReadsMemory extends RegionMemory {
    private val preparing     = ConcurrentHashMap.newKeySet[AddressSpace.ReadRanges]()
    val shared: AtomicBoolean = AtomicBoolean(false)

    override def prepareReads(ranges: AddressSpace.ReadRanges): Either[Fault, Unit] = {
      if (!preparing.add(ranges)) shared.set(true)
      LockSupport.parkNanos(20_000L)
      val _ = preparing.remove(ranges)
      super.prepareReads(ranges)
    }
  }

}

package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import fixtures.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Using

/** Embedded gfx900 words shared by the JVM and Native tests. */
class GlobalWorkerSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  private val code = Vec[Long](
    0xbe8400ffL, 0x3000L, 0xbe850080L, 0xbe8600ffL, 0x100L, 0xbe8700ffL, 0x20000L, 0x8e088402L, 0x7e000208L,
    0xe05c1000L, 0x80010100L, 0xbf8c0f70L, 0xbe8a00ffL, 0x4000L, 0xbe8b0080L, 0xdc7c8000L, 0xa0100L, 0xbf810000L
  ).flatMap(w => LittleEndian.toVec(w, 4))
  private object UnboundMemory extends vm.Memory with TestAddressSpace {
    override protected val regionSizes: Iterable[(AllocationId, Long)]                      = Seq.empty
    def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Left(Fault.Backend("unexpected external memory read"))
    def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      Left(Fault.Backend("unexpected external memory write"))
  }
  test("buffer loads and wide stores use unbound ELF globals on worker threads") {
    class Workers extends Workgroups with AutoCloseable {
      val parallelism: Int > 0 = 4
      val calls: AtomicInteger = AtomicInteger()
      private val executor     = Executors.newFixedThreadPool(parallelism.raw)
      override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks.raw > 1
      def parallelFor(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit])
          : Either[Fault, Unit] = {
        calls.incrementAndGet()
        val futures = Vec.newBuilder[java.util.concurrent.Future[Either[Fault, Unit]]]
        (0L until tasks.raw).foreach(i =>
          futures += executor.submit[Either[Fault, Unit]](() => task(i.unsafe[Long >= 0]))
        )
        futures.result().foldLeft[Either[Fault, Unit]](Right(()))((r, f) =>
          r.flatMap(_ => f.get(5, TimeUnit.SECONDS))
        )
      }
      override def close(): Unit = {
        val _ = executor.shutdownNow()
        assert(executor.awaitTermination(5, TimeUnit.SECONDS))
      }
    }
    val calls = Using.resource(Workers()) { workers =>
      for {
        groups   <- Vec[Int > 0](2, 3, 5, 9)
        parallel <- Seq(false, true)
      } {
        val input  = Array.ofDim[Byte](256)
        val output = Array.fill[Byte](256)(0x55.toByte)
        val kernel = CodeObjectKernel(
          name = "kernel",
          target = Target("gfx900"),
          kernargSize = 0,
          groupSegmentSize = 0,
          privateSegmentSize = 0,
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
        ).toOption.get
        val policy   = if (parallel) workers else Workgroups.Sequential
        val prepared = VMCompiler().compile(kernel).toOption.get
        for (epoch <- Seq(17, 71)) {
          input.indices.foreach(i => input(i) = (i * 3 + epoch).toByte)
          java.util.Arrays.fill(output, 0x55.toByte)
          assertEquals(
            executeKernel(
              prepared,
              dispatch,
              UnboundMemory,
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
    // Captured snapshots are mutable observation state confined to this local final fixture.
    class Memory extends vm.Memory with TestAddressSpace {
      val destination: Array[Byte]                                       = Array.ofDim[Byte](64)
      val snapshots: ArrayBuffer[Vec[Byte]]                              = mutable.ArrayBuffer.empty[Vec[Byte]]
      override protected val regionSizes: Iterable[(AllocationId, Long)] =
        Seq(input -> original.length.toLong, output -> destination.length.toLong)
      def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = {
        assertEquals(id, input)
        val snapshot = original.slice(offset.raw.toInt, offset.raw.toInt + size.raw)
        snapshots += snapshot
        Right(snapshot)
      }
      def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = {
        assertEquals(id, output)
        val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.size)
        Right(())
      }
    }
    val memory = Memory()
    val code = Vec[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toVec(w, 4))
    val kernel = CodeObjectKernel(
      name = "kernel",
      target = Target("gfx900"),
      kernargSize = 16,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x08,
      code = code
    )
    val dispatch = KernelTask.validated(
      "kernel",
      testDimensions(4, 1, 1),
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(input, 0L),
        KernelTask.Argument.Memory(output, 0L)
      )
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(Vec.from(memory.destination), original)
    java.util.Arrays.fill(memory.destination, 0.toByte)
    assertEquals(Vec.from(memory.snapshots.flatten), original)
  }

  test("packing rejects excess typed arguments while preserving inferred linked descriptors") {
    val end  = Vec.from(LittleEndian.toArray(0xbf810000L, 4))
    val base = CodeObjectKernel(
      name = "kernel",
      target = Target("gfx900"),
      kernargSize = 8,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = end
    )
    val word = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))
    def run(kernel: CodeObjectKernel, args: Vec[KernelTask.Argument]) = executeKernel(
      compileKernel(kernel),
      KernelTask.validated(
        "kernel",
        Dimensions.One,
        Dimensions.One,
        args
      ).toOption.get,
      UnboundMemory
    )
    assertEquals(run(base, Vec(word)), Right(()))
    for (
      extra <- Seq(
        KernelTask.Argument.Scalar(Vec[Byte](1)),
        KernelTask.Argument.Memory(AllocationId(1), 0L),
        KernelTask.Argument.Local(4L)
      )
    ) {
      assert(run(base, Vec(word, extra)).isLeft)
      assert(run(base.copy(kernargSize = 0), Vec(extra)).isLeft)
    }
    assert(run(base, Vec(KernelTask.Argument.Scalar(Vec.fill(9)(0.toByte)))).isLeft)
    assertEquals(run(base.copy(kernargSize = 0), Vec.empty), Right(()))
    assertEquals(run(base.copy(kernargSize = 0, codeAddress = 0x1000), Vec(word, word)), Right(()))
  }
}

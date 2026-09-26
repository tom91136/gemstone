package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
import java.util.concurrent.{Executors, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable
import scala.util.Using

/** Embedded gfx900 words shared by the JVM and Native tests. */
final class GlobalWorkerSuite extends munit.FunSuite with CodeObjectTestSupport {
  private val code = Vec[Long](
    0xbe8400ffL, 0x3000L, 0xbe850080L, 0xbe8600ffL, 0x100L, 0xbe8700ffL, 0x20000L, 0x8e088402L, 0x7e000208L,
    0xe05c1000L, 0x80010100L, 0xbf8c0f70L, 0xbe8a00ffL, 0x4000L, 0xbe8b0080L, 0xdc7c8000L, 0xa0100L, 0xbf810000L
  ).flatMap(w => LittleEndian.toVec(w, ScalarByteWidth.Dword))
  private object UnboundMemory extends Memory with TestAddressSpace {
    override protected val regionSizes: Iterable[(MemoryRegion, Long)]            = Seq.empty
    def read(id: MemoryRegion, offset: Long :| Positive0, size: Int :| Positive0) =
      Left(Fault.Backend("unexpected external memory read"))
    def write(id: MemoryRegion, offset: Long :| Positive0, bytes: Vec[Byte]) =
      Left(Fault.Backend("unexpected external memory write"))
  }
  test("buffer loads and wide stores use unbound ELF globals on worker threads") {
    final class Workers extends Scheduler with AutoCloseable {
      val parallelism: Int :| Positive = 4
      val calls                        = AtomicInteger()
      private val executor             = Executors.newFixedThreadPool(parallelism)
      override def shouldParallel(tasks: Long :| Positive0, workItems: Long :| Positive0) = tasks > 1
      def parallelFor(tasks: Long :| Positive0)(task: Long :| Positive0 => Either[Fault, Unit])
          : Either[Fault, Unit] = {
        calls.incrementAndGet()
        val futures = Vec.newBuilder[java.util.concurrent.Future[Either[Fault, Unit]]]
        (0L until tasks).foreach(i =>
          futures += executor.submit[Either[Fault, Unit]](() => task(i.refineUnsafe[Positive0]))
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
        groups   <- Seq(2, 3, 5, 9)
        parallel <- Seq(false, true)
      } {
        val input  = Array.ofDim[Byte](256)
        val output = Array.fill[Byte](256)(0x55.toByte)
        val kernel = CodeObjectKernel(
          "kernel",
          Target("gfx900"),
          0,
          0,
          0,
          0x80,
          0x08,
          code,
          globalMemory = Array(0x3000L -> input, 0x4000L -> output)
        )
        val dispatch = KernelTask.validated(
          KernelName("kernel"),
          Dimensions.One,
          testDimensions(groups),
          Vec.empty
        ).toOption.get
        val policy   = if (parallel) workers else Scheduler.Sequential
        val prepared = VMCompiler.compile(kernel).toOption.get
        for (epoch <- Seq(17, 71)) {
          input.indices.foreach(i => input(i) = (i * 3 + epoch).toByte)
          java.util.Arrays.fill(output, 0x55.toByte)
          assertEquals(
            executeKernel(
              prepared,
              dispatch,
              UnboundMemory,
              TaskControl.forScheduler(scheduler = policy),
              policy
            ),
            Right(())
          )
          assert(output.take(groups * 16).sameElements(input.take(groups * 16)))
          assert(output.drop(groups * 16).forall(_ == 0x55.toByte))
        }
      }
      workers.calls.get()
    }
    assertEquals(calls, 8)
  }

  test("external vector loads preserve every byte without exposing or mutating the owned snapshot") {
    val input    = MemoryRegion(1)
    val output   = MemoryRegion(2)
    val original = Vec.tabulate[Byte](64)(i => (i * 17 - 128).toByte)
    // Captured snapshots are mutable observation state confined to this local final fixture.
    final class Memory extends net.kurobako.gemstone.vm.Memory with TestAddressSpace {
      val destination                                                    = Array.ofDim[Byte](64)
      val snapshots                                                      = mutable.ArrayBuffer.empty[Vec[Byte]]
      override protected val regionSizes: Iterable[(MemoryRegion, Long)] =
        Seq(input -> original.length.toLong, output -> destination.length.toLong)
      def read(id: MemoryRegion, offset: Long :| Positive0, size: Int :| Positive0) = {
        assertEquals(id, input)
        val snapshot = original.slice(offset.toInt, offset.toInt + size)
        snapshots += snapshot
        Right(snapshot)
      }
      def write(id: MemoryRegion, offset: Long :| Positive0, bytes: Vec[Byte]) = {
        assertEquals(id, output)
        val _ = bytes.copyToArray(destination, offset.toInt, bytes.size)
        Right(())
      }
    }
    val memory = Memory()
    val code = Vec[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toVec(w, ScalarByteWidth.Dword))
    val kernel   = CodeObjectKernel("kernel", Target("gfx900"), 16, 0, 0, 0x80, 0x08, code)
    val dispatch = KernelTask.validated(
      KernelName("kernel"),
      Dimensions(4, 1, 1),
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(input, 0), KernelTask.Argument.Memory(output, 0))
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, memory), Right(()))
    assertEquals(Vec.from(memory.destination), original)
    java.util.Arrays.fill(memory.destination, 0.toByte)
    assertEquals(Vec.from(memory.snapshots.flatten), original)
  }

  test("packing rejects excess typed arguments while preserving inferred linked descriptors") {
    val end  = Vec.from(LittleEndian.toArray(0xbf810000L, ScalarByteWidth.Dword))
    val base = CodeObjectKernel("kernel", Target("gfx900"), 8, 0, 0, 0, 0, end)
    val word = KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))
    def run(kernel: CodeObjectKernel, args: Vec[KernelTask.Argument]) = executeKernel(
      compileKernel(kernel),
      KernelTask.validated(
        KernelName("kernel"),
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
        KernelTask.Argument.Memory(MemoryRegion(1), 0),
        KernelTask.Argument.Local(4)
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

package net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import java.util.concurrent.{Callable, Executors, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger

/** gfx900 words assembled from src/test/resources/luna/global-workers.s with llvm-mc/llvm-objcopy. */
final class GlobalWorkerSuite extends munit.FunSuite {
  private val code = Col[Long](
    0xbe8400ffL, 0x3000L, 0xbe850080L, 0xbe8600ffL, 0x100L, 0xbe8700ffL, 0x20000L, 0x8e088402L, 0x7e000208L,
    0xe05c1000L, 0x80010100L, 0xbf8c0f70L, 0xbe8a00ffL, 0x4000L, 0xbe8b0080L, 0xdc7c8000L, 0xa0100L, 0xbf810000L
  ).flatMap(w => LittleEndian.toCol(w, 4))
  private object UnboundMemory extends ExecutionMemory {
    def read(id: AllocationId, offset: Long, size: Int) =
      Left(ExecutionFailure.Failed("unexpected external allocation read"))
    def write(id: AllocationId, offset: Long, bytes: Col[Byte]) =
      Left(ExecutionFailure.Failed("unexpected external allocation write"))
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  test("buffer loads and wide stores use unbound ELF globals on worker threads") {
    val executor = Executors.newFixedThreadPool(4)
    val calls    = AtomicInteger()
    final class Workers extends ExecutionParallelism {
      val parallelism                                           = 4
      override def shouldParallel(tasks: Long, workItems: Long) = tasks > 1
      def parallelFor(tasks: Long)(task: Long => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit] = {
        calls.incrementAndGet()
        val futures = (0L until tasks).map(i => executor.submit[Either[ExecutionFailure, Unit]](() => task(i)))
        futures.foldLeft[Either[ExecutionFailure, Unit]](Right(()))((r, f) =>
          r.flatMap(_ => f.get(5, TimeUnit.SECONDS))
        )
      }
    }
    try
      for {
        groups   <- Seq(2, 3, 5, 9)
        parallel <- Seq(false, true)
      } {
        val input  = Array.ofDim[Byte](256)
        val output = Array.fill[Byte](256)(0x55.toByte)
        val kernel = CodeObjectKernel(
          "kernel",
          Target.unsafe("gfx900"),
          0,
          0,
          0,
          0x80,
          0x08,
          code,
          globalMemory = Array(0x3000L -> input, 0x4000L -> output)
        )
        val dispatch = KernelDispatch.create(
          ProgramId.fromLong(1).toOption.get,
          "kernel",
          Dimensions.One,
          Dimensions.create(groups, 1, 1).toOption.get,
          Col.empty
        ).toOption.get
        val policy   = if (parallel) Workers() else ExecutionParallelism.Sequential
        val prepared = DispatchInterpreter.prepare(kernel).toOption.get
        for (epoch <- Seq(17, 71)) {
          input.indices.foreach(i => input(i) = (i * 3 + epoch).toByte)
          java.util.Arrays.fill(output, 0x55.toByte)
          assertEquals(
            DispatchInterpreter.execute(
              prepared,
              dispatch,
              UnboundMemory,
              ExecutionControl.create(parallelism = policy)
            ),
            Right(())
          )
          assert(output.take(groups * 16).sameElements(input.take(groups * 16)))
          assert(output.drop(groups * 16).forall(_ == 0x55.toByte))
        }
      }
    finally {
      val _ = executor.shutdownNow()
      assert(executor.awaitTermination(5, TimeUnit.SECONDS))
    }
    assertEquals(calls.get(), 8)
  }

  test("external vector loads preserve every byte without exposing or mutating the owned snapshot") {
    val input    = AllocationId.fromLong(1).toOption.get
    val output   = AllocationId.fromLong(2).toOption.get
    val original = Col.tabulate[Byte](64)(i => (i * 17 - 128).toByte)
    final class Memory extends ExecutionMemory {
      val destination                                     = Array.ofDim[Byte](64)
      var snapshots                                       = Vector.empty[Col[Byte]]
      def read(id: AllocationId, offset: Long, size: Int) = {
        assertEquals(id, input)
        val snapshot = original.slice(offset.toInt, offset.toInt + size)
        snapshots :+= snapshot
        Right(snapshot)
      }
      def write(id: AllocationId, offset: Long, bytes: Col[Byte]) = {
        assertEquals(id, output)
        val _ = bytes.copyToArray(destination, offset.toInt, bytes.size)
        Right(())
      }
      def recordPointerRelocation(value: PointerRelocation): Unit = ()
    }
    val memory = Memory()
    val code = Col[Long](0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L,
      0x60100L, 0xbf810000L).flatMap(w => LittleEndian.toCol(w, 4))
    val kernel   = CodeObjectKernel("kernel", Target.unsafe("gfx900"), 16, 0, 0, 0x80, 0x08, code)
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1).toOption.get,
      "kernel",
      Dimensions.create(4, 1, 1).toOption.get,
      Dimensions.One,
      Col(KernelArgument.Allocation(input, 0), KernelArgument.Allocation(output, 0))
    ).toOption.get
    assertEquals(DispatchInterpreter.execute(kernel, dispatch, memory), Right(()))
    assertEquals(Col.from(memory.destination), original)
    java.util.Arrays.fill(memory.destination, 0.toByte)
    assertEquals(Col.from(memory.snapshots.flatten), original)
  }

  test("packing rejects excess typed arguments while preserving inferred linked descriptors") {
    val end  = Col.from(LittleEndian.toArray(0xbf810000L, 4))
    val base = CodeObjectKernel("kernel", Target.unsafe("gfx900"), 8, 0, 0, 0, 0, end)
    val word = KernelArgument.Scalar(Col.fill(8)(0.toByte))
    def run(kernel: CodeObjectKernel, args: Col[KernelArgument]) = DispatchInterpreter.execute(
      kernel,
      KernelDispatch.create(
        ProgramId.fromLong(1).toOption.get,
        "kernel",
        Dimensions.One,
        Dimensions.One,
        args
      ).toOption.get,
      UnboundMemory
    )
    assertEquals(run(base, Col(word)), Right(()))
    for (
      extra <- Seq(
        KernelArgument.Scalar(Col[Byte](1)),
        KernelArgument.Allocation(AllocationId.fromLong(1).toOption.get, 0),
        KernelArgument.Local(4)
      )
    ) {
      assert(run(base, Col(word, extra)).isLeft)
      assert(run(base.copy(kernargSize = 0), Col(extra)).isLeft)
    }
    assert(run(base, Col(KernelArgument.Scalar(Col.fill(9)(0.toByte)))).isLeft)
    assertEquals(run(base.copy(kernargSize = 0), Col.empty), Right(()))
    assertEquals(run(base.copy(kernargSize = 0, codeAddress = 0x1000), Col(word, word)), Right(()))
  }
}

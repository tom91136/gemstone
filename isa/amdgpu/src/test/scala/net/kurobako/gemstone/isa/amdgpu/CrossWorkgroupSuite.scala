package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian}
import net.kurobako.gemstone.execution.*
import java.util.concurrent.{Callable, CountDownLatch, Executors, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger

/** Real gfx1030 instructions, with host completion boundaries between reduction phases.
  *
  * The raw words below are assembled from src/test/resources/cross-workgroup/{publish,reduce,prefix,lds-exchange}.s:
  * llvm-mc -triple=amdgcn-amd-amdhsa -mcpu=gfx1030 -filetype=obj input.s -o input.o; llvm-objcopy --only-section=.text
  * -O binary input.o input.bin (little-endian 32-bit words).
  */
final class CrossWorkgroupSuite extends munit.FunSuite {
  private val counts                    = Vector(1, 2, 3, 5, 9) // P=4; deduplicated controls, P+1 and 2P+1.
  private val output                    = AllocationId.fromLong(1).toOption.get
  private val markers                   = AllocationId.fromLong(2).toOption.get
  private val partials                  = AllocationId.fromLong(3).toOption.get
  private val counter                   = AllocationId.fromLong(4).toOption.get
  private val tickets                   = AllocationId.fromLong(5).toOption.get
  private val prefixes                  = AllocationId.fromLong(6).toOption.get
  private def scalar(value: Int)        = KernelArgument.Scalar(LittleEndian.toCol(value.toLong, 4))
  private def pointer(id: AllocationId) = KernelArgument.Allocation(id, 0L)
  private def prepared(name: String, size: Int, code: Col[Long]) =
    DispatchInterpreter.prepare(CodeObjectKernel(
      name,
      Target.unsafe("gfx1030"),
      size,
      512,
      0,
      0x80,
      0x8,
      code.flatMap(word => LittleEndian.toCol(word, 4))
    )).toOption.get
  // Write lane values through private group LDS; publish one marker before the group barrier.
  private lazy val publish = prepared(
    "publish",
    28,
    Col[Long](
      0xf4080100L, 0xfa000000L, 0xf4040200L, 0xfa000010L, 0xf4000280L, 0xfa000018L,
      0xbf8cc07fL, 0x93030902L, 0x4a020003L, 0xd4c1006aL, 0x00001101L, 0xbe8b3c6aL,
      0x4a06020aL, 0x34040082L, 0xd8340000L, 0x00000302L, 0x7d840080L, 0xbe8c3c6aL,
      0x7e0c0202L, 0x340c0c82L, 0xdc708000L, 0x00060306L, 0xbefe030cL, 0xbf8a0000L,
      0xd8d80000L, 0x04000002L, 0xbd7d0000L, 0x340a0282L, 0xdc708000L, 0x00040405L,
      0xbf810000L
    )
  )
  // One lane per group sums its completed input tile, publishes the partial, then takes an atomic ticket.
  private lazy val reduce = prepared(
    "reduce",
    40,
    Col[Long](
      0xf4080100L, 0xfa000000L, 0xf4080200L, 0xfa000010L, 0xf4040300L, 0xfa000020L,
      0xbf8cc07fL, 0x7d840080L, 0xbe8e3c6aL, 0x93030d02L, 0x808c030cL, 0x838c0d0cL,
      0x7e020203L, 0x34020282L, 0x7e040280L, 0xdc308000L, 0x03040001L, 0xbf8c3f70L,
      0x4a040702L, 0x4a020284L, 0x808c810cL, 0xbf07800cL, 0xbf85fff8L, 0x7e080202L,
      0x34080882L, 0xdc708000L, 0x00060204L, 0x7e0a0280L, 0x7e0c0281L, 0xdcc98000L,
      0x07080605L, 0xdc708000L, 0x000a0704L, 0xbf810000L
    )
  )
  // Each group independently sums completed partials through its own index; no inter-group spin.
  private lazy val prefix = prepared(
    "prefix",
    16,
    Col[Long](
      0xf4080100L, 0xfa000000L, 0xbf8cc07fL, 0x7d840080L, 0xbe883c6aL, 0x80038102L,
      0x7e020280L, 0x7e040280L, 0xdc308000L, 0x03040001L, 0xbf8c3f70L, 0x4a040702L,
      0x4a020284L, 0x80838103L, 0xbf078003L, 0xbf85fff8L, 0x7e080202L, 0x34080882L,
      0xdc708000L, 0x00060204L, 0xbf810000L
    )
  )

  for {
    parallel <- Vector(false, true)
    groups   <- counts
    width    <- Vector(31, 32, 33)
    tail     <- Vector(0, 1)
  }
    test(s"groups=$groups width=$width tail=$tail parallel=$parallel publication-reduction-LDS") {
      val n       = groups * width - tail
      val memory  = Memory(n, groups)
      val workers = Workers(memory)
      val policy  = if (parallel) workers else ExecutionParallelism.Sequential
      def run(program: DispatchInterpreter.PreparedKernel, args: Col[KernelArgument]): Unit = {
        val dispatch = KernelDispatch.create(
          ProgramId.fromLong(1).toOption.get,
          program.kernel.name,
          Dimensions.create(width, 1, 1).toOption.get,
          Dimensions.create(groups, 1, 1).toOption.get,
          args,
          globalSize = Some(Dimensions.create(n, 1, 1).toOption.get)
        ).toOption.get
        assertEquals(
          DispatchInterpreter.execute(
            program,
            dispatch,
            memory,
            ExecutionControl.create(ExecutionLimits(5000000, 64), parallelism = policy)
          ),
          Right(())
        )
      }
      try for (epoch <- Vector(7, 29)) {
          memory.reset()
          run(publish, Col(pointer(output), pointer(markers), scalar(n), scalar(width), scalar(epoch)))
          assertEquals(memory.ints(output).take(n), Vector.tabulate(n)(_ + epoch))
          assertEquals(memory.ints(output).last, -777) // Partial-final-group sentinel must remain untouched.
          assertEquals(memory.ints(markers), Vector.tabulate(groups)(g => g * width + epoch))
          val calls = workers.calls.get()
          run(
            reduce,
            Col(pointer(output), pointer(partials), pointer(counter), pointer(tickets), scalar(n), scalar(width))
          )
          // Atomic dispatches currently use the interpreter's conservative group-serialization policy.
          assertEquals(workers.calls.get(), calls)
          val expected = Vector.tabulate(groups)(g => (g * width until math.min(n, (g + 1) * width)).map(_ + epoch).sum)
          assertEquals(memory.ints(partials), expected)
          assertEquals(memory.ints(counter), Vector(groups))
          assertEquals(memory.ints(tickets).sorted, Vector.range(0, groups))
          assertEquals(memory.publications.get(), groups)
          run(prefix, Col(pointer(partials), pointer(prefixes)))
          assertEquals(memory.ints(prefixes), expected.scanLeft(0)(_ + _).tail)
          if (parallel && groups > 1) assert(workers.calls.get() >= 2)
        }
      finally workers.close()
    }

  // The earlier consumer wave must not read LDS until the later producer wave reaches the barrier.
  private lazy val ldsExchange = prepared(
    "lds_exchange",
    28,
    Col[Long](
      0xf4080100L, 0xfa000000L, 0xf4040200L, 0xfa000010L, 0xf4000280L, 0xfa000018L,
      0xbf8cc07fL, 0x93030902L, 0x4a020003L, 0x7e040280L, 0x808b8109L, 0x7d84000bL,
      0xbe8c3c6aL, 0xd5250003L, 0x0000040aL, 0xd8340000L, 0x00000302L, 0x7e0c0202L,
      0x340c0c82L, 0xdc708000L, 0x00060306L, 0xbefe030cL, 0xbf8cc07fL, 0xbf8a0000L,
      0xd8d80000L, 0x04000002L, 0xbd7d0000L, 0x340a0282L, 0xdc708000L, 0x00040405L,
      0xbf810000L
    )
  )

  for {
    parallel <- Vector(false, true)
    groups   <- counts
    width    <- Vector(33, 64, 65)
  }
    test(s"groups=$groups width=$width parallel=$parallel later-wave-LDS-producer") {
      val n       = groups * width
      val memory  = Memory(n, groups)
      val workers = Workers(memory)
      val policy  = if (parallel) workers else ExecutionParallelism.Sequential
      try for (epoch <- Vector(7, 29)) {
          memory.reset()
          val dispatch = KernelDispatch.create(
            ProgramId.fromLong(2).toOption.get,
            ldsExchange.kernel.name,
            Dimensions.create(width, 1, 1).toOption.get,
            Dimensions.create(groups, 1, 1).toOption.get,
            Col(pointer(output), pointer(markers), scalar(n), scalar(width), scalar(epoch))
          ).toOption.get
          assertEquals(
            DispatchInterpreter.execute(
              ldsExchange,
              dispatch,
              memory,
              ExecutionControl.create(ExecutionLimits(5000000, 64), parallelism = policy)
            ),
            Right(())
          )
          assertEquals(memory.ints(output).take(n), Vector.tabulate(n)(index => index / width + epoch))
          assertEquals(memory.ints(output).last, -777)
          assertEquals(memory.ints(markers), Vector.tabulate(groups)(_ + epoch))
          if (parallel && groups > 1) assert(workers.calls.get() > 0)
        }
      finally workers.close()
    }

  private final class Memory(n: Int, groups: Int) extends ExecutionMemory {
    private val lock = Object()
    private val data = Map(
      output   -> Array.ofDim[Byte]((n + 1) * 4),
      markers  -> Array.ofDim[Byte](groups * 4),
      partials -> Array.ofDim[Byte](groups * 4),
      counter  -> Array.ofDim[Byte](4),
      tickets  -> Array.ofDim[Byte](groups * 4),
      prefixes -> Array.ofDim[Byte](groups * 4)
    )
    @volatile var markerGate: Int => Unit   = _ => ()
    val publications                        = AtomicInteger()
    def ints(id: AllocationId): Vector[Int] = lock.synchronized {
      Vector.tabulate(data(id).length / 4)(i => LittleEndian.decode(data(id), i * 4, 4).toInt)
    }
    def reset(): Unit = lock.synchronized {
      data.foreach { case (id, bytes) =>
        for (i <- 0 until bytes.length / 4) LittleEndian.write(if (id == counter) 0L else -777L, bytes, i * 4, 4)
      }
      publications.set(0)
      markerGate = _ => ()
    }
    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      lock.synchronized {
        val bytes = data(id)
        if (!ByteRange.contains(bytes.length, offset, size.toLong)) Left(ExecutionFailure.InvalidRequest("read bounds"))
        else Right(Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)))
      }
    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] = {
      val result = lock.synchronized {
        val destination = data(id)
        if (!ByteRange.contains(destination.length, offset, bytes.length.toLong))
          Left(ExecutionFailure.InvalidRequest("write bounds"))
        else {
          val _ = bytes.copyToArray(destination, offset.toInt, bytes.length)
          if (id == counter) {
            val published = LittleEndian.decode(destination, 0, 4).toInt
            // Observe payload at the publication operation itself, not just after dispatch completion.
            require(ints(partials).count(_ != -777) >= published)
            val _ = publications.incrementAndGet()
          }
          Right(())
        }
      }
      if (result.isRight && id == markers) markerGate((offset / 4).toInt)
      result
    }
    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }

  private final class Workers(memory: Memory) extends ExecutionParallelism {
    val parallelism                                                = 4
    val calls                                                      = AtomicInteger()
    private val pool                                               = Executors.newFixedThreadPool(parallelism)
    override def shouldParallel(tasks: Long, lanes: Long): Boolean = tasks > 1
    override def parallelFor(tasks: Long)(task: Long => Either[ExecutionFailure, Unit])
        : Either[ExecutionFailure, Unit] = {
      calls.incrementAndGet()
      // Only each bounded resident cohort rendezvous in the test memory hook. The guest kernel never waits on another group.
      for (start <- 0 until tasks.toInt by parallelism) {
        val indices = (start until math.min(start + parallelism, tasks.toInt)).reverse
        val arrived = CountDownLatch(indices.size)
        memory.markerGate = group => {
          require(indices.contains(group))
          arrived.countDown()
          require(arrived.await(10, TimeUnit.SECONDS), "resident cohort did not publish")
        }
        val jobs = java.util.ArrayList[Callable[Either[ExecutionFailure, Unit]]]()
        indices.foreach(i => jobs.add(() => task(i.toLong)))
        val results = pool.invokeAll(jobs, 15, TimeUnit.SECONDS).iterator()
        while (results.hasNext) {
          val result = results.next().get()
          assertEquals(result, Right(()))
        }
      }
      Right(())
    }
    def close(): Unit = {
      val _ = pool.shutdownNow()
      assert(pool.awaitTermination(10, TimeUnit.SECONDS))
    }
  }
}

package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.binary.Widths.*

import net.kurobako.gemstone.Constraint.NonEmptyString

import CodeObject.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian}
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
import java.util.concurrent.{Callable, CountDownLatch, Executors, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Real gfx1030 instructions, with host completion boundaries between reduction phases.
  *
  * The raw words below are embedded little-endian gfx1030 machine code so JVM and Native tests share one fixture.
  */
final class CrossWorkgroupSuite extends munit.FunSuite with CodeObjectTestSupport {
  private val counts             = Vector(1, 2, 3, 5, 9) // P=4; deduplicated controls, P+1 and 2P+1.
  private val output             = MemoryRegion(1)
  private val markers            = MemoryRegion(2)
  private val partials           = MemoryRegion(3)
  private val counter            = MemoryRegion(4)
  private val tickets            = MemoryRegion(5)
  private val prefixes           = MemoryRegion(6)
  private def scalar(value: Int) =
    KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))
  private def pointer(region: MemoryRegion)                      = KernelTask.Argument.Memory(region, 0L)
  private def prepared(name: String, size: Int, code: Vec[Long]) =
    VMCompiler.compile(CodeObjectKernel(
      name,
      Target("gfx1030"),
      size,
      512,
      0,
      0x80,
      0x8,
      code.flatMap(word => LittleEndian.toVec(word, 4))
    )).toOption.get
  // Write lane values through private group LDS; publish one marker before the group barrier.
  private lazy val publish = prepared(
    "publish",
    28,
    Vec[Long](
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
    Vec[Long](
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
    Vec[Long](
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
      val n      = groups * width - tail
      val memory = Memory(n, groups)
      Using.resource(Workers(memory)) { workers =>
        val policy = if (parallel) workers else Scheduler.Sequential
        def run(program: VMIR.Kernel, args: Vec[KernelTask.Argument]): Unit = {
          val dispatch = KernelTask.validated(
            NonEmptyString.either(program.source.name).fold(fail(_), value => value),
            testDimensions(width),
            testDimensions(groups),
            args,
            globalSize = Some(testDimensions(n).toGlobalDimensions)
          ).toOption.get
          assertEquals(
            executeKernel(
              program,
              dispatch,
              memory,
              TaskControl.forScheduler(
                TaskLimits(5000000L, 64),
                scheduler = policy
              ),
              policy
            ),
            Right(())
          )
        }
        for (epoch <- Vector(7, 29)) {
          memory.reset()
          run(publish, Vec(pointer(output), pointer(markers), scalar(n), scalar(width), scalar(epoch)))
          assertEquals(memory.ints(output).take(n), Vector.tabulate(n)(_ + epoch))
          assertEquals(memory.ints(output).last, -777) // Partial-final-group sentinel must remain untouched.
          assertEquals(memory.ints(markers), Vector.tabulate(groups)(g => g * width + epoch))
          val calls = workers.calls.get()
          run(
            reduce,
            Vec(pointer(output), pointer(partials), pointer(counter), pointer(tickets), scalar(n), scalar(width))
          )
          assertEquals(workers.calls.get(), calls + (if (parallel && groups > 1) 1 else 0))
          val expected = Vector.tabulate(groups)(g => (g * width until math.min(n, (g + 1) * width)).map(_ + epoch).sum)
          assertEquals(memory.ints(partials), expected)
          assertEquals(memory.ints(counter), Vector(groups))
          assertEquals(memory.ints(tickets).sorted, Vector.range(0, groups))
          assertEquals(memory.publications.get(), groups)
          run(prefix, Vec(pointer(partials), pointer(prefixes)))
          assertEquals(memory.ints(prefixes), expected.scanLeft(0)(_ + _).tail)
          if (parallel && groups > 1) assert(workers.calls.get() >= 2)
        }
      }
    }

  // The earlier consumer wave must not read LDS until the later producer wave reaches the barrier.
  private lazy val ldsExchange = prepared(
    "lds_exchange",
    28,
    Vec[Long](
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
      val n      = groups * width
      val memory = Memory(n, groups)
      Using.resource(Workers(memory)) { workers =>
        val policy = if (parallel) workers else Scheduler.Sequential
        for (epoch <- Vector(7, 29)) {
          memory.reset()
          val dispatch = KernelTask.validated(
            NonEmptyString.either(ldsExchange.source.name).fold(fail(_), value => value),
            testDimensions(width),
            testDimensions(groups),
            Vec(pointer(output), pointer(markers), scalar(n), scalar(width), scalar(epoch))
          ).toOption.get
          assertEquals(
            executeKernel(
              ldsExchange,
              dispatch,
              memory,
              TaskControl.forScheduler(
                TaskLimits(5000000L, 64),
                scheduler = policy
              ),
              policy
            ),
            Right(())
          )
          assertEquals(memory.ints(output).take(n), Vector.tabulate(n)(index => index / width + epoch))
          assertEquals(memory.ints(output).last, -777)
          assertEquals(memory.ints(markers), Vector.tabulate(groups)(_ + epoch))
          if (parallel && groups > 1) assert(workers.calls.get() > 0)
        }
      }
    }

  // The marker gate is mutable coordination state confined to this final memory fixture.
  private final class Memory(n: Int, groups: Int)
      extends net.kurobako.gemstone.vm.Memory with TestAddressSpace {
    private val lock = ReentrantLock()
    private val data = Map(
      output   -> Array.ofDim[Byte]((n + 1) * 4),
      markers  -> Array.ofDim[Byte](groups * 4),
      partials -> Array.ofDim[Byte](groups * 4),
      counter  -> Array.ofDim[Byte](4),
      tickets  -> Array.ofDim[Byte](groups * 4),
      prefixes -> Array.ofDim[Byte](groups * 4)
    )
    private val markerGate                                             = AtomicReference[Int => Unit](_ => ())
    val publications                                                   = AtomicInteger()
    override protected def regionSizes: Iterable[(MemoryRegion, Long)] = data.view.mapValues(_.length.toLong)
    def installMarkerGate(gate: Int => Unit): Unit                     = markerGate.set(gate)
    def ints(id: MemoryRegion): Vector[Int]                            = lock.withLock {
      Vector.tabulate(data(id).length / 4)(i =>
        LittleEndian.decodeInt32(data(id), i * 4)
      )
    }
    def reset(): Unit = lock.withLock {
      data.foreach { case (id, bytes) =>
        for (i <- 0 until bytes.length / 4)
          LittleEndian.write(
            if (id == counter) 0L else -777L,
            bytes,
            i * 4,
            4
          )
      }
      publications.set(0)
      markerGate.set(_ => ())
    }
    override def read(
        id: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
    ): Either[Fault, Vec[Byte]] =
      lock.withLock {
        val bytes = data(id)
        if (!ByteRange.contains(bytes.length, offset.raw, size.raw.toLong))
          Left(Fault.InvalidTask("read bounds"))
        else
          Right(Vec.unsafeFromArray(
            java.util.Arrays.copyOfRange(bytes, offset.raw.toInt, offset.raw.toInt + size.raw)
          ))
      }
    override def write(
        id: MemoryRegion,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = {
      val result = lock.withLock {
        val destination = data(id)
        if (!ByteRange.contains(destination.length, offset.raw, bytes.length.toLong))
          Left(Fault.InvalidTask("write bounds"))
        else {
          val _ = bytes.copyToArray(destination, offset.raw.toInt, bytes.length)
          if (id == counter) {
            val published = LittleEndian.decodeInt32(destination, 0)
            // Observe payload at the publication operation itself, not just after dispatch completion.
            if (ints(partials).count(_ != -777) < published)
              throw AssertionError("counter was published before its partial")
            val _ = publications.incrementAndGet()
          }
          Right(())
        }
      }
      if (result.isRight && id == markers) markerGate.get()((offset.raw / 4).toInt)
      result
    }
    override def atomicModifyValue(
        id: MemoryRegion,
        offset: NonNegative[Long],
        size: IntBetween[1, 8]
    )(update: Long => Long): Either[Fault, Long] = lock.withLock {
      val destination = data(id)
      val scalarWidth = size.scalarByteWidth
      if (ByteRange.contains(destination.length, offset.raw, size.raw)) {
        val arrayOffset = offset.raw.toInt
        val previous    = LittleEndian.decode(destination, arrayOffset, scalarWidth)
        LittleEndian.write(update(previous), destination, arrayOffset, scalarWidth)
        if (id == counter) {
          val published = LittleEndian.decodeInt32(destination, 0)
          // Observe payload at the publication operation itself, not just after dispatch completion.
          if (ints(partials).count(_ != -777) < published)
            throw AssertionError("counter was published before its partial")
          val _ = publications.incrementAndGet()
        }
        Right(previous)
      } else Left(Fault.InvalidTask("atomic bounds"))
    }
  }

  private final class Workers(memory: Memory) extends Scheduler with AutoCloseable {
    val parallelism: Positive[Int] = 4
    val calls                      = AtomicInteger()
    private val pool               = Executors.newFixedThreadPool(parallelism.raw)
    override def shouldParallel(tasks: NonNegative[Long], lanes: NonNegative[Long]): Boolean = tasks.raw > 1
    override def parallelFor(tasks: NonNegative[Long])(task: NonNegative[Long] => Either[Fault, Unit])
        : Either[Fault, Unit] = {
      calls.incrementAndGet()
      // Only each bounded resident cohort rendezvous in the test memory hook. The guest kernel never waits on another group.
      for (start <- 0 until tasks.raw.toInt by parallelism.raw) {
        val indices = (start until math.min(start + parallelism.raw, tasks.raw.toInt)).reverse
        val arrived = CountDownLatch(indices.size)
        memory.installMarkerGate { group =>
          if (!indices.contains(group))
            throw AssertionError(s"group $group is outside the resident cohort $indices")
          arrived.countDown()
          if (!arrived.await(10, TimeUnit.SECONDS))
            throw AssertionError("resident cohort did not publish")
        }
        val jobs = java.util.ArrayList[Callable[Either[Fault, Unit]]]()
        indices.foreach(i => jobs.add(() => task(NonNegative.unsafe(i.toLong))))
        pool.invokeAll(jobs, 15, TimeUnit.SECONDS).asScala.view.foreach { result =>
          assertEquals(result.get(), Right(()))
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

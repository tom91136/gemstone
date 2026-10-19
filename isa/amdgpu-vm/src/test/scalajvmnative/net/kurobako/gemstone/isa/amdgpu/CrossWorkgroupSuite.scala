package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Real gfx1030 instructions, with host completion boundaries between reduction phases.
  *
  * The raw words below are embedded little-endian gfx1030 machine code so JVM and Native tests share one fixture.
  */
class CrossWorkgroupSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  private val counts             = Vec[Int > 0](1, 2, 3, 5, 9) // P=4; deduplicated controls, P+1 and 2P+1.
  private val output             = AllocationId(1)
  private val markers            = AllocationId(2)
  private val partials           = AllocationId(3)
  private val counter            = AllocationId(4)
  private val tickets            = AllocationId(5)
  private val prefixes           = AllocationId(6)
  private def scalar(value: Int) =
    KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4).unsafe)
  private def pointer(region: AllocationId)                                    = KernelTask.Argument.Memory(region, 0L)
  private def prepared(name: NonEmpty[String], size: Int >= 0, code: Vec[Int]) =
    VMCompiler().compile(codeKernel(
      name = name,
      target = Target("gfx1030"),
      kernargSize = size,
      groupSegmentSize = 512,
      computePgmRsrc2 = 0x80,
      kernelCodeProperties = 0x8 | EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32,
      code = words(code*)
    )).value
  // Write lane values through private group LDS; publish one marker before the group barrier.
  private lazy val publish = prepared(
    "publish",
    28,
    Vec(
      0xf4080100, 0xfa000000, 0xf4040200, 0xfa000010, 0xf4000280, 0xfa000018,
      0xbf8cc07f, 0x93030902, 0x4a020003, 0xd4c1006a, 0x00001101, 0xbe8b3c6a,
      0x4a06020a, 0x34040082, 0xd8340000, 0x00000302, 0x7d840080, 0xbe8c3c6a,
      0x7e0c0202, 0x340c0c82, 0xdc708000, 0x00060306, 0xbefe030c, 0xbf8a0000,
      0xd8d80000, 0x04000002, 0xbd7d0000, 0x340a0282, 0xdc708000, 0x00040405,
      0xbf810000
    )
  )
  // One lane per group sums its completed input tile, publishes the partial, then takes an atomic ticket.
  private lazy val reduce = prepared(
    "reduce",
    40,
    Vec(
      0xf4080100, 0xfa000000, 0xf4080200, 0xfa000010, 0xf4040300, 0xfa000020,
      0xbf8cc07f, 0x7d840080, 0xbe8e3c6a, 0x93030d02, 0x808c030c, 0x838c0d0c,
      0x7e020203, 0x34020282, 0x7e040280, 0xdc308000, 0x03040001, 0xbf8c3f70,
      0x4a040702, 0x4a020284, 0x808c810c, 0xbf07800c, 0xbf85fff8, 0x7e080202,
      0x34080882, 0xdc708000, 0x00060204, 0x7e0a0280, 0x7e0c0281, 0xdcc98000,
      0x07080605, 0xdc708000, 0x000a0704, 0xbf810000
    )
  )
  // Each group independently sums completed partials through its own index; no inter-group spin.
  private lazy val prefix = prepared(
    "prefix",
    16,
    Vec(
      0xf4080100, 0xfa000000, 0xbf8cc07f, 0x7d840080, 0xbe883c6a, 0x80038102,
      0x7e020280, 0x7e040280, 0xdc308000, 0x03040001, 0xbf8c3f70, 0x4a040702,
      0x4a020284, 0x80838103, 0xbf078003, 0xbf85fff8, 0x7e080202, 0x34080882,
      0xdc708000, 0x00060204, 0xbf810000
    )
  )

  private val narrowWidths = Vec[Int > 0](31, 32, 33)
  private val wideWidths   = Vec[Int > 0](33, 64, 65)

  for {
    parallel   <- Vector(false, true)
    groupCount <- counts
    widthCount <- narrowWidths
    tail       <- Vector(0, 1)
  }
    test(s"groups=$groupCount width=$widthCount tail=$tail parallel=$parallel publication-reduction-LDS") {
      val groups      = groupCount.raw
      val width       = widthCount.raw
      val globalCount = (groups * width - tail).refine[Int > 0].value
      val n           = globalCount.raw
      val memory      = Memory(n, groups)
      Using.resource(Workers(memory)) { workers =>
        val policy = if (parallel) workers else Workgroups.Sequential
        def run(program: VMIR.Kernel, args: Vec[KernelTask.Argument]): Unit = {
          val dispatch = KernelTask.validated(
            program.source.name,
            testDimensions(widthCount),
            testDimensions(groupCount),
            args,
            globalSize = Some(testDimensions(globalCount).toGlobalDimensions)
          ).value
          assertEquals(
            executeKernel(
              program,
              dispatch,
              memory,
              TaskControl(
                TaskControl.Limits(5000000L)
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
    Vec(
      0xf4080100, 0xfa000000, 0xf4040200, 0xfa000010, 0xf4000280, 0xfa000018,
      0xbf8cc07f, 0x93030902, 0x4a020003, 0x7e040280, 0x808b8109, 0x7d84000b,
      0xbe8c3c6a, 0xd5250003, 0x0000040a, 0xd8340000, 0x00000302, 0x7e0c0202,
      0x340c0c82, 0xdc708000, 0x00060306, 0xbefe030c, 0xbf8cc07f, 0xbf8a0000,
      0xd8d80000, 0x04000002, 0xbd7d0000, 0x340a0282, 0xdc708000, 0x00040405,
      0xbf810000
    )
  )

  for {
    parallel   <- Vector(false, true)
    groupCount <- counts
    widthCount <- wideWidths
  }
    test(s"groups=$groupCount width=$widthCount parallel=$parallel later-wave-LDS-producer") {
      val groups = groupCount.raw
      val width  = widthCount.raw
      val n      = (groupCount * widthCount).raw
      val memory = Memory(n, groups)
      Using.resource(Workers(memory)) { workers =>
        val policy = if (parallel) workers else Workgroups.Sequential
        for (epoch <- Vector(7, 29)) {
          memory.reset()
          val dispatch = KernelTask.validated(
            ldsExchange.source.name,
            testDimensions(widthCount),
            testDimensions(groupCount),
            Vec(pointer(output), pointer(markers), scalar(n), scalar(width), scalar(epoch))
          ).value
          assertEquals(
            executeKernel(
              ldsExchange,
              dispatch,
              memory,
              TaskControl(
                TaskControl.Limits(5000000L)
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

  // The marker gate is mutable coordination state confined to this memory fixture.
  private class Memory(n: Int, groups: Int)
      extends RegionMemory(
        output   -> (n + 1) * 4,
        markers  -> groups * 4,
        partials -> groups * 4,
        counter  -> 4,
        tickets  -> groups * 4,
        prefixes -> groups * 4
      ) {
    private val markerGate                         = AtomicReference[Int => Unit](_ => ())
    val publications: AtomicInteger                = AtomicInteger()
    def installMarkerGate(gate: Int => Unit): Unit = markerGate.set(gate)
    def ints(id: AllocationId): Vector[Int]        = exclusive {
      val bytes = array(id)
      Vector.tabulate(bytes.length / 4)(i => LittleEndian.decodeInt32(bytes, i * 4))
    }
    def reset(): Unit = exclusive {
      Seq(output, markers, partials, counter, tickets, prefixes).foreach { id =>
        val bytes = array(id)
        for (i <- 0 until bytes.length / 4) LittleEndian.write(if (id == counter) 0L else -777L, bytes, i * 4, 4)
      }
      publications.set(0)
      markerGate.set(_ => ())
    }
    // Observe payload at the publication operation itself, not just after dispatch completion.
    private def observe(id: AllocationId): Unit =
      if (id == counter) {
        if (ints(partials).count(_ != -777) < LittleEndian.decodeInt32(array(counter), 0))
          throw AssertionError("counter was published before its partial")
        val _ = publications.incrementAndGet()
      }
    override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] = {
      val result = exclusive(super.write(id, offset, bytes).map(_ => observe(id)))
      if (result.isRight && id == markers) markerGate.get()((offset.raw / 4).toInt)
      result
    }
    override def atomicModifyValue(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)(
        update: Long => Long
    ): Either[Fault, Long] = exclusive {
      super.atomicModifyValue(id, offset, size)(update).map { previous =>
        observe(id)
        previous
      }
    }
  }

  private class Workers(memory: Memory) extends PooledWorkgroups() {
    override protected def run(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit]): Either[Fault, Unit] = {
      // Only each bounded resident cohort rendezvous in the test memory hook. The guest kernel never waits on another group.
      for (start <- 0 until tasks.toInt by parallelism) {
        val indices = (start until math.min(start + parallelism, tasks.toInt)).reverse
        val arrived = CountDownLatch(indices.size)
        memory.installMarkerGate { group =>
          if (!indices.contains(group))
            throw AssertionError(s"group $group is outside the resident cohort $indices")
          arrived.countDown()
          if (!arrived.await(10, TimeUnit.SECONDS))
            throw AssertionError("resident cohort did not publish")
        }
        val jobs = java.util.ArrayList[Callable[Either[Fault, Unit]]]()
        indices.foreach(i => jobs.add(() => task(i.toLong.unsafe[Long >= 0])))
        pool.executor.invokeAll(jobs, 15, TimeUnit.SECONDS).asScala.view.foreach { result =>
          assertEquals(result.get(), Right(()))
        }
      }
      Right(())
    }
  }
}

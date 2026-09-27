package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.NonEmptyVec

import Program.*

import VMIR.*

import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.vm.{Fault, LaunchGeometry, Scheduler, TaskControl, TaskLimits}
import java.util.concurrent.{Callable, CountDownLatch, Executors, TimeUnit}
import java.util.concurrent.locks.ReentrantLock
import scala.util.Using

final class CrossWorkgroupSuite extends munit.FunSuite {
  private val groupCounts = Seq(1, 2, 3, 5, 9) // P=4: includes P+1 and 2P+1.
  private val widths      = Seq(31, 32, 33)
  private val header      = ".version 8.0\n.target sm_80\n.address_size 64\n"

  private final class Workers extends Scheduler with AutoCloseable {
    private val executor                                                            = Executors.newFixedThreadPool(4)
    override val parallelism: Positive[Int]                                         = 4
    override def shouldParallel(tasks: NonNegative[Long], lanes: NonNegative[Long]) = tasks.raw > 1
    private final class Work(
        index: NonNegative[Long],
        task: NonNegative[Long] => Either[Fault, Unit]
    ) extends Callable[Either[Fault, Unit]] {
      override def call() = task(index)
    }
    override def parallelFor(
        tasks: NonNegative[Long]
    )(task: NonNegative[Long] => Either[Fault, Unit]) = {
      val futures = (0L until tasks.raw).map(index => executor.submit(Work(NonNegative.unsafe(index), task)))
      futures.view.map(_.get(15, TimeUnit.SECONDS)).collectFirst { case Left(error) => Left(error) }
        .getOrElse(Right(()))
    }
    override def close(): Unit = {
      val _ = executor.shutdownNow()
      assert(executor.awaitTermination(5, TimeUnit.SECONDS), "test workers did not terminate")
    }
  }

  // Coordination flags and latches are confined to this final concurrent test fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private final class Memory(size: Int) extends AddressMemory {
    private val lock = ReentrantLock()
    val backing      = ArrayCheckedMemory.allocate(
      NonNegative.either(size).fold(error => throw IllegalArgumentException(error), value => value),
      GuestAddress(0x10000000000L)
    )
    val base                                                   = backing.allocation.start
    var delayPayload                                           = false
    var flagRead                                               = CountDownLatch(1)
    var delayPrefix                                            = false
    var prefixRead                                             = CountDownLatch(1)
    override def readInt8(address: GuestAddress)               = lock.withLock(backing.readInt8(address))
    override def readInt32(address: GuestAddress)              = lock.withLock(backing.readInt32(address))
    override def writeInt8(address: GuestAddress, value: Byte) = lock.withLock(backing.writeInt8(address, value))
    override def writeInt32(address: GuestAddress, value: Int) = lock.withLock(backing.writeInt32(address, value))
    override def readLittleEndian(address: GuestAddress, size: IntBetween[1, 8]) = {
      val result = lock.withLock(backing.readLittleEndian(address, size))
      if (delayPayload && address == base.offset(16) && result.contains(0L)) flagRead.countDown()
      if (delayPrefix && address == base && result.exists(value => (value & 0xffffffffL) == 0L))
        prefixRead.countDown()
      result
    }
    private def beforeWrite(address: GuestAddress): Unit = {
      if (delayPayload && address == base)
        assert(flagRead.await(10, TimeUnit.SECONDS), "consumer never attempted observation")
      if (delayPrefix && address == base)
        assert(prefixRead.await(10, TimeUnit.SECONDS), "successor never observed its pending predecessor")
    }
    override def writeLittleEndian(address: GuestAddress, value: Long, size: IntBetween[1, 8]) = {
      beforeWrite(address)
      lock.withLock(backing.writeLittleEndian(address, value, size))
    }
    override def atomicModifyLittleEndian(
        address: GuestAddress,
        size: IntBetween[1, 8]
    )(update: Long => Long) =
      lock.withLock(backing.atomicModifyLittleEndian(address, size)(update))
    override def writeBytes(address: GuestAddress, bytes: net.kurobako.gemstone.Vec[Byte]) = {
      beforeWrite(address)
      lock.withLock(backing.writeBytes(address, bytes))
    }

    def clear(): Unit = (0 until size).foreach(index => backing.writeInt8(base.offset(index), 0).toOption.get)
    def word(offset: Long, size: IntBetween[1, 8] = 4): Long =
      backing.readLittleEndian(base.offset(offset), size).toOption.get
  }

  private def kernel(body: String): Kernel =
    VMCompiler.compile(header + body).fold(error => fail(error.message), value => value)
  private def geometry(groups: Int, lanes: Int, count: Int): LaunchGeometry =
    LaunchGeometry.forLaunch(
      TestDimensions(lanes),
      TestDimensions(groups),
      Some(TestDimensions(count).toGlobalDimensions)
    ).toOption.get
  private def run(
      kernel: Kernel,
      groups: Int,
      lanes: Int,
      count: Int,
      memory: Memory,
      arguments: Map[NonEmptyString, Argument],
      workers: Scheduler
  ): Unit = {
    val result = VMInterpreter.execute(
      kernel,
      geometry(groups, lanes, count),
      arguments,
      memory,
      TaskControl.forScheduler(TaskLimits(1000000L, 32), scheduler = workers),
      workers
    )
    assert(result.isRight, result.toString)
  }
  private def withWorkers(parallel: Boolean)(body: Scheduler => Unit): Unit =
    if (!parallel) body(Scheduler.Sequential)
    else {
      Using.resource(Workers())(body)
    }

  private val publication = kernel(""".visible .entry publish(.param .u64 memory) {
    .reg .pred %p<2>;
    .reg .u32 %r<3>;
    .reg .u64 %rd<7>;
    mov.u32 %r0, %tid.x;
    setp.ne.u32 %p0, %r0, 0;
    @%p0 ret;
    ld.param.u64 %rd0, [memory];
    mov.u32 %r1, %ctaid.x;
    setp.ne.u32 %p0, %r1, 0;
    @%p0 bra observe;
    mov.u64 %rd1, 0x1122334455667788;
    mov.u64 %rd2, 0x1020304050607080;
    st.global.v2.u64 [%rd0], {%rd1, %rd2};
    mov.u32 %r2, 1;
    st.release.gpu.u32 [%rd0+16], %r2;
  observe:
    ld.acquire.gpu.u32 %r2, [%rd0+16];
    setp.eq.u32 %p1, %r2, 0;
    @%p1 bra observe;
    ld.global.v2.u64 {%rd3, %rd4}, [%rd0];
    mul.wide.u32 %rd6, %r1, 16;
    add.u64 %rd6, %rd6, %rd0;
    st.global.u64 [%rd6+24], %rd3;
    st.global.u64 [%rd6+32], %rd4;
    ret;
  }""")

  private val compactSource = """.visible .entry compact(
    .param .u64 descriptors, .param .u64 output, .param .u32 count) {
    .reg .pred %p<4>;
    .reg .u32 %r<12>;
    .reg .u64 %rd<7>;
    mov.u32 %r0, %tid.x;
    setp.ne.u32 %p0, %r0, 0;
    @%p0 ret;
    ld.param.u64 %rd0, [descriptors];
    ld.param.u64 %rd1, [output];
    ld.param.u32 %r1, [count];
    mov.u32 %r2, %ctaid.x;
    mov.u32 %r3, %ntid.x;
    mul.lo.u32 %r4, %r2, %r3;
    add.u32 %r5, %r4, %r3;
    min.u32 %r5, %r5, %r1;
    mul.wide.u32 %rd2, %r2, 8;
    add.u64 %rd2, %rd2, %rd0;
    mov.u32 %r6, 0;
    setp.eq.u32 %p1, %r2, 0;
    @%p1 bra select;
  predecessor:
    ld.acquire.gpu.v2.u32 {%r7, %r6}, [%rd2-8];
    setp.ne.u32 %p1, %r7, 101;
    @%p1 bra predecessor;
  select:
    setp.ge.u32 %p2, %r4, %r5;
    @%p2 bra publish;
    and.b32 %r8, %r4, 1;
    setp.ne.u32 %p3, %r8, 0;
    @%p3 bra next;
    mul.wide.u32 %rd3, %r6, 4;
    add.u64 %rd3, %rd3, %rd1;
    st.global.u32 [%rd3], %r4;
    add.u32 %r6, %r6, 1;
  next:
    add.u32 %r4, %r4, 1;
    bra select;
  publish:
    mov.u32 %r7, 101;
    st.release.gpu.v2.u32 [%rd2], {%r7, %r6};
    ret;
  }"""
  private val compact       = kernel(compactSource)

  private val isolationSource = """.visible .entry isolate(
    .param .u64 memory, .param .u32 epoch) {
    .shared .align 4 .b8 scratch[132];
    .reg .pred %p<2>;
    .reg .u32 %r<12>;
    .reg .u64 %rd<4>;
    ld.param.u64 %rd0, [memory];
    ld.param.u32 %r0, [epoch];
    mov.u32 %r1, %ctaid.x;
    mov.u32 %r2, %ntid.x;
    mov.u32 %r3, %tid.x;
    mad.lo.u32 %r4, %r1, %r2, %r3;
    add.u32 %r5, %r4, %r0;
    st.local.u32 [0], %r5;
    mov.u32 %r6, scratch;
    mul.lo.u32 %r7, %r3, 4;
    add.u32 %r6, %r6, %r7;
    st.shared.u32 [%r6], %r5;
    bar.sync 0;
    ld.shared.u32 %r8, [%r6];
    ld.local.u32 %r9, [0];
    setp.ne.u32 %p0, %r8, %r5;
    @%p0 trap;
    setp.ne.u32 %p1, %r9, %r5;
    @%p1 trap;
    atom.global.add.u32 %r10, [%rd0], 1;
    mul.wide.u32 %rd1, %r4, 4;
    add.u64 %rd1, %rd1, %rd0;
    st.global.u32 [%rd1+8], %r8;
    ret;
  }"""
  private val isolation       = kernel(isolationSource)
  // Atomic PTX dispatches are intentionally sequential. Exercise isolation without an atomic too.
  private val parallelIsolation = kernel(isolationSource.replace("atom.global.add.u32 %r10, [%rd0], 1;", ""))

  for {
    parallel <- Seq(false, true)
    groups   <- groupCounts
  } {
    test(s"128-bit payload plus release flag: groups=$groups parallel=$parallel") {
      withWorkers(parallel) { workers =>
        for (lanes <- widths) {
          val memory = Memory(24 + groups * 16)
          for (_ <- 0 until 2) {
            memory.clear()
            memory.flagRead = CountDownLatch(1)
            memory.delayPayload = parallel && groups > 1
            run(
              publication,
              groups,
              lanes,
              groups * lanes - lanes / 2,
              memory,
              Map(NonEmptyString("memory") -> Argument.fromLong(memory.base.raw)),
              workers
            )
            (0 until groups).foreach { group =>
              assertEquals(memory.word(24 + group * 16L, 8), 0x1122334455667788L)
              assertEquals(memory.word(32 + group * 16L, 8), 0x1020304050607080L)
            }
            if (memory.delayPayload) assertEquals(memory.flagRead.getCount, 0L)
          }
        }
      }
    }
    test(s"multi-tile prefix and stable compaction: groups=$groups parallel=$parallel") {
      withWorkers(parallel) { workers =>
        for {
          lanes <- widths
          tail  <- Seq(0, lanes / 2)
        } {
          val count  = groups * lanes - tail
          val memory = Memory(groups * 8 + count * 4)
          for (_ <- 0 until 2) {
            memory.clear()
            memory.delayPrefix = parallel && groups > 1
            memory.prefixRead = CountDownLatch(1)
            run(
              compact,
              groups,
              lanes,
              count,
              memory,
              Map(
                NonEmptyString("descriptors") -> Argument.fromLong(memory.base.raw),
                NonEmptyString("output")      -> Argument.fromLong(memory.base.raw + groups * 8L),
                NonEmptyString("count")       -> Argument(
                  NonEmptyVec.unsafeFromVec(net.kurobako.gemstone.binary.LittleEndian.toVec(
                    count,
                    4
                  ))
                )
              ),
              workers
            )
            val expected = (0 until count).filter(_ % 2 == 0)
            assertEquals(memory.word((groups - 1) * 8L + 4), expected.size.toLong)
            if (memory.delayPrefix) assertEquals(memory.prefixRead.getCount, 0L)
            expected.zipWithIndex.foreach { case (value, index) =>
              assertEquals(memory.word(groups * 8L + index * 4L), value.toLong)
            }
            (0 until groups).foreach { group =>
              assertEquals(memory.word(group * 8L), 101L)
              assertEquals(memory.word(group * 8L + 4), (Math.min(count, (group + 1) * lanes) + 1L) / 2)
            }
          }
        }
      }
    }
    test(s"4096-element CUB remove/partition prefix regression: groups=$groups parallel=$parallel") {
      withWorkers(parallel) { workers =>
        val count  = 4096
        val tile   = (count + groups - 1) / groups
        val values = (0 until count).map(index => (index * 17 + 11) % 23 - 11)
        for (retainEven <- Seq(false, true)) {
          // Same deterministic input and predicates as the previously failing CUB cases.
          // Independent output expectations use the host collection filter, not a tile scan.
          val expected = values.filter(value => (value % 2 == 0) == retainEven)
          assertEquals(expected.size, if (retainEven) 1960 else 2136)
          val source = compactSource
            .replace("mov.u32 %r3, %ntid.x;", s"mov.u32 %r3, $tile;")
            .replace(
              "and.b32 %r8, %r4, 1;",
              """mad.lo.u32 %r9, %r4, 17, 11;
              rem.u32 %r9, %r9, 23;
              add.s32 %r9, %r9, -11;
              and.b32 %r8, %r9, 1;"""
            )
            .replace("st.global.u32 [%rd3], %r4;", "st.global.u32 [%rd3], %r9;")
          val program = kernel(if (retainEven) source
          else source.replace(
            "setp.ne.u32 %p3, %r8, 0;",
            "setp.eq.u32 %p3, %r8, 0;"
          ))
          val memory = Memory(groups * 8 + count * 4)
          for (_ <- 0 until 2) {
            memory.clear()
            memory.delayPrefix = parallel && groups > 1
            memory.prefixRead = CountDownLatch(1)
            run(
              program,
              groups,
              32,
              groups * 32,
              memory,
              Map(
                NonEmptyString("descriptors") -> Argument.fromLong(memory.base.raw),
                NonEmptyString("output")      -> Argument.fromLong(memory.base.raw + groups * 8L),
                NonEmptyString("count")       -> Argument(
                  NonEmptyVec.unsafeFromVec(net.kurobako.gemstone.binary.LittleEndian.toVec(
                    count,
                    4
                  ))
                )
              ),
              workers
            )
            assertEquals(memory.word((groups - 1) * 8L + 4), expected.size.toLong)
            if (memory.delayPrefix) assertEquals(memory.prefixRead.getCount, 0L)
            expected.zipWithIndex.foreach { case (value, index) =>
              assertEquals(memory.word(groups * 8L + index * 4L), value.toLong & 0xffffffffL)
            }
          }
        }
      }
    }
    test(s"atomic counts and per-group shared/per-lane local isolation: groups=$groups parallel=$parallel") {
      withWorkers(parallel) { workers =>
        for (lanes <- widths) {
          val count  = groups * lanes - lanes / 2
          val memory = Memory(8 + count * 4)
          for (epoch <- 1 to 2) {
            run(
              parallelIsolation,
              groups,
              lanes,
              count,
              memory,
              Map(
                NonEmptyString("memory") -> Argument.fromLong(memory.base.raw),
                NonEmptyString("epoch")  -> Argument(
                  NonEmptyVec.unsafeFromVec(net.kurobako.gemstone.binary.LittleEndian.toVec(
                    (epoch + 10) * 1000,
                    4
                  ))
                )
              ),
              workers
            )
            (0 until count).foreach(index =>
              assertEquals(memory.word(8 + index * 4L), ((epoch + 10) * 1000 + index).toLong)
            )
            run(
              isolation,
              groups,
              lanes,
              count,
              memory,
              Map(
                NonEmptyString("memory") -> Argument.fromLong(memory.base.raw),
                NonEmptyString("epoch")  -> Argument(
                  NonEmptyVec.unsafeFromVec(net.kurobako.gemstone.binary.LittleEndian.toVec(
                    epoch * 1000,
                    4
                  ))
                )
              ),
              workers
            )
            assertEquals(memory.word(0), (count * epoch).toLong)
            (0 until count).foreach(index => assertEquals(memory.word(8 + index * 4L), (epoch * 1000 + index).toLong))
          }
        }
      }
    }
  }
}

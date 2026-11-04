package net.kurobako.gemstone.rpc

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

class HostSubwordAtomicSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private val encoding = ProgramEncoding.PTXText
  private val base     = 0x10000000L
  private val seed     = Vec[Byte](0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte)

  private class NativeMemory {
    private val lock    = ReentrantLock()
    private val storage = Array.tabulate[Byte](4096)(index => if (index < seed.size) seed(index) else 0.toByte)

    private def at(address: Long): Int = (address - base).toInt

    private def inside(address: Long, index: Int): Boolean = at(address) + index >= 0 &&
      at(address) + index < storage.length

    def read(address: Long, size: Int): Vec[Byte] =
      lock.withLock(Vec.tabulate(size)(index => if (inside(address, index)) storage(at(address) + index) else 0.toByte))

    def write(address: Long, values: Vec[Byte]): Unit = lock.withLock(
      values.zipWithIndex.foreach((value, index) => if (inside(address, index)) storage(at(address) + index) = value)
    )

    def value(address: Long, size: Int in 0 ~ 8): Long = LittleEndian.decode(read(address, size), 0, size)

    def exchange(atom: HostMemoryAtomic): Long = lock.withLock {
      assertEquals(atom.address.raw % atom.size, 0L)
      assert(atom.size >= 4, s"sub-word native exchange of ${atom.size} bytes")
      val observed = value(atom.address.raw, atom.size.widen[Int in 0 ~ 8])
      val applies  = atom.operation match {
        case HostMemoryAtomicOperation.CompareExchange => observed == atom.expected
        case HostMemoryAtomicOperation.Store           => true
        case HostMemoryAtomicOperation.Load            => false
      }
      if (applies) write(atom.address.raw, LittleEndian.toVec(atom.desired, atom.size.widen[Int in 0 ~ 8]))
      observed
    }
  }

  private type Body = (TaskContext, Int) => Either[Fault, Unit]

  private def run(bodies: Vector[Body], tracked: Option[Int] = None, together: Boolean = false)
      : (NativeMemory, Option[Fault]) = {
    val failure = AtomicReference(Option.empty[Fault])
    val arrived = AtomicInteger()
    val barrier = CountDownLatch(bodies.size)
    val memory  = NativeMemory()
    val count   = bodies.size.unsafe[Int > 0]
    class FakeProgram extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(0, 1, Vec.empty).left.map(Fault.InvalidRequest(_))
      override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] = {
        val index = arrived.getAndIncrement()
        if (together) { barrier.countDown(); assert(barrier.await(60L, TimeUnit.SECONDS), "dispatches did not meet") }
        bodies(index)(context, index)
      }
      override def close(): Unit = ()
    }
    class FakeBackend extends Backend {
      override val format: ProgramFormat                                  = encoding.format
      override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(FakeProgram())
    }
    Using.resource(WorkerPool(count)) { workers =>
      Using.resource(ExecutionSession(List(FakeBackend()), executionContext = workers, parallelism = count)) {
        service =>
          assertEquals(service.enableHostMemory(), Right(()))
          tracked.foreach { size =>
            val id = service.registerAllocation(size.unsafe[Int > 0]).toOption.get
            assertEquals(service.registerAllocationAddress(id, GuestAddress(base), hostBacked = true), Right(()))
          }
          val program = service.load(encoding, Vec[Byte](1)).toOption.get
          val kernel  =
            KernelTask.validated("atomics", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty)
          val completions = Vector.fill(bodies.size)(service.launch(program, kernel.toOption.get).toOption.get)
          val pumps       = completions.map { completion =>
            val pump = Thread { () =>
              @annotation.tailrec
              def drain(): Unit = {
                val batch = service.pollHostMemory(completion).getOrElse(HostMemoryBatch(Vec.empty, Vec.empty, true))
                batch.writes.foreach(write => memory.write(write.address.raw, write.bytes))
                if (batch.reads.nonEmpty)
                  assertEquals(
                    service.supplyHostMemory(
                      completion,
                      batch.reads.map(range =>
                        HostMemoryBytes(range.address, memory.read(range.address.raw, range.size.raw))
                      )
                    ),
                    Right(())
                  )
                batch.sync.foreach { sync =>
                  sync.atomicStores.foreach(memory.exchange(_))
                  val observed = sync.atomic.fold(0L)(memory.exchange(_))
                  assertEquals(service.supplyHostSync(completion, sync.ticket, observed), Right(()))
                }
                if (!batch.completed) drain()
              }
              drain()
            }
            pump.start()
            pump
          }
          pumps.foreach(_.join(60000L))
          completions.foreach(completion =>
            service.awaitCompletion(completion).left.foreach(error =>
              failure.compareAndSet(None, Some(Fault.Memory(error.message)))
            )
          )
          (memory, failure.get())
      }
    }
  }

  private def single(body: TaskContext => Either[Fault, Unit], tracked: Option[Int] = None) =
    run(Vector((context, _) => body(context)), tracked)

  private def add(context: TaskContext, address: Long, width: Int, local: Boolean)(by: Long): Either[Fault, Long] = {
    val size = width.unsafe[Int in 1 ~ 8]
    if (local) context.memory.atomicModifyLocalValue(GuestAddress(address), size)(_ + by)
    else context.memory.atomicModifyValue(GuestAddress(address), size)(_ + by)
  }

  for (local <- List(false, true)) {
    val scope = if (local) "workgroup" else "device"

    test(s"$scope-scope 8-bit atomic on host memory keeps the neighbouring bytes") {
      val observed          = AtomicReference(Vector.empty[Long])
      val (memory, failure) = single { context =>
        for {
          a <- add(context, base + 1, 1, local)(1)
          b <- add(context, base + 2, 1, local)(0xdd)
        } yield observed.set(Vector(a, b))
      }
      assertEquals(failure, None)
      assertEquals(observed.get(), Vector(0x22L, 0x33L))
      assertEquals(memory.read(base, 8), Vec[Byte](0x11, 0x23, 0x10, 0x44, 0x55, 0x66, 0x77, 0x88.toByte))
    }

    test(s"$scope-scope 16-bit atomic on host memory keeps the neighbouring bytes") {
      val observed          = AtomicReference(Vector.empty[Long])
      val (memory, failure) = single { context =>
        for {
          a <- add(context, base + 2, 2, local)(0x0101)
          b <- add(context, base + 6, 2, local)(0xffff)
        } yield observed.set(Vector(a, b))
      }
      assertEquals(failure, None)
      assertEquals(observed.get(), Vector(0x4433L, 0x8877L))
      assertEquals(memory.read(base, 8), Vec[Byte](0x11, 0x22, 0x34, 0x45, 0x55, 0x66, 0x76, 0x88.toByte))
    }

    test(s"$scope-scope 8-bit atomic wraps within its byte") {
      val (memory, failure) = single(context => add(context, base + 3, 1, local)(0xbc).map(_ => ()))
      assertEquals(failure, None)
      assertEquals(memory.read(base, 4), Vec[Byte](0x11, 0x22, 0x33, 0x00))
    }

    for (staggered <- List(false, true))
      test(s"$scope-scope sub-word atomics on host memory are shared by ${
          if (staggered) "staggered" else "simultaneous"
        } dispatches") {
        val lanes      = 3
        val rounds     = 40
        val started    = CountDownLatch(1)
        val first      = AtomicInteger()
        val failure    = AtomicReference(Option.empty[Fault])
        val body: Body = (context, index) => {
          if (staggered && index != 0) started.await()
          val threads = Vector.fill(lanes)(Thread { () =>
            (0 until rounds).foreach { _ =>
              Vector((base + 16, 2), (base + 18, 1), (base + 19, 1)).map { (address, width) =>
                add(context, address, width, local)(1)
              }.collectFirst { case Left(fault) => fault }.foreach(fault => failure.set(Some(fault)))
              if (index == 0 && first.incrementAndGet() == lanes * rounds / 2) started.countDown()
            }
          })
          threads.foreach(_.start())
          threads.foreach { thread =>
            thread.join(60000L)
            assert(!thread.isAlive, "lane did not finish")
          }
          failure.get().toLeft(())
        }
        val (memory, error) = run(Vector(body, body), together = !staggered)
        assertEquals(error, None)
        val total = 2L * lanes * rounds
        assertEquals(memory.value(base + 16, 2), total)
        assertEquals(memory.value(base + 18, 1), total)
        assertEquals(memory.value(base + 19, 1), total)
      }
  }

  test("sub-word atomic whose containing word leaves the allocation faults") {
    val outcomes    = AtomicReference(Vector.empty[Either[Fault, Long]])
    val (memory, _) = single(
      { context =>
        outcomes.set(
          Vector(
            add(context, base + 3, 1, local = false)(1),
            add(context, base + 5, 1, local = false)(1),
            add(context, base + 4, 2, local = false)(1)
          )
        )
        Right(())
      },
      tracked = Some(6)
    )
    val results = outcomes.get()
    assertEquals(results(0), Right(0x44L))
    assert(results(1).left.exists(_.message.contains("outside the allocation")), s"${results(1)}")
    assert(results(2).left.exists(_.message.contains("outside the allocation")), s"${results(2)}")
    assertEquals(memory.read(base, 8), Vec[Byte](0x11, 0x22, 0x33, 0x45, 0x55, 0x66, 0x77, 0x88.toByte))
  }
}

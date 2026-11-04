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

class HostConcurrentAtomicSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private val encoding = ProgramEncoding.PTXText
  private val base     = 0x10000000L
  private val bytes    = 4096

  private class NativeMemory {
    private val lock    = ReentrantLock()
    private val storage = Array.ofDim[Byte](bytes)

    private def at(address: Long): Int = (address - base).toInt

    def read(address: Long, size: Int): Vec[Byte] =
      lock.withLock(Vec.tabulate(size)(index => storage(at(address) + index)))

    def write(address: Long, values: Vec[Byte]): Unit = lock.withLock(
      values.zipWithIndex.foreach((value, index) => storage(at(address) + index) = value)
    )

    def word(address: Long): Long = lock.withLock(LittleEndian.decode(read(address, 8), 0, 8))

    def exchange(atom: HostMemoryAtomic): Long = lock.withLock {
      val observed = word(atom.address.raw)
      val applies  = atom.operation match {
        case HostMemoryAtomicOperation.CompareExchange => observed == atom.expected
        case HostMemoryAtomicOperation.Store           => true
        case HostMemoryAtomicOperation.Load            => false
      }
      if (applies)
        write(atom.address.raw, LittleEndian.toVec(atom.desired, atom.size.widen[Int in 0 ~ 8]))
      observed
    }
  }

  private val dispatches = 2

  private def run(lanes: Int, rounds: Int, local: Vector[Boolean], staggered: Boolean): Long = {
    val failure  = AtomicReference(Option.empty[Fault])
    val arrived  = AtomicInteger()
    val first    = AtomicInteger()
    val started  = CountDownLatch(1)
    val together = CountDownLatch(dispatches)
    val memory   = NativeMemory()
    class FakeProgram extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
        KernelMetadata.validated(0, 1, Vec.empty).left.map(Fault.InvalidRequest(_))
      override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] = {
        val index                      = arrived.getAndIncrement()
        def add(): Either[Fault, Long] =
          if (local(index)) context.memory.atomicModifyLocalValue(GuestAddress(base), 8)(_ + 1)
          else context.memory.atomicModifyValue(GuestAddress(base), 8)(_ + 1)
        def step(): Unit = {
          add().left.foreach(f => failure.set(Some(f)))
          if (index == 0 && first.incrementAndGet() == lanes * rounds / 2) started.countDown()
        }
        if (!staggered) {
          together.countDown()
          assert(together.await(60L, TimeUnit.SECONDS), "dispatches did not meet")
        } else if (index != 0) assert(started.await(60L, TimeUnit.SECONDS), "first dispatch did not start")
        val threads = Vector.fill(lanes)(Thread(() => (0 until rounds).foreach(_ => step())))
        threads.foreach(_.start())
        threads.foreach { thread =>
          thread.join(60000L)
          assert(!thread.isAlive, "lane did not finish")
        }
        failure.get().toLeft(())
      }
      override def close(): Unit = ()
    }
    class FakeBackend extends Backend {
      override val format: ProgramFormat                                  = encoding.format
      override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(FakeProgram())
    }
    Using.resource(WorkerPool(2)) { workers =>
      Using.resource(ExecutionSession(List(FakeBackend()), executionContext = workers, parallelism = 2)) {
        service =>
          assertEquals(service.enableHostMemory(), Right(()))
          val program = service.load(encoding, Vec[Byte](1)).toOption.get
          val kernel  =
            KernelTask.validated("atomics", LaunchGeometry.Dimensions.One, LaunchGeometry.Dimensions.One, Vec.empty)
          val completions = Vector.fill(dispatches)(service.launch(program, kernel.toOption.get).toOption.get)
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
          completions.foreach(completion => assertEquals(service.awaitCompletion(completion), Right(())))
          assertEquals(failure.get(), None)
          memory.word(base)
      }
    }
  }

  for {
    (name, local) <- List(
      "workgroup-scope"      -> Vector(true, true),
      "device-scope"         -> Vector(false, false),
      "mixed-scope"          -> Vector(true, false),
      "reversed mixed-scope" -> Vector(false, true)
    )
    staggered <- List(false, true)
  }
    test(s"$name atomics on host memory are shared by ${if (staggered) "staggered" else "simultaneous"} dispatches") {
      assertEquals(run(lanes = 3, rounds = 200, local, staggered), 2L * 3 * 200)
    }
}
